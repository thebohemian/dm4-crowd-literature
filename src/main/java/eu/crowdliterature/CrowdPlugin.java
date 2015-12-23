package eu.crowdliterature;

import de.deepamehta.core.RelatedTopic;
import de.deepamehta.core.osgi.PluginActivator;
import de.deepamehta.core.service.ResultList;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;



@Path("/crowd")
@Produces("application/json")
public class CrowdPlugin extends PluginActivator implements CrowdService {

    // -------------------------------------------------------------------------------------------------- Public Methods



    // ***********************************
    // *** CrowdService Implementation ***
    // ***********************************



    @GET
    @Path("/person/{id}/works")
    @Override
    public ResultList<RelatedTopic> getWorks(@PathParam("id") long personId) {
        return dms.getTopic(personId).getRelatedTopics("crowd.work.involvement", "dm4.core.default", "dm4.core.default",
            "crowd.work");
    }

    @GET
    @Path("/work/{id}/persons")
    @Override
    public ResultList<RelatedTopic> getPersons(@PathParam("id") long workId) {
        return dms.getTopic(workId).getRelatedTopics("crowd.work.involvement", "dm4.core.default", "dm4.core.default",
            "dm4.contacts.person");
    }
}
