package eu.crowdliterature;

import eu.crowdliterature.model.Address;
import eu.crowdliterature.model.DateTime;
import eu.crowdliterature.model.EditablePerson;
import eu.crowdliterature.model.Event;
import eu.crowdliterature.model.EventBasics;
import eu.crowdliterature.model.EventOfMap;
import eu.crowdliterature.model.EventSeries;
import eu.crowdliterature.model.EventSeriesOfEvent;
import eu.crowdliterature.model.Institution;
import eu.crowdliterature.model.InstitutionOfAddress;
import eu.crowdliterature.model.InstitutionOfMap;
import eu.crowdliterature.model.InstitutionOfPerson;
import eu.crowdliterature.model.InstitutionOfWork;
import eu.crowdliterature.model.Person;
import eu.crowdliterature.model.PersonOfEvent;
import eu.crowdliterature.model.PersonOfInstitution;
import eu.crowdliterature.model.PersonOfMap;
import eu.crowdliterature.model.PersonOfWork;
import eu.crowdliterature.model.PhoneNumber;
import eu.crowdliterature.model.Translation;
import eu.crowdliterature.model.Work;
import eu.crowdliterature.model.WorkOfPerson;

import de.deepamehta.core.ChildTopics;
import de.deepamehta.core.RelatedTopic;
import de.deepamehta.core.Topic;
import de.deepamehta.core.model.AssociationModel;
import de.deepamehta.core.model.SimpleValue;
import de.deepamehta.core.osgi.PluginActivator;
import de.deepamehta.core.service.Inject;
import de.deepamehta.core.service.event.PreCreateAssociationListener;
import de.deepamehta.core.util.DeepaMehtaUtils;
import de.deepamehta.accesscontrol.AccessControlService;
import de.deepamehta.contacts.ContactsService;
import de.deepamehta.events.EventsService;
import de.deepamehta.geomaps.GeomapsService;
import de.deepamehta.geomaps.model.GeoCoordinate;
import de.deepamehta.workspaces.WorkspacesService;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import java.util.ArrayList;
import java.util.List;



@Path("/crowd")
@Produces("application/json")
public class CrowdPlugin extends PluginActivator implements CrowdService, PreCreateAssociationListener {

    // ---------------------------------------------------------------------------------------------- Instance Variables

    @Inject private ContactsService contactsService;

    @Inject private EventsService eventsService;

    @Inject private GeomapsService geomapsService;

    @Inject private WorkspacesService wsService;        // needed by migration 1

    @Inject private AccessControlService acService;     // needed by migration 1

    // -------------------------------------------------------------------------------------------------- Public Methods



    // ***********************************
    // *** CrowdService Implementation ***
    // ***********************************



    @GET
    @Path("/start_page")
    @Produces("text/html")
    @Override
    public String getStartPageContent() {
        return dm4.getTopicByUri("crowd.meet.start_page").getChildTopics().getString("dm4.notes.text");
    }

    // --- Person ---

    @GET
    @Path("/person/{id}")
    @Override
    public Person getPerson(@PathParam("id") long personId) {
        Topic person = dm4.getTopic(personId);
        ChildTopics childs = person.getChildTopics();
        return new Person(
            person.getSimpleValue().toString(),
	    getPlaceOfLiving(person),
            childs.getChildTopics("dm4.datetime.date#dm4.contacts.date_of_birth").getStringOrNull("dm4.datetime.year"),
            childs.getStringOrNull("dm4.contacts.city#crowd.person.place_of_birth"),
            childs.getString("dm4.contacts.notes"),
            getStrings(person, "dm4.webbrowser.url"),
            getStrings(person, "crowd.person.nationality"),
            getStrings(person, "crowd.language"),
            getInstitutionsOfPerson(personId),
            getWorksOfPerson(person),
            getEventsOfPerson(personId)
        );
    }

