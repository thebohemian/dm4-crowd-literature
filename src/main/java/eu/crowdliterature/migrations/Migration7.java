package eu.crowdliterature.migrations;

import de.deepamehta.core.Topic;
import de.deepamehta.core.service.Inject;
import de.deepamehta.core.service.Migration;
import de.deepamehta.workspaces.WorkspacesService;



/**
 * Creates "CROWD Meet start page" Note topic.
 * <p>
 * Runs ALWAYS.
 * Part of CROWD Literature 0.6
 */
public class Migration7 extends Migration {

    @Inject
    private WorkspacesService wsService;

    @Override
    public void run() {
        Topic startPage = dm4.createTopic(mf.newTopicModel("crowd.meet.start_page", "dm4.notes.note",
            mf.newChildTopicsModel()
                .put("dm4.notes.title", "CROWD Meet start page")
                .put("dm4.notes.text", "<p>Your content here ...</p>")
        ));
        wsService.assignToWorkspace(startPage, wsService.getWorkspace("crowd.workspace").getId());
    }
}
