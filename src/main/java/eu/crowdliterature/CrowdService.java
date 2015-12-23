package eu.crowdliterature;

import de.deepamehta.core.RelatedTopic;
import de.deepamehta.core.service.ResultList;



public interface CrowdService {

    ResultList<RelatedTopic> getWorks(long personId);

    ResultList<RelatedTopic> getPersons(long workId);
}