    @GET
    @Path("/persons")
    @Override
    public List<PersonOfMap> getAllPersons() {
        List<PersonOfMap> persons = new ArrayList();
        for (Topic person : dm4.getTopicsByType("dm4.contacts.person")) {
            Topic address = getAddresses(person).get(0);
            GeoCoordinate geoCoord = address != null ? geomapsService.getGeoCoordinate(address) : null;
            persons.add(new PersonOfMap(
                person.getId(),
                person.getSimpleValue().toString(),
                geoCoord
            ));

        }
        return persons;
    }

    @GET
    @Path("/editable_person/by_username/{userName}")
    @Override
    public EditablePerson getEditablePersonByUsername(@PathParam("userName") String userName) {
	Topic userNameTopic = acService.getUsernameTopic(userName);

	RelatedTopic person = userNameTopic.getRelatedTopic("dm4.core.association", null, null, "dm4.contacts.person");

	if (person == null) {
		return null;
	}

	ChildTopics childs = person.getChildTopics();
        return new EditablePerson(
	    person.getId(),
            childs.getChildTopics("dm4.contacts.person_name").getStringOrNull("dm4.contacts.first_name"),
            childs.getChildTopics("dm4.contacts.person_name").getStringOrNull("dm4.contacts.last_name"),
	    getStrings(person, "dm4.contacts.email_address"),
            childs.getChildTopics("dm4.datetime.date#dm4.contacts.date_of_birth").getStringOrNull("dm4.datetime.year"),
            childs.getStringOrNull("dm4.contacts.city#crowd.person.place_of_birth"),
            childs.getString("dm4.contacts.notes")
        );
    }

    // --- Work ---

    @GET
    @Path("/work/{id}")
    @Override
    public Work getWork(@PathParam("id") long workId) {
        Topic work = dm4.getTopic(workId);
        ChildTopics childs = work.getChildTopics();
        return new Work(
            work.getSimpleValue().toString(),
            childs.getStringOrNull("crowd.work.type"),
            childs.getStringOrNull("crowd.language"),
            childs.getStringOrNull("dm4.datetime.year#crowd.work.year_of_publication"),
            childs.getStringOrNull("dm4.contacts.city#crowd.work.place_of_publication"),
            getStrings(work, "crowd.work.genre"),
            childs.getString("crowd.work.notes"),
            childs.getStringOrNull("crowd.work.isbn"),
            childs.getStringOrNull("dm4.webbrowser.url"),
            getTranslations(work),
            getPersonsOfWork(workId),
            getInstitutionsOfWork(workId)
        );
    }

    // --- Institution ---

    @GET
    @Path("/institution/{id}")
    @Override
    public Institution getInstitution(@PathParam("id") long instId) {
        Topic inst = dm4.getTopic(instId);
        ChildTopics childs = inst.getChildTopics();
        List<RelatedTopic> addressTopics = getAddresses(inst);
        return new Institution(
            inst.getSimpleValue().toString(),
            getStrings(inst, "dm4.webbrowser.url"),
            getAddresses(addressTopics),
            getPhoneNumbers(inst),
            getStrings(inst, "dm4.contacts.email_address"),
            childs.getString("dm4.contacts.notes"),
            getPersonsOfInstitution(instId),
            getEventsOfInstitution(addressTopics)
        );
    }

    @GET
    @Path("/institutions")
    @Override
    public List<InstitutionOfMap> getAllInstitutions() {
        List<InstitutionOfMap> result = new ArrayList();
        for (Topic inst : dm4.getTopicsByType("dm4.contacts.institution")) {
            Topic address = getAddresses(inst).get(0);
            GeoCoordinate geoCoord = address != null ? geomapsService.getGeoCoordinate(address) : null;
            result.add(new InstitutionOfMap(
                inst.getId(),
                inst.getSimpleValue().toString(),
                geoCoord
            ));

        }
        return result;
    }


    // ********************************
    // *** Listener Implementations ***
    // ********************************



