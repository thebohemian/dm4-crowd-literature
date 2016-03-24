package eu.crowdliterature;

import eu.crowdliterature.model.Event;
import eu.crowdliterature.model.EventOfMap;
import eu.crowdliterature.model.EventSeries;
import eu.crowdliterature.model.Institution;
import eu.crowdliterature.model.Person;
import eu.crowdliterature.model.Work;

import java.util.List;



public interface CrowdService {

    String getStartPageContent();

    // --- Person ---

    Person getPerson(long personId);

    // --- Work ---

    Work getWork(long workId);

    // --- Event ---

    Event getEvent(long eventId);

    List<EventOfMap> getAllEvents();

    // --- Event Series ---

    EventSeries getEventSeries(long eventSeriesId);

    // --- Institution ---

    Institution getInstitution(long instId);
}
