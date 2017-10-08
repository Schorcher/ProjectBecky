package com.becky.networking.message;

import org.json.JSONObject;

public class UsernameChangeRequest implements NetworkedMessage {
    private String oldUsername;
    private String newUsername;
    private String authenticationString;

    public UsernameChangeRequest() {}

    public UsernameChangeRequest(final String json) {
        final int jsonStartIndex = json.indexOf('{');
        if(jsonStartIndex == -1) {
            throw new IllegalArgumentException("Invalid JSON Object");
        }
        if(!json.startsWith(UsernameChangeRequest.class.getSimpleName())) {
            throw new IllegalArgumentException("Json object does not define a valid UsernameChangeRequest object.");
        }

        final JSONObject obj = new JSONObject(json.substring(jsonStartIndex));
        this.oldUsername = obj.getString("oldUsername");
        this.newUsername = obj.getString("newUsername");
        this.authenticationString = obj.getString("authenticationString");
    }

    public String getOldUsername() {
        return oldUsername;
    }

    public void setOldUsername(final String oldUsername) {
        this.oldUsername = oldUsername;
    }

    public String getNewUsername() {
        return newUsername;
    }

    public void setNewUsername(final String newUsername) {
        this.newUsername = newUsername;
    }

    public String getAuthenticationString() {
        return authenticationString;
    }

    public void setAuthenticationString(final String authenticationString) {
        this.authenticationString = authenticationString;
    }

    @Override
    public String jsonSerialize() {
        return UsernameChangeRequest.class.getSimpleName() + ":" + new JSONObject(this).toString();
    }
}
