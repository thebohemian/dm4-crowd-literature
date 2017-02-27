package eu.crowdliterature.migrations;

import de.deepamehta.core.ChildTopics;
import de.deepamehta.core.Topic;
import de.deepamehta.core.service.Migration;



public class Migration8 extends Migration {

    @Override
    public void run() {
        Topic startPage = dm4.getTopicByUri("crowd.omnibus.start_page");
        startPage.setUri("crowd.meet.start_page");
        
        Topic signUpConfig = dm4.getTopicByUri("org.deepamehta.signup.default_configuration");
        ChildTopics childs = signUpConfig.getChildTopics();
        childs.set("org.deepamehta.signup.config_project_title", "CROWD Meet Sign Up Configuration");
        childs.set("org.deepamehta.signup.config_webapp_title", "CROWD Meet");
        childs.set("org.deepamehta.signup.config_from_mailbox", "XXX");	// TODO!
        childs.set("org.deepamehta.signup.loading_app_hint", "Entering CROWD Meet");
        
        childs.set("org.deepamehta.signup.config_admin_mailbox", "thebohemian@gmx.net");
        childs.set("org.deepamehta.signup.config_email_confirmation", false);
        childs.set("org.deepamehta.signup.start_page_url", "/eu.crowd-literature/edit.html");
        childs.set("org.deepamehta.signup.home_page_url", "/eu.crowd-literature/index.html");
    }
}
