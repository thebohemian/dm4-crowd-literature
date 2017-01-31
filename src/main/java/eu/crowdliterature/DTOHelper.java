package eu.crowdliterature;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.codehaus.jettison.json.JSONException;

import de.deepamehta.core.Topic;
import eu.crowdliterature.CrowdService.SearchResult;

public class DTOHelper {
	
	static HashSet<String> acceptableTypes = new HashSet<String>();
	
	static {
		acceptableTypes.add("dm4.contacts.person");
		acceptableTypes.add("dm4.contacts.institution");
	}

	public static SearchResult toSearchResult(List<Topic> topics) throws JSONException {
		SearchResultImpl json = new SearchResultImpl();
		
		Set<Long> filter = new HashSet<Long>();
		for (Topic topic : topics) {
			// Goes "up" in the type hierarchy to find out whether the topic is
			// in a hierarchy that interests us.
			do {
				String type = topic.getTypeUri();
				if (acceptableTypes.contains(type)) {
					filter.add(topic.getId());
					break;
				} else {
					topic = safeParentOrNull(topic);
				}
				
			} while (topic != null);
		}
		json.put("filter", filter);
		
		return json;
	}
	
	private static Topic safeParentOrNull(Topic topic) {
		return (topic != null) ? topic.getRelatedTopic(null, "dm4.core.child", "dm4.core.parent", null) : null;
	}
	
	static class SearchResultImpl extends JSONEnabledImpl implements SearchResult { }
}
