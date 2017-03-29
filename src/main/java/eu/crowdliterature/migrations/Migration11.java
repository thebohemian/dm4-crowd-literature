package eu.crowdliterature.migrations;

import java.util.List;

import de.deepamehta.core.RelatedTopic;
import de.deepamehta.core.Topic;
import de.deepamehta.core.service.Inject;
import de.deepamehta.core.service.Migration;
import de.deepamehta.facets.FacetsService;
import eu.crowdliterature.GeoMapsHelper;



public class Migration11 extends Migration {
	
	@Inject
	private FacetsService facetsService;

    @Override
    public void run() {
    	dm4.getTopicType("dm4.contacts.person")
    		.addAssocDef(mf.newAssociationDefinitionModel("dm4.core.composition_def",
    			"dm4.contacts.person", "crowd.person.iscontactenabled",
    			"dm4.core.one", "dm4.core.one"));
    	
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