    @Override
    public void preCreateAssociation(AssociationModel assoc) {
        // Work <-> Person
        DeepaMehtaUtils.associationAutoTyping(assoc, "crowd.work", "dm4.contacts.person",
            "crowd.work.involvement", "dm4.core.default", "dm4.core.default", dm4);
        //
        // Work <-> Institution
        DeepaMehtaUtils.associationAutoTyping(assoc, "crowd.work", "dm4.contacts.institution",
            "crowd.work.involvement", "dm4.core.default", "dm4.core.default", dm4);
        //
        // Translation <-> Person
        DeepaMehtaUtils.associationAutoTyping(assoc, "crowd.work.translation", "dm4.contacts.person",
            "crowd.work.involvement", "dm4.core.default", "dm4.core.default", dm4);
    }



    // ------------------------------------------------------------------------------------------------- Private Methods

    // --- Person ---

    private JSONArray getInstitutionsOfPerson(long personId) {
        JSONArray institutions = null;
        List<RelatedTopic> instTopics = contactsService.getInstitutions(personId);
        if (!instTopics.isEmpty()) {
            institutions = new JSONArray();
            for (RelatedTopic inst : instTopics) {
                institutions.put(new InstitutionOfPerson(
                    inst.getId(),
                    inst.getSimpleValue().toString(),
                    inst.getRelatingAssociation().getSimpleValue().toString()
                ).toJSON());
            }
        }
        return institutions;
    }

    private JSONArray getWorksOfPerson(Topic person) {
        JSONArray works = null;
        // 1) works
        List<RelatedTopic> workTopics = person.getRelatedTopics("crowd.work.involvement", "dm4.core.default",
            "dm4.core.default", "crowd.work");
        if (!workTopics.isEmpty()) {
            works = new JSONArray();
            for (RelatedTopic work : workTopics) {
                works.put(new WorkOfPerson(
                    work.getId(),
                    work.getSimpleValue().toString(),
                    work.getRelatingAssociation().getSimpleValue().toString()
                ).toJSON());
            }
        }
        // 2) translations
        List<RelatedTopic> translations = person.getRelatedTopics("crowd.work.involvement", "dm4.core.default",
            "dm4.core.default", "crowd.work.translation");
        if (works == null && !translations.isEmpty()) {
            works = new JSONArray();
        }
        for (RelatedTopic translation : translations) {
            Topic work = getWorkOfTranslation(translation.getId());
            works.put(new WorkOfPerson(
                work.getId(),
                work.getSimpleValue().toString(),
                translation.getRelatingAssociation().getSimpleValue().toString()
            ).toJSON());
        }
        //
        return works;
    }

    private Topic getWorkOfTranslation(long translationId) {
        return dm4.getTopic(translationId).getRelatedTopic("dm4.core.composition", "dm4.core.child", "dm4.core.parent",
            "crowd.work");
    }

    private JSONArray getEventsOfPerson(long personId) {
        JSONArray events = null;

	/* not needed anymore
        List<RelatedTopic> eventTopics = eventsService.getEvents(personId);
        if (!eventTopics.isEmpty()) {
            events = new JSONArray();
            for (Topic event : eventTopics) {
                events.put(eventBasics(event));
            }
        }
	*/

        return events;
    }

    // --- Work ---

    private JSONArray getTranslations(Topic work) {
        JSONArray translations = null;
        for (Topic translation : work.getChildTopics().getTopics("crowd.work.translation")) {
            ChildTopics childs = translation.getChildTopics();
            String title    = childs.getStringOrNull("crowd.work.title");
            String language = childs.getStringOrNull("crowd.language");
            String isbn     = childs.getStringOrNull("crowd.work.isbn");
            JSONArray persons = getPersonsOfWork(translation.getId());
            // We don't want create empty Translation objects.
            // Note: the webclients creates an empty *composite*, that is when no childs exist.
            // (In contrast empty *simple* childs are never created.)
            if (title != null || language != null || isbn != null || persons != null) {
                if (translations == null) {
                    translations = new JSONArray();
                }
                translations.put(new Translation(title, language, isbn, persons).toJSON());
            }
        }
        return translations;
    }

