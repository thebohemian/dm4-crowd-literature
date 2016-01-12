package eu.crowdliterature.model;

import de.deepamehta.core.JSONEnabled;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;



public class Work implements JSONEnabled {

    // ---------------------------------------------------------------------------------------------- Instance Variables

    private JSONObject json;

    // ---------------------------------------------------------------------------------------------------- Constructors

    public Work(String title, String type, String language, String yearOfPublication, String placeOfPublication,
                JSONArray genres, String notes, String isbn, String url, JSONArray translations, JSONArray persons,
                JSONArray institutions) {
        try {
            json = new JSONObject()
                .put("title", title)
                .put("type", type)
                .put("language", language)
                .put("yearOfPublication", yearOfPublication)
                .put("placeOfPublication", placeOfPublication)
                .put("genres", genres)
                .put("notes", notes)
                .put("isbn", isbn)
                .put("url", url)
                .put("translations", translations)
                .put("persons", persons)
                .put("institutions", institutions);
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
