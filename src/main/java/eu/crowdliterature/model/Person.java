package eu.crowdliterature.model;

import de.deepamehta.core.JSONEnabled;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;



public class Person implements JSONEnabled {

    // ---------------------------------------------------------------------------------------------- Instance Variables

    private JSONObject json;

    // ---------------------------------------------------------------------------------------------------- Constructors

    public Person(String name, String placeOfLiving, String yearOfBirth, String placeOfBirth, String notes, JSONArray urls,
                  JSONArray nationalities, JSONArray languages, JSONArray institutions, JSONArray works,
                  JSONArray events) {
        try {
            json = new JSONObject()
                .put("name", name)
		.put("placeOfLiving", placeOfLiving)
                .put("yearOfBirth", yearOfBirth)
                .put("placeOfBirth", placeOfBirth)
                .put("notes", notes)
                .put("urls", urls)
                .put("nationalities", nationalities)
                .put("languages", languages)
                .put("institutions", institutions)
                .put("works", works)
                .put("events", events);
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