    /**
     * @param   workId      ID of a work or a translation
     */
    private JSONArray getPersonsOfWork(long workId) {
        JSONArray persons = null;
        List<RelatedTopic> personTopics = dm4.getTopic(workId).getRelatedTopics("crowd.work.involvement",
            "dm4.core.default", "dm4.core.default", "dm4.contacts.person");
        if (!personTopics.isEmpty()) {
            persons = new JSONArray();
            for (RelatedTopic person : personTopics) {
                persons.put(new PersonOfWork(
                    person.getId(),
                    person.getSimpleValue().toString(),
                    person.getRelatingAssociation().getSimpleValue().toString()
                ).toJSON());
            }
        }
        return persons;
    }

    /**
     * @param   workId      ID of a work or a translation
     */
    private JSONArray getInstitutionsOfWork(long workId) {
        JSONArray institutions = null;
        List<RelatedTopic> instTopics = dm4.getTopic(workId).getRelatedTopics("crowd.work.involvement",
            "dm4.core.default", "dm4.core.default", "dm4.contacts.institution");
        if (!instTopics.isEmpty()) {
            institutions = new JSONArray();
            for (RelatedTopic inst : instTopics) {
                institutions.put(new InstitutionOfWork(
                    inst.getId(),
                    inst.getSimpleValue().toString(),
                    inst.getRelatingAssociation().getSimpleValue().toString()
                ).toJSON());
            }
        }
        return institutions;
    }

    // --- Event ---

    private JSONArray getParticipants(long eventId) {
        JSONArray participants = null;
        List<RelatedTopic> personTopics = eventsService.getParticipants(eventId);
        if (!personTopics.isEmpty()) {
            participants = new JSONArray();
            for (RelatedTopic person : personTopics) {
                participants.put(new PersonOfEvent(
                    person.getId(),
                    person.getSimpleValue().toString()
                ).toJSON());
            }
        }
        return participants;
    }

    private JSONArray getEventSeries(Topic event) {
        JSONArray eventSeries = null;

	/* not needed
        List<RelatedTopic> eventSeriesTopics = event.getRelatedTopics("dm4.core.association", "dm4.core.default",
            "dm4.core.default", "crowd.event_series");
        if (!eventSeriesTopics.isEmpty())  {
            eventSeries = new JSONArray();
            for (RelatedTopic eventSeriesTopic : eventSeriesTopics) {
                eventSeries.put(new EventSeriesOfEvent(
                    eventSeriesTopic.getId(),
                    eventSeriesTopic.getSimpleValue().toString()
                ).toJSON());
            }
        }
	*/

        return eventSeries;
    }

    private JSONObject getDateTime(Topic event, String assocDefUri) {
        ChildTopics dateTime = event.getChildTopics().getChildTopics(assocDefUri);
        ChildTopics date = dateTime.getChildTopics("dm4.datetime.date");
        ChildTopics time = dateTime.getChildTopics("dm4.datetime.time");
        return new DateTime(
            date.getIntOrNull("dm4.datetime.month"),
            date.getIntOrNull("dm4.datetime.day"),
            date.getIntOrNull("dm4.datetime.year"),
            time.getIntOrNull("dm4.datetime.hour"),
            time.getIntOrNull("dm4.datetime.minute")
        ).toJSON();
    }

    private JSONObject getAddress(Topic event) {
        return address(event.getChildTopics().getTopic("dm4.contacts.address"), true);  // includeInstitution=true
    }

    // --- Event Series ---

    private JSONArray getEventsOfEventSeries(Topic eventSeries) {
        JSONArray events = null;
        List<RelatedTopic> eventTopics = eventSeries.getRelatedTopics("dm4.core.association", "dm4.core.default",
            "dm4.core.default", "dm4.events.event");
        if (!eventTopics.isEmpty()) {
            events = new JSONArray();
            for (RelatedTopic event : eventTopics) {
                events.put(eventBasics(event));
            }
        }
        return events;
    }

    // --- Institution ---

    private List<RelatedTopic> getAddresses(Topic institution) {
        return institution.getChildTopics().getTopics("dm4.contacts.address#dm4.contacts.address_entry");
    }

