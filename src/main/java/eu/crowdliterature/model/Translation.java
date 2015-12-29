package eu.crowdliterature.model;

import de.deepamehta.core.JSONEnabled;
import de.deepamehta.core.util.DeepaMehtaUtils;

import org.codehaus.jettison.json.JSONObject;

import java.util.List;



public class Translation implements JSONEnabled {

    // ---------------------------------------------------------------------------------------------- Instance Variables

    private String title;
    private String language;
    private String isbn;
    private List<PersonOfWork> translators;

    // ---------------------------------------------------------------------------------------------------- Constructors

    public Translation(String title, String language, String isbn, List<PersonOfWork> translators) {
        this.title = title;
        this.language = language;
        this.isbn = isbn;
        this.translators = translators;
    }

    // -------------------------------------------------------------------------------------------------- Public Methods

    @Override
    public JSONObject toJSON() {
        try {
            return new JSONObject()
                .put("title", title)
                .put("language", language)
                .put("isbn", isbn)
                .put("translators", DeepaMehtaUtils.toJSONArray(translators));
        } catch (Exception e) {
            throw new RuntimeException("Serialization failed (" + this + ")", e);
        }
    }
}
