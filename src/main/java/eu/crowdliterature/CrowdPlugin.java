package eu.crowdliterature;

import eu.crowdliterature.model.Address;
import eu.crowdliterature.model.Event;
import eu.crowdliterature.model.EventOfEventSeries;
import eu.crowdliterature.model.EventOfPerson;
import eu.crowdliterature.model.EventSeries;
import eu.crowdliterature.model.EventSeriesOfEvent;
import eu.crowdliterature.model.Institution;
import eu.crowdliterature.model.InstitutionOfPerson;
import eu.crowdliterature.model.InstitutionOfWork;
import eu.crowdliterature.model.Person;
import eu.crowdliterature.model.PersonOfEvent;
import eu.crowdliterature.model.PersonOfInstitution;
import eu.crowdliterature.model.PersonOfWork;
import eu.crowdliterature.model.PhoneNumber;
import eu.crowdliterature.model.Translation;
import eu.crowdliterature.model.Work;
import eu.crowdliterature.model.WorkOfPerson;

import de.deepamehta.core.ChildTopics;
import de.deepamehta.core.RelatedTopic;
import de.deepamehta.core.Topic;
import de.deepamehta.core.model.AssociationModel;
import de.deepamehta.core.osgi.PluginActivator;
import de.deepamehta.core.service.Inject;
import de.deepamehta.core.service.ResultList;
import de.deepamehta.core.service.event.PreCreateAssociationListener;
import de.deepamehta.core.util.DeepaMehtaUtils;
import de.deepamehta.plugins.contacts.ContactsService;
import de.deepamehta.plugins.events.EventsService;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import java.util.List;



@Path("/crowd")
@Produces("application/json")
public class CrowdPlugin extends PluginActivator implements CrowdService, PreCreateAssociationListener {

    // ---------------------------------------------------------------------------------------------- Instance Variables

    @Inject
    private ContactsService contactsService;

    @Inject
    private EventsService eventsService;

    // -------------------------------------------------------------------------------------------------- Public Methods



    // ***********************************
    // *** CrowdService Implementation ***
    // ***********************************



    // --- Person ---