    private String getPlaceOfLiving(Topic person) {
        String placeOfLiving = null;
        Topic address = getAddresses(person).get(0);
        return (address != null) ? address.getChildTopics().getStringOrNull("dm4.contacts.city") : null;
    }

    private JSONArray getAddresses(List<RelatedTopic> addressTopics) {
        JSONArray addresses = null;
        if (!addressTopics.isEmpty()) {
            addresses = new JSONArray();
            for (RelatedTopic address : addressTopics) {
                addresses.put(address(address, false));     // includeInstitution=false
            }
        }
        return addresses;
    }

    private JSONArray getPhoneNumbers(Topic institution) {
        JSONArray phoneNumbers = null;
        List<RelatedTopic> phoneTopics = institution.getChildTopics().getTopicsOrNull("dm4.contacts.phone_number#" +
            "dm4.contacts.phone_entry");
        if (phoneTopics != null) {
            phoneNumbers = new JSONArray();
            for (RelatedTopic phone : phoneTopics) {
                phoneNumbers.put(new PhoneNumber(
                    phone.getRelatingAssociation().getSimpleValue().toString(),
                    phone.getSimpleValue().toString()
                ).toJSON());
            }
        }
        return phoneNumbers;
    }

    private JSONArray getPersonsOfInstitution(long instId) {
        JSONArray persons = null;
        List<RelatedTopic> personTopics = contactsService.getPersons(instId);
        if (!personTopics.isEmpty()) {
            persons = new JSONArray();
            for (RelatedTopic person : personTopics) {
                persons.put(new PersonOfInstitution(
                    person.getId(),
                    person.getSimpleValue().toString(),
                    person.getRelatingAssociation().getSimpleValue().toString()
                ).toJSON());
            }
        }
        return persons;
    }

    private JSONArray getEventsOfInstitution(List<RelatedTopic> addressTopics) {
        JSONArray events = null;
	/* not needed
        for (Topic address : addressTopics) {
            List<RelatedTopic> eventTopics = address.getRelatedTopics("dm4.core.aggregation", "dm4.core.child",
                "dm4.core.parent", "dm4.events.event");
            if (!eventTopics.isEmpty()) {
                if (events == null) {
                    events = new JSONArray();
                }
                for (Topic event : eventTopics) {
                    events.put(eventBasics(event));
                }
            }
        }
	*/
        return events;
    }

    // --- Helper ---

    private JSONObject eventBasics(Topic event) {
        return new EventBasics(
            event.getId(),
            event.getSimpleValue().toString(),
            getDateTime(event, "dm4.datetime#dm4.events.from"),
            getDateTime(event, "dm4.datetime#dm4.events.to")
        ).toJSON();
    }

    private JSONObject address(RelatedTopic address, boolean includeInstitution) {
        ChildTopics childs = address.getChildTopics();
        return new Address(
            address.getRelatingAssociation().getSimpleValue().toString(),
            childs.getStringOrNull("dm4.contacts.street"),
            childs.getStringOrNull("dm4.contacts.postal_code"),
            childs.getStringOrNull("dm4.contacts.city"),
            childs.getStringOrNull("dm4.contacts.country"),
            includeInstitution ? addressInstitution(address) : null
        ).toJSON();
    }

    private JSONObject addressInstitution(Topic address) {
        Topic inst = getInstitution(address);
        return inst != null ? new InstitutionOfAddress(
            inst.getId(),
            inst.getSimpleValue().toString()
        ).toJSON() : null;
    }

    private Topic getInstitution(Topic address) {
        return address.getRelatedTopic("dm4.contacts.address_entry", "dm4.core.child", "dm4.core.parent",
            "dm4.contacts.institution");
    }

    private JSONArray getStrings(Topic topic, String assocDefUri) {
        JSONArray strings = null;
        List<RelatedTopic> topics = topic.getChildTopics().getTopicsOrNull(assocDefUri);
        if (topics != null) {
            strings = new JSONArray();
            for (Topic t : topics) {
                strings.put(t.getSimpleValue().toString());
            }
        }
        return strings;
    }
}
