package eu.crowdliterature.model;

import de.deepamehta.core.JSONEnabled;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;



public class Institution implements JSONEnabled {

    // ---------------------------------------------------------------------------------------------- Instance Variables

    private JSONObject json;

    // ---------------------------------------------------------------------------------------------------- Constructors

    public Institution(String name, JSONArray urls, JSONArray addresses, JSONArray phones, JSONArray emails,
                       String notes, JSONArray persons) {
        try {
            json = new JSONObject()
                .put("name",      name)
                .put("urls",      urls)
                .put("addresses", addresses)
                .put("phones",    phones)
                .put("emails",    emails)
                .put("notes",     notes)
                .put("persons",   persons);
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
