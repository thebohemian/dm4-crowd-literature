package eu.crowdliterature;

import java.util.List;

import javax.ws.rs.PathParam;

import de.deepamehta.core.JSONEnabled;
import eu.crowdliterature.model.Event;
import eu.crowdliterature.model.Institution;
import eu.crowdliterature.model.InstitutionOfMap;
import eu.crowdliterature.model.Person;
import eu.crowdliterature.model.PersonOfMap;
import eu.crowdliterature.model.Work;



public interface CrowdService {

    String getStartPageContent();
    
    SearchResult searchTopics(String searchTerm);

    // --- Event ---
    
	Event getEvent(long eventId);

    
    // --- Person ---

    Person getPerson(long personId);

    List<PersonOfMap> getAllPersons();

    // Editable Person
    void ensureUserToPersonAssociationAndWorkspaceSetup();
    
    long getPersonIdByLoggedInUser();
    
    // --- Work ---

    Work getWork(long workId);

    // --- Institution ---

    Institution getInstitution(long instId);

    List<InstitutionOfMap> getAllInstitutions();
    
    interface SearchResult extends JSONEnabled {}
}
