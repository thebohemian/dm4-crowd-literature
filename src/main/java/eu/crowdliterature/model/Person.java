package eu.crowdliterature.model;

import de.deepamehta.core.JSONEnabled;

import org.codehaus.jettison.json.JSONObject;

import java.util.List;



public class Person implements JSONEnabled {

    // ---------------------------------------------------------------------------------------------- Instance Variables

    private String name;
    private String yearOfBirth;
    private String placeOfBirth;
    private String notes;
    private List<String> urls;
    private List<String> nationalities;
    private List<String> languages;

    // ---------------------------------------------------------------------------------------------------- Constructors

    public Person(String name, String yearOfBirth, String placeOfBirth, String notes, List<String> urls,
                                                   List<String> nationalities, List<String> languages) {
        this.name = name;
        this.yearOfBirth = yearOfBirth;
        this.placeOfBirth = placeOfBirth;
        this.notes = notes;
        this.urls = urls;
        this.nationalities = nationalities;
        this.languages = languages;
    }

    // -------------------------------------------------------------------------------------------------- Public Methods

    @Override
    public JSONObject toJSON() {
        try {
            return new JSONObject()
                .put("name", name)
                .put("yearOfBirth", yearOfBirth)
                .put("placeOfBirth", placeOfBirth)
                .put("notes", notes)
                .put("urls", urls)
                .put("nationalities", nationalities)
                .put("languages", languages);
        } catch (Exception e) {
            throw new RuntimeException("Serialization failed (" + this + ")", e);
        }
    }
}
