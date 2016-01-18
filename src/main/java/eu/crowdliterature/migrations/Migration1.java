package eu.crowdliterature.migrations;

import de.deepamehta.core.Topic;
import de.deepamehta.core.service.Inject;
import de.deepamehta.core.service.Migration;
import de.deepamehta.core.service.accesscontrol.SharingMode;
import de.deepamehta.plugins.accesscontrol.AccessControlService;
import de.deepamehta.plugins.workspaces.WorkspacesService;



/**
 * Creates the "CROWD" workspace and sets "admin" as its owner.
 * <p>
 * Runs ALWAYS.
 * Part of CROWD Literature 0.4
 */
public class Migration1 extends Migration {

    @Inject
    private WorkspacesService wsService;

    @Inject
    private AccessControlService acService;

    @Override
    public void run() {
        Topic crowd = wsService.createWorkspace("CROWD", "crowd.workspace", SharingMode.PUBLIC);
        acService.setWorkspaceOwner(crowd, AccessControlService.ADMIN_USERNAME);
    }
}
