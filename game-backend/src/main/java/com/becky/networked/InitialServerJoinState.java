package com.becky.networked;

import org.json.JSONObject;

public class InitialServerJoinState {
    private String initialUsername;
    private String authenticationString;
    private float initialLocationX;
    private float initialLocationY;

    public InitialServerJoinState(final String json) {
        final int jsonStartIndex = json.indexOf('{');
        if(jsonStartIndex == -1) {
            throw new IllegalArgumentException("Invalid JSON Object");
        }
        if(!json.startsWith(InitialServerJoinState.class.getSimpleName())) {
            throw new IllegalArgumentException("The given JSON object is not a valid InitialServerJoinState object.");
        }

        final JSONObject obj = new JSONObject(json.substring(jsonStartIndex));
        this.initialUsername = obj.getString("initialUsername");
        this.authenticationString = obj.getString("authenticationString");
        this.initialLocationX = obj.getInt("initialLocationX");
        this.initialLocationY = obj.getInt("initialLocationY");
    }

    public InitialServerJoinState() {}

    public String getInitialUsername() {
        return initialUsername;
    }

    public void setInitialUsername(final String initialUsername) {
        this.initialUsername = initialUsername;
    }

    public String getAuthenticationString() {
        return authenticationString;
    }

    public void setAuthenticationString(final String authenticationString) {
        this.authenticationString = authenticationString;
    }

    public float getInitialLocationX() {
        return initialLocationX;
    }

    public void setInitialLocationX(final float initialLocationX) {
        this.initialLocationX = initialLocationX;
    }

    public float getInitialLocationY() {
        return initialLocationY;
    }

    public void setInitialLocationY(final float initialLocationY) {
        this.initialLocationY = initialLocationY;
    }

    public String jsonSerialize() {
        return InitialServerJoinState.class.getSimpleName() + ":" + new JSONObject(this).toString();
    }
}
