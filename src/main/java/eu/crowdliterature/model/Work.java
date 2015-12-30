package eu.crowdliterature.model;

import de.deepamehta.core.JSONEnabled;
import de.deepamehta.core.util.DeepaMehtaUtils;

import org.codehaus.jettison.json.JSONObject;

import java.util.List;



public class Work implements JSONEnabled {

    // ---------------------------------------------------------------------------------------------- Instance Variables

    private JSONObject json;

    // ---------------------------------------------------------------------------------------------------- Constructors

    public Work(String title, String type, String language, String yearOfPublication, String placeOfPublication,
                List<String> genres, String notes, String isbn, String url, List<Translation> translations,
                List<PersonOfWork> persons) {
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
                .put("translations", DeepaMehtaUtils.toJSONArray(translations))
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
