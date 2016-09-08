package eu.crowdliterature;

import eu.crowdliterature.model.Institution;
import eu.crowdliterature.model.InstitutionOfMap;
import eu.crowdliterature.model.Person;
import eu.crowdliterature.model.PersonOfMap;
import eu.crowdliterature.model.Work;

import java.util.List;



public interface CrowdService {

    String getStartPageContent();

    // --- Person ---

    Person getPerson(long personId);

    List<PersonOfMap> getAllPersons();

    // --- Work ---

    Work getWork(long workId);

    // --- Institution ---

    Institution getInstitution(long instId);

    List<InstitutionOfMap> getAllInstitutions();
}
