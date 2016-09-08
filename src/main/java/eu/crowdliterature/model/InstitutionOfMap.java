package eu.crowdliterature.model;

import de.deepamehta.core.JSONEnabled;
import de.deepamehta.geomaps.model.GeoCoordinate;

import org.codehaus.jettison.json.JSONObject;



public class InstitutionOfMap implements JSONEnabled {

    // ---------------------------------------------------------------------------------------------- Instance Variables

    private JSONObject json;

    // ---------------------------------------------------------------------------------------------------- Constructors

    public InstitutionOfMap(long id, String title, GeoCoordinate geoCoord) {
        try {
            json = new JSONObject()
                .put("id",    id)
                .put("title", title);
            if (geoCoord != null) {
                json.put("lat", geoCoord.lat)
                    .put("lng", geoCoord.lon);
            }
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
