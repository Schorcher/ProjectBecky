package com.becky.networking.message;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class ServerPlayerUpdate implements NetworkedMessage {
    private float posX;
    private float posY;
    private float velX;
    private float velY;
    private float accelX;
    private float accelY;
    private float angle;
    private String playerName;

    public ServerPlayerUpdate() {}

    public float getPosX() {
        return posX;
    }

    public void setPosX(final float posX) {
        this.posX = posX;
    }

    public float getPosY() {
        return posY;
    }

    public void setPosY(final float posY) {
        this.posY = posY;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setVelX(final float velocity) {
        this.velX = velocity;
    }

    public float getVelX() {
        return this.velX;
    }

    public void setVelY(final float velocity) {
        this.velY = velocity;
    }

    public float getVelY() {
        return this.velY;
    }

    public void setAccelX(final float acceleration) {
        this.accelX = acceleration;
    }

    public float getAccelX() {
        return this.accelX;
    }

    public void setAccelY(final float acceleration) {
        this.accelY = acceleration;
    }

    public float getAccelY() {
        return this.accelY;
    }

    public void setPlayerName(final String playerName) {
        this.playerName = playerName;
    }

    public float getAngle() {
        return this.angle;
    }

    public void setAngle(final float angle) {
        this.angle = angle;
    }

    @Override
    public String jsonSerialize() {
        return ServerPlayerUpdate.class.getSimpleName() + ":" + new JSONObject(this).toString();
    }

    public static String jsonSerializeAll(final List<ServerPlayerUpdate> updates) {
        return ServerPlayerUpdate.class.getSimpleName() + "[]:" + new JSONArray(updates).toString();
    }
}
