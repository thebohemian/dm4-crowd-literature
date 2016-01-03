package eu.crowdliterature.model;

import de.deepamehta.core.JSONEnabled;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;



public class Person implements JSONEnabled {

    // ---------------------------------------------------------------------------------------------- Instance Variables

    private JSONObject json;

    // ---------------------------------------------------------------------------------------------------- Constructors

    public Person(String name, String yearOfBirth, String placeOfBirth, String notes, JSONArray urls,
                                                   JSONArray nationalities, JSONArray languages) {
        try {
            json = new JSONObject()
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

    // -------------------------------------------------------------------------------------------------- Public Methods

    @Override
    public JSONObject toJSON() {
        return json;
    }
}
