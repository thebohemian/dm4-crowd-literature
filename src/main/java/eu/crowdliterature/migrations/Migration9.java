package eu.crowdliterature.migrations;

import java.util.List;

import de.deepamehta.core.RelatedTopic;
import de.deepamehta.core.Topic;
import de.deepamehta.core.service.Inject;
import de.deepamehta.core.service.Migration;
import de.deepamehta.facets.FacetsService;
import eu.crowdliterature.GeoMapsHelper;



public class Migration9 extends Migration {
	
	@Inject
	private FacetsService facetsService;

    @Override
    public void run() {
    	GeoMapsHelper geoMapsHelper = new GeoMapsHelper(mf, facetsService);
    	
    	List<Topic> persons = dm4.getTopicsByType("dm4.contacts.person");
    	for (Topic person : persons) {
    		List<RelatedTopic> addresses = person.getChildTopics().getTopics("dm4.contacts.address#dm4.contacts.address_entry");
    		for (RelatedTopic address : addresses) {
    			// Just save again.
    			geoMapsHelper.fixGeoCoordinate(address);
    		}
    	}
    }
}
