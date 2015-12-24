package eu.crowdliterature.model;

import de.deepamehta.core.JSONEnabled;

import org.codehaus.jettison.json.JSONObject;



public class WorkOfAPerson implements JSONEnabled {

    // ---------------------------------------------------------------------------------------------- Instance Variables

    private long workId;
    private String workTitle;
    private String role;

    // ---------------------------------------------------------------------------------------------------- Constructors

    public WorkOfAPerson(long workId, String workTitle, String role) {
        this.workId = workId;
        this.workTitle = workTitle;
        this.role = role;
    }

    // -------------------------------------------------------------------------------------------------- Public Methods

    @Override
    public JSONObject toJSON() {
        try {
            return new JSONObject()
                .put("id",    workId)
                .put("title", workTitle)
                .put("role",  role);
        } catch (Exception e) {
            throw new RuntimeException("Serialization failed (" + this + ")", e);
        }
    }
}
