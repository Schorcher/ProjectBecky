package com.becky.networked;

import org.json.JSONObject;

/**
 * The message sent to clients regarding changes in player health
 * Created by Clayton on 10/3/2017.
 */
public class PlayerHealthMessage implements NetworkedMessage {
    private String username;
    private String affectedBy;
    private float health;

    public String getUsername() {
        return this.username;
    }

    public String getAffectedBy() {
        return this.affectedBy;
    }

    public float getHealth() {
        return this.health;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public void setAffectedBy(final String affectedBy) {
        this.affectedBy = affectedBy;
    }

    public void setHealth(final float health) {
        this.health = health;
    }

    @Override
    public String jsonSerialize() {
        return PlayerHealthMessage.class.getSimpleName() + ":" + new JSONObject(this).toString();
    }
}
