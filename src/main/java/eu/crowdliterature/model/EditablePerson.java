package eu.crowdliterature.model;

import de.deepamehta.core.JSONEnabled;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;



public class EditablePerson implements JSONEnabled {

    // ---------------------------------------------------------------------------------------------- Instance Variables

    private JSONObject json;

    // ---------------------------------------------------------------------------------------------------- Constructors

    public EditablePerson(long id, String name, String email, String yearOfBirth, String placeOfBirth, String notes) {
        try {
            json = new JSONObject()
                .put("id", id)
                .put("name", name)
		.put("email", email)
                .put("yearOfBirth", yearOfBirth)
                .put("placeOfBirth", placeOfBirth)
                .put("notes", notes);
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