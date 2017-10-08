package com.becky.networking.message;

import org.json.JSONObject;

/**
 * A message indicating a points update.
 * Created by Clayton on 10/4/2017.
 */
public class PointsUpdate implements NetworkedMessage {
    private String username;
    private int numPoints;

    public void setUsername(final String value) {
        this.username = value;
    }

    public void setNumPoints(final int value) {
        this.numPoints = value;
    }

    public String getUsername() {
        return this.username;
    }

    public int getNumPoints() {
        return this.numPoints;
    }

    public String jsonSerialize() {
        return PointsUpdate.class.getSimpleName() + new JSONObject(this).toString();
    }
}
