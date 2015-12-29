package eu.crowdliterature;

import eu.crowdliterature.model.Person;
import eu.crowdliterature.model.PersonOfWork;
import eu.crowdliterature.model.Work;
import eu.crowdliterature.model.WorkOfPerson;

import de.deepamehta.core.RelatedTopic;
import de.deepamehta.core.service.ResultList;

import java.util.List;



public interface CrowdService {

    // --- Person ---

    Person getPerson(long personId);

    // --- Works ---

    Work getWork(long workId);

    List<WorkOfPerson> getWorks(long personId);

    /**
     * @param   workId      ID of a work or a translation
     */
    List<PersonOfWork> getPersonsOfWork(long workId);

    // --- Event Series ---

    ResultList<RelatedTopic> getEventSeriesOfEvent(long eventId);

    ResultList<RelatedTopic> getEventsOfEventSeries(long eventSeriesId);
}
