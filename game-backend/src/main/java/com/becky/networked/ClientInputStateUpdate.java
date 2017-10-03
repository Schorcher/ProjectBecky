package com.becky.networked;

import org.json.JSONObject;

/**
 * Represents an input state change message from the client to the server.
 */
public class ClientInputStateUpdate {
    private final String username;
    private final String authString;
    private final boolean movingUp;
    private final boolean movingLeft;
    private final boolean movingRight;
    private final boolean movingDown;
    private final boolean shooting;
    private final float angle;

    public ClientInputStateUpdate(final String json) {
        final int jsonStartIndex = json.indexOf('{');
        if(jsonStartIndex == -1) {
            throw new IllegalArgumentException("Invalid JSON Object");
        }
        if(!json.startsWith(ClientInputStateUpdate.class.getSimpleName())) {
            throw new IllegalArgumentException("The given JSON object is not a valid ClientInputStateUpdate object.");
        }

        final JSONObject obj = new JSONObject(json.substring(jsonStartIndex));
        movingUp = obj.optBoolean("movingUp", false);
        movingDown = obj.optBoolean("movingDown", false);
        movingLeft = obj.optBoolean("movingLeft", false);
        movingRight = obj.optBoolean("movingRight", false);
        shooting = obj.optBoolean("shooting", false);
        angle = (float)obj.optDouble("angle", 0.0);
        username = obj.getString("username");
        authString = obj.getString("authString");
    }

    public boolean isMovingUp() {
        return movingUp;
    }

    public boolean isMovingLeft() {
        return movingLeft;
    }

    public boolean isMovingRight() {
        return movingRight;
    }

    public boolean isMovingDown() {
        return movingDown;
    }

    public boolean isShooting() {
        return shooting;
    }

    public float getAngle() {
        return angle;
    }

    public String getUsername() {
        return this.username;
    }

    public String getAuthString() {
        return this.authString;
    }
}
