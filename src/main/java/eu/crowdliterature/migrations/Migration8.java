package eu.crowdliterature.migrations;

import de.deepamehta.core.Topic;
import de.deepamehta.core.service.Inject;
import de.deepamehta.core.service.Migration;
import de.deepamehta.workspaces.WorkspacesService;



/**
 * Renames the "CROWD Bus start page" Note topic to "CROWD Meet start page".
 * <p>
 * Runs ALWAYS.
 * Part of CROWD Literature 0.7
 */
public class Migration8 extends Migration {

    @Inject
    private WorkspacesService wsService;

    @Override
    public void run() {
        Topic startPage = dm4.getTopicByUri("crowd.omnibus.start_page");
	startPage.setUri("crowd.meet.start_page");
    }
}
