package eu.crowdliterature;

import eu.crowdliterature.model.Event;
import eu.crowdliterature.model.EventOfPerson;
import eu.crowdliterature.model.EventSeriesOfEvent;
import eu.crowdliterature.model.Institution;
import eu.crowdliterature.model.InstitutionOfPerson;
import eu.crowdliterature.model.Person;
import eu.crowdliterature.model.PersonOfEvent;
import eu.crowdliterature.model.PersonOfInstitution;
import eu.crowdliterature.model.PersonOfWork;
import eu.crowdliterature.model.Phone;
import eu.crowdliterature.model.Translation;
import eu.crowdliterature.model.Work;
import eu.crowdliterature.model.WorkOfPerson;

import de.deepamehta.core.ChildTopics;
import de.deepamehta.core.RelatedTopic;
import de.deepamehta.core.Topic;
import de.deepamehta.core.osgi.PluginActivator;
import de.deepamehta.core.service.Inject;
import de.deepamehta.core.service.ResultList;
import de.deepamehta.plugins.contacts.ContactsService;
import de.deepamehta.plugins.events.EventsService;

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
public class CrowdPlugin extends PluginActivator implements CrowdService {

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
            getPersonsOfWork(workId)
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
    @Path("/event_series/{id}/events")
    @Override
    public ResultList<RelatedTopic> getEventsOfEventSeries(@PathParam("id") long eventSeriesId) {
        return dms.getTopic(eventSeriesId).getRelatedTopics("dm4.core.association", "dm4.core.default",
            "dm4.core.default", "dm4.events.event");
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



    // ------------------------------------------------------------------------------------------------- Private Methods

    // --- Person ---

    private JSONArray getInstitutionsOfPerson(long personId) {
        JSONArray institutions = null;
        for (RelatedTopic inst : contactsService.getInstitutions(personId)) {
            if (institutions == null) {
                institutions = new JSONArray();
            }
            institutions.put(new InstitutionOfPerson(
                inst.getId(),
                inst.getSimpleValue().toString(),
                inst.getRelatingAssociation().getSimpleValue().toString()
            ).toJSON());
        }
        return institutions;
    }

    private JSONArray getWorksOfPerson(Topic person) {
        JSONArray works = null;
        // works
        for (RelatedTopic work : person.getRelatedTopics("crowd.work.involvement", "dm4.core.default",
                                                         "dm4.core.default", "crowd.work")) {
            if (works == null) {
                works = new JSONArray();
            }
            works.put(new WorkOfPerson(
                work.getId(),
                work.getSimpleValue().toString(),
                work.getRelatingAssociation().getSimpleValue().toString()
            ).toJSON());
        }
        // translations
        for (RelatedTopic translation : person.getRelatedTopics("crowd.work.involvement", "dm4.core.default",
                                                                "dm4.core.default", "crowd.work.translation")) {
            if (works == null) {
                works = new JSONArray();
            }
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
        for (RelatedTopic event : eventsService.getEvents(personId)) {
            if (events == null) {
                events = new JSONArray();
            }
            events.put(new EventOfPerson(
                event.getId(),
                event.getSimpleValue().toString()
            ).toJSON());
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
        for (RelatedTopic person : dms.getTopic(workId).getRelatedTopics("crowd.work.involvement", "dm4.core.default",
                                                                         "dm4.core.default", "dm4.contacts.person")) {
            if (persons == null) {
                persons = new JSONArray();
            }
            persons.put(new PersonOfWork(
                person.getId(),
                person.getSimpleValue().toString(),
                person.getRelatingAssociation().getSimpleValue().toString()
            ).toJSON());
        }
        return persons;
    }

    // --- Event ---

    private JSONArray getParticipants(long eventId) {
        JSONArray participants = null;
        for (RelatedTopic person : eventsService.getParticipants(eventId)) {
            if (participants == null) {
                participants = new JSONArray();
            }
            participants.put(new PersonOfEvent(
                person.getId(),
                person.getSimpleValue().toString()
            ).toJSON());
        }
        return participants;
    }

    private JSONArray getEventSeries(Topic event) {
        JSONArray eventSeries = null;
        for (RelatedTopic eventSeriesTopic : event.getRelatedTopics("dm4.core.association", "dm4.core.default",
                                                                    "dm4.core.default", "crowd.event_series")) {
            if (eventSeries == null) {
                eventSeries = new JSONArray();
            }
            eventSeries.put(new EventSeriesOfEvent(
                eventSeriesTopic.getId(),
                eventSeriesTopic.getSimpleValue().toString()
            ).toJSON());
        }
        return eventSeries;
    }

    // ---

    private JSONObject getAddress(Topic topic) {
        return address(topic.getChildTopics().getTopic("dm4.contacts.address"));
    }

    // --- Institution ---

    private JSONArray getAddresses(Topic topic) {
        JSONArray addresses = null;
        List<RelatedTopic> addressTopics = topic.getChildTopics().getTopics("dm4.contacts.address#" +
            "dm4.contacts.address_entry");
        if (!addressTopics.isEmpty()) {
            addresses = new JSONArray();
            for (RelatedTopic address : addressTopics) {
                addresses.put(address(address));
            }
        }
        return addresses;
    }

    private JSONArray getPhoneNumbers(Topic topic) {
        JSONArray phoneNumbers = null;
        List<RelatedTopic> phoneTopics = topic.getChildTopics().getTopicsOrNull("dm4.contacts.phone_number#" +
            "dm4.contacts.phone_entry");
        if (phoneTopics != null) {
            phoneNumbers = new JSONArray();
            for (RelatedTopic phone : phoneTopics) {
                phoneNumbers.put(new Phone(
                    phone.getRelatingAssociation().getSimpleValue().toString(),
                    phone.getSimpleValue().toString()
                ).toJSON());
            }
        }
        return phoneNumbers;
    }

    private JSONArray getPersonsOfInstitution(long instId) {
        JSONArray persons = null;
        for (RelatedTopic person : contactsService.getPersons(instId)) {
            if (persons == null) {
                persons = new JSONArray();
            }
            persons.put(new PersonOfInstitution(
                person.getId(),
                person.getSimpleValue().toString(),
                person.getRelatingAssociation().getSimpleValue().toString()
            ).toJSON());
        }
        return persons;
    }

    // --- Helper ---

    private JSONObject address(RelatedTopic address) {
        try {
            ChildTopics childs = address.getChildTopics();
            return new JSONObject()
                .put("label", address.getRelatingAssociation().getSimpleValue().toString())
                .put("street",     childs.getStringOrNull("dm4.contacts.street"))
                .put("postalCode", childs.getStringOrNull("dm4.contacts.postal_code"))
                .put("city",       childs.getStringOrNull("dm4.contacts.city"))
                .put("country",    childs.getStringOrNull("dm4.contacts.country"));
        } catch (Exception e) {
            throw new RuntimeException("Serializing an Address failed (" + this + ")", e);
        }
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
