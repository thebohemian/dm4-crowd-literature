package eu.crowdliterature.model;

import de.deepamehta.core.JSONEnabled;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;



public class Event implements JSONEnabled {

    // ---------------------------------------------------------------------------------------------- Instance Variables

    private JSONObject json;

    // ---------------------------------------------------------------------------------------------------- Constructors

    public Event(String title, JSONObject from, JSONObject to, JSONObject address, String notes, JSONArray participants,
                 String entranceFee, String url, JSONArray eventSeries, JSONArray recommendedByPersons) {
        try {
            json = new JSONObject()
                .put("title",        title)
                .put("from",         from)
                .put("to",           to)
                .put("address",      address)
                .put("notes",        notes)
                .put("participants", participants)
                .put("entranceFee",  entranceFee)
                .put("url",          url)
                .put("eventSeries",  eventSeries)
                .put("recommendedBy", recommendedByPersons);
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