    @GET
    @Path("/person/{id}")
    @Override
    public Person getPerson(@PathParam("id") long personId) {
        Topic person = dms.getTopic(personId);
        ChildTopics childs = person.getChildTopics();
        return new Person(
            person.getSimpleValue().toString(),
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

    // --- Work ---

    @GET
    @Path("/work/{id}")
    @Override
    public Work getWork(@PathParam("id") long workId) {
        Topic work = dms.getTopic(workId);
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

    // --- Event ---

    @GET
    @Path("/event/{id}")
    @Override
    public Event getEvent(@PathParam("id") long eventId) {
        Topic event = dms.getTopic(eventId);
        ChildTopics childs = event.getChildTopics();
        return new Event(
            event.getSimpleValue().toString(),
            childs.getString("dm4.datetime#dm4.events.from"),
            childs.getString("dm4.datetime#dm4.events.to"),
            getAddress(event),
            childs.getString("dm4.events.notes"),
            getParticipants(eventId),
            childs.getStringOrNull("crowd.event.entrance_fee"),
            childs.getStringOrNull("dm4.webbrowser.url"),
            getEventSeries(event)
        );
    }

    // --- Event Series ---

    @GET
    @Path("/event_series/{id}")
    @Override
    public EventSeries getEventSeries(@PathParam("id") long eventSeriesId) {
        Topic eventSeries = dms.getTopic(eventSeriesId);
        ChildTopics childs = eventSeries.getChildTopics();
        return new EventSeries(
            eventSeries.getSimpleValue().toString(),
            childs.getString("crowd.event_series.notes"),
            childs.getStringOrNull("dm4.webbrowser.url"),
            getEventsOfEventSeries(eventSeries)
        );
    }

    // --- Institution ---

    @GET
    @Path("/institution/{id}")
    @Override
    public Institution getInstitution(@PathParam("id") long instId) {
        Topic inst = dms.getTopic(instId);
        ChildTopics childs = inst.getChildTopics();
        return new Institution(
            inst.getSimpleValue().toString(),
            getStrings(inst, "dm4.webbrowser.url"),
            getAddresses(inst),
            getPhoneNumbers(inst),
            getStrings(inst, "dm4.contacts.email_address"),
            childs.getString("dm4.contacts.notes"),
            getPersonsOfInstitution(instId)
        );
    }



    // ********************************
    // *** Listener Implementations ***
    // ********************************



    @Override
    public void preCreateAssociation(AssociationModel assoc) {
        // Work <-> Person
        DeepaMehtaUtils.associationAutoTyping(assoc, "crowd.work", "dm4.contacts.person",
            "crowd.work.involvement", "dm4.core.default", "dm4.core.default", dms);
        //
        // Work <-> Institution
        DeepaMehtaUtils.associationAutoTyping(assoc, "crowd.work", "dm4.contacts.institution",
            "crowd.work.involvement", "dm4.core.default", "dm4.core.default", dms);
        //
        // Translation <-> Person
        DeepaMehtaUtils.associationAutoTyping(assoc, "crowd.work.translation", "dm4.contacts.person",
            "crowd.work.involvement", "dm4.core.default", "dm4.core.default", dms);
    }    



    // ------------------------------------------------------------------------------------------------- Private Methods

    // --- Person ---

    private JSONArray getInstitutionsOfPerson(long personId) {
        JSONArray institutions = null;
        ResultList<RelatedTopic> instTopics = contactsService.getInstitutions(personId);
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
        ResultList<RelatedTopic> workTopics = person.getRelatedTopics("crowd.work.involvement", "dm4.core.default",
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
        ResultList<RelatedTopic> translations = person.getRelatedTopics("crowd.work.involvement", "dm4.core.default",
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
        return dms.getTopic(translationId).getRelatedTopic("dm4.core.composition", "dm4.core.child", "dm4.core.parent",
            "crowd.work");
    }

    private JSONArray getEventsOfPerson(long personId) {
        JSONArray events = null;
        ResultList<RelatedTopic> eventTopics = eventsService.getEvents(personId);
        if (!eventTopics.isEmpty()) {
            events = new JSONArray();
            for (RelatedTopic event : eventTopics) {
                events.put(new EventOfPerson(
                    event.getId(),
                    event.getSimpleValue().toString()
                ).toJSON());
            }
        }
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
        ResultList<RelatedTopic> personTopics = dms.getTopic(workId).getRelatedTopics("crowd.work.involvement",
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
        ResultList<RelatedTopic> instTopics = dms.getTopic(workId).getRelatedTopics("crowd.work.involvement",
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
        ResultList<RelatedTopic> personTopics = eventsService.getParticipants(eventId);
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
        ResultList<RelatedTopic> eventSeriesTopics = event.getRelatedTopics("dm4.core.association", "dm4.core.default",
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
        return eventSeries;
    }

    private JSONObject getAddress(Topic event) {
        return address(event.getChildTopics().getTopic("dm4.contacts.address"));
    }

    // --- Event Series ---

    private JSONArray getEventsOfEventSeries(Topic eventSeries) {
        JSONArray events = null;
        ResultList<RelatedTopic> eventTopics = eventSeries.getRelatedTopics("dm4.core.association", "dm4.core.default",
                                                                            "dm4.core.default", "dm4.events.event");
        if (!eventTopics.isEmpty())  {
            events = new JSONArray();
            for (RelatedTopic event : eventTopics) {
                events.put(new EventOfEventSeries(
                    event.getId(),
                    event.getSimpleValue().toString(),
                    event.getChildTopics().getString("dm4.datetime#dm4.events.from")
                ).toJSON());
            }
        }
        return events;
    }

    // --- Institution ---

    private JSONArray getAddresses(Topic institution) {
        JSONArray addresses = null;
        List<RelatedTopic> addressTopics = institution.getChildTopics().getTopics("dm4.contacts.address#" +
            "dm4.contacts.address_entry");
        if (!addressTopics.isEmpty()) {
            addresses = new JSONArray();
            for (RelatedTopic address : addressTopics) {
                addresses.put(address(address));
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
        ResultList<RelatedTopic> personTopics = contactsService.getPersons(instId);
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

    // --- Helper ---

    private JSONObject address(RelatedTopic address) {
        ChildTopics childs = address.getChildTopics();
        return new Address(
            address.getRelatingAssociation().getSimpleValue().toString(),
            childs.getStringOrNull("dm4.contacts.street"),
            childs.getStringOrNull("dm4.contacts.postal_code"),
            childs.getStringOrNull("dm4.contacts.city"),
            childs.getStringOrNull("dm4.contacts.country")
        ).toJSON();
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
