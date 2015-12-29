package eu.crowdliterature.model;

import de.deepamehta.core.JSONEnabled;

import org.codehaus.jettison.json.JSONObject;



public class WorkOfPerson implements JSONEnabled {

    // ---------------------------------------------------------------------------------------------- Instance Variables

    private long id;
    private String title;
    private String role;

    // ---------------------------------------------------------------------------------------------------- Constructors

    public WorkOfPerson(long id, String title, String role) {
        this.id = id;
        this.title = title;
        this.role = role;
    }

    // -------------------------------------------------------------------------------------------------- Public Methods

    @Override
    public JSONObject toJSON() {
        try {
            return new JSONObject()
                .put("id",    id)
                .put("title", title)
                .put("role",  role);
        } catch (Exception e) {
            throw new RuntimeException("Serialization failed (" + this + ")", e);
        }
    }
}
