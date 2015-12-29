package eu.crowdliterature.model;

import de.deepamehta.core.JSONEnabled;

import org.codehaus.jettison.json.JSONObject;



public class PersonOfWork implements JSONEnabled {

    // ---------------------------------------------------------------------------------------------- Instance Variables

    private long id;
    private String name;
    private String role;

    // ---------------------------------------------------------------------------------------------------- Constructors

    public PersonOfWork(long id, String name, String role) {
        this.id = id;
        this.name = name;
        this.role = role;
    }

    // -------------------------------------------------------------------------------------------------- Public Methods

    @Override
    public JSONObject toJSON() {
        try {
            return new JSONObject()
                .put("id",   id)
                .put("name", name)
                .put("role", role);
        } catch (Exception e) {
            throw new RuntimeException("Serialization failed (" + this + ")", e);
        }
    }
}
