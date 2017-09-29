package com.becky.networked;

import org.json.JSONObject;

public class InitialServerJoinState {
    private String initialUsername;
    private String authenticationString;
    private int initialLocationX;
    private int initialLocationY;

    public InitialServerJoinState(final String json) {
        if(!json.startsWith(InitialServerJoinState.class.getSimpleName())) {
            throw new IllegalArgumentException("The given JSON object is not a valid InitialServerJoinState object.");
        }

        final JSONObject obj = new JSONObject(json);
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

    public int getInitialLocationX() {
        return initialLocationX;
    }

    public void setInitialLocationX(final int initialLocationX) {
        this.initialLocationX = initialLocationX;
    }

    public int getInitialLocationY() {
        return initialLocationY;
    }

    public void setInitialLocationY(final int initialLocationY) {
        this.initialLocationY = initialLocationY;
    }

    public String jsonSerialize() {
        return InitialServerJoinState.class.getSimpleName() + ":" + new JSONObject(this).toString();
    }
}
