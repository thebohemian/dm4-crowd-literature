package eu.crowdliterature.model;

import de.deepamehta.core.JSONEnabled;
import de.deepamehta.core.util.DeepaMehtaUtils;

import org.codehaus.jettison.json.JSONObject;

import java.util.List;



public class Work implements JSONEnabled {

    // ---------------------------------------------------------------------------------------------- Instance Variables

    private String title;
    private String type;
    private String language;
    private String yearOfPublication;
    private String placeOfPublication;
    private List<String> genres;
    private String notes;
    private String isbn;
    private String url;
    private List<Translation> translations;
    private List<PersonOfWork> persons;

    // ---------------------------------------------------------------------------------------------------- Constructors

    public Work(String title, String type, String language, String yearOfPublication, String placeOfPublication,
                List<String> genres, String notes, String isbn, String url, List<Translation> translations,
                List<PersonOfWork> persons) {
        this.title = title;
        this.type = type;
        this.language = language;
        this.yearOfPublication = yearOfPublication;
        this.placeOfPublication = placeOfPublication;
        this.genres = genres;
        this.notes = notes;
        this.isbn = isbn;
        this.url = url;
        this.translations = translations;
        this.persons = persons;
    }

    // -------------------------------------------------------------------------------------------------- Public Methods

    @Override
    public JSONObject toJSON() {
        try {
            return new JSONObject()
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
}
