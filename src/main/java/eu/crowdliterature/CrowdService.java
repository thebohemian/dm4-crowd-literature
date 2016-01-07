package eu.crowdliterature;

import eu.crowdliterature.model.Event;
import eu.crowdliterature.model.EventSeries;
import eu.crowdliterature.model.Institution;
import eu.crowdliterature.model.Person;
import eu.crowdliterature.model.Work;



public interface CrowdService {

    // --- Person ---

    Person getPerson(long personId);

    // --- Work ---

    Work getWork(long workId);

    // --- Event ---

    Event getEvent(long eventId);

    // --- Event Series ---

    EventSeries getEventSeries(long eventSeriesId);

    // --- Institution ---

    Institution getInstitution(long instId);
}
