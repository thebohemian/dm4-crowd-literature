package eu.crowdliterature.model;

import de.deepamehta.core.JSONEnabled;

import org.codehaus.jettison.json.JSONObject;



public class DateTime implements JSONEnabled {

    // ---------------------------------------------------------------------------------------------- Instance Variables

    private JSONObject json;

    // ---------------------------------------------------------------------------------------------------- Constructors

    public DateTime(Integer month, Integer day, Integer year, Integer hour, Integer minute) {
        try {
            json = new JSONObject()
                .put("date", new JSONObject()
                    .put("month", month)
                    .put("day",   day)
                    .put("year",  year)
                )
                .put("time", new JSONObject()
                    .put("hour",   hour)
                    .put("minute", minute)
                );
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
