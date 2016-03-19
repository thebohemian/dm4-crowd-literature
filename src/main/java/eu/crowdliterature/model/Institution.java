package eu.crowdliterature.model;

import de.deepamehta.core.JSONEnabled;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;



public class Institution implements JSONEnabled {

    // ---------------------------------------------------------------------------------------------- Instance Variables

    private JSONObject json;

    // ---------------------------------------------------------------------------------------------------- Constructors

    public Institution(String name, JSONArray urls, JSONArray addresses, JSONArray phoneNumbers, JSONArray emails,
                       String notes, JSONArray persons, JSONArray events) {
        try {
            json = new JSONObject()
                .put("name",         name)
                .put("urls",         urls)
                .put("addresses",    addresses)
                .put("phoneNumbers", phoneNumbers)
                .put("emails",       emails)
                .put("notes",        notes)
                .put("persons",      persons)
                .put("events",       events);
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
