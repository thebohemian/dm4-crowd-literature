package eu.crowdliterature.model;

import de.deepamehta.core.JSONEnabled;
import de.deepamehta.core.util.DeepaMehtaUtils;

import org.codehaus.jettison.json.JSONObject;

import java.util.List;



public class Translation implements JSONEnabled {

    // ---------------------------------------------------------------------------------------------- Instance Variables

    private JSONObject json;

    // ---------------------------------------------------------------------------------------------------- Constructors

    public Translation(String title, String language, String isbn, List<PersonOfWork> persons) {
        try {
            json = new JSONObject()
                .put("title", title)
                .put("language", language)
                .put("isbn", isbn)
                .put("persons", DeepaMehtaUtils.toJSONArray(persons));
        } catch (Exception e) {
            throw new RuntimeException("Serialization failed (" + this + ")", e);
        }
    }

    // -------------------------------------------------------------------------------------------------- Public Methods

    @Override
    public JSONObject toJSON() {
        return json;
    }
}
