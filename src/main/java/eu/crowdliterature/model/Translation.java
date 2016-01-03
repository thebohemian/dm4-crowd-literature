package eu.crowdliterature.model;

import de.deepamehta.core.JSONEnabled;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;



public class Translation implements JSONEnabled {

    // ---------------------------------------------------------------------------------------------- Instance Variables

    private JSONObject json;

    // ---------------------------------------------------------------------------------------------------- Constructors

    public Translation(String title, String language, String isbn, JSONArray persons) {
        try {
            json = new JSONObject()
                .put("title", title)
                .put("language", language)
                .put("isbn", isbn)
                .put("persons", persons);
            // Note: when List/Map null is put Jettison serializes an empty array/object.
            // In contrast when String/Object/JSONObject/JSONArray null is put Jettison removes the property.
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
