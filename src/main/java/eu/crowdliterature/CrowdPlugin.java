package eu.crowdliterature;

import eu.crowdliterature.model.WorkOfAPerson;

import de.deepamehta.core.RelatedTopic;
import de.deepamehta.core.Topic;
import de.deepamehta.core.osgi.PluginActivator;
import de.deepamehta.core.service.ResultList;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import java.util.ArrayList;
import java.util.List;



@Path("/crowd")
@Produces("application/json")
public class CrowdPlugin extends PluginActivator implements CrowdService {

    // -------------------------------------------------------------------------------------------------- Public Methods



    // ***********************************
    // *** CrowdService Implementation ***
    // ***********************************



    // --- Works ---

    @GET
    @Path("/person/{id}/works")
    @Override
    public List<WorkOfAPerson> getWorks(@PathParam("id") long personId) {
        List<WorkOfAPerson> works = new ArrayList();
        Topic person = dms.getTopic(personId);
        // works
        for (RelatedTopic work : person.getRelatedTopics("crowd.work.involvement", "dm4.core.default",
                                                         "dm4.core.default", "crowd.work")) {
            String workTitle = work.getSimpleValue().toString();
            String role = work.getRelatingAssociation().getSimpleValue().toString();
            works.add(new WorkOfAPerson(work.getId(), workTitle, role));
        }
        // translations
        for (RelatedTopic translation : person.getRelatedTopics("crowd.work.involvement", "dm4.core.default",
                                                                "dm4.core.default", "crowd.work.translation")) {
            Topic work = getWork(translation.getId());
            String workTitle = work.getSimpleValue().toString();
            String role = translation.getRelatingAssociation().getSimpleValue().toString();
            works.add(new WorkOfAPerson(work.getId(), workTitle, role));
        }
        //
        return works;
    }

    @GET
    @Path("/work/{id}/persons")
    @Override
    public ResultList<RelatedTopic> getPersons(@PathParam("id") long workId) {
        return dms.getTopic(workId).getRelatedTopics("crowd.work.involvement", "dm4.core.default", "dm4.core.default",
            "dm4.contacts.person");
    }

    // --- Events ---

    @GET
    @Path("/event/{id}/series")
    @Override
    public ResultList<RelatedTopic> getSeriesOfEvents(@PathParam("id") long eventId) {
        return dms.getTopic(eventId).getRelatedTopics("dm4.core.association", "dm4.core.default", "dm4.core.default",
            "crowd.series_of_events");
    }



    // ------------------------------------------------------------------------------------------------- Private Methods

    private Topic getWork(long translationId) {
        return dms.getTopic(translationId).getRelatedTopic("dm4.core.composition", "dm4.core.child", "dm4.core.parent",
            "crowd.work");
    }
}
