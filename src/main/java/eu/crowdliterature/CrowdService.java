package eu.crowdliterature;

import eu.crowdliterature.model.Event;
import eu.crowdliterature.model.Person;
import eu.crowdliterature.model.Work;

import de.deepamehta.core.RelatedTopic;
import de.deepamehta.core.service.ResultList;

import java.util.List;



public interface CrowdService {

    // --- Person ---

    Person getPerson(long personId);

    // --- Work ---

    Work getWork(long workId);

    // --- Event ---

    Event getEvent(long eventId);

    // --- Event Series ---

    ResultList<RelatedTopic> getEventsOfEventSeries(long eventSeriesId);
}
