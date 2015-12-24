package eu.crowdliterature;

import eu.crowdliterature.model.WorkOfAPerson;

import de.deepamehta.core.RelatedTopic;
import de.deepamehta.core.service.ResultList;

import java.util.List;



public interface CrowdService {

    List<WorkOfAPerson> getWorks(long personId);

    /**
     * @param   workId      ID of a work or a translation
     */
    ResultList<RelatedTopic> getPersons(long workId);
}
