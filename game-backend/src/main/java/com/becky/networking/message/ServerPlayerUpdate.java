package com.becky.networking.message;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class ServerPlayerUpdate implements NetworkedMessage {
    private float posX;
    private float posY;
    private String playerName;

    public ServerPlayerUpdate() {}

    public ServerPlayerUpdate(final String json) {
        final int jsonStartIndex = json.indexOf('{');
        if(jsonStartIndex == -1) {
            throw new IllegalArgumentException("Invalid JSON Object");
        }
        if(!json.startsWith(ServerPlayerUpdate.class.getSimpleName())) {
            throw new IllegalArgumentException("Json object does not define a ServerPlayerUpdate object.");
        }

        final JSONObject obj = new JSONObject(json.substring(jsonStartIndex));
        this.posX = obj.getInt("posX");
        this.posY = obj.getInt("posY");
        this.playerName = obj.getString("playerName");
        //TODO: Update this to accommodate for new fields in future phases
    }

    //TODO: Phase 2 uncomment the following fields
    //private accelX: number;
    //private accelY: number;
    //private velX: number;
    //private velY: number;

    //TODO: Phase 2 uncomment the following methods
    //public getVelocity(): Point {
    //    return new Point(this.velX, this.velY);
    //}

    //public getAcceleration(): Point {
    //    return new Point(this.accelX, this.accelY);
    //}

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

    public void setPlayerName(final String playerName) {
        this.playerName = playerName;
    }

    @Override
    public String jsonSerialize() {
        return ServerPlayerUpdate.class.getSimpleName() + ":" + new JSONObject(this).toString();
    }

    public static String jsonSerializeAll(final List<ServerPlayerUpdate> updates) {
        return ServerPlayerUpdate.class.getSimpleName() + "[]:" + new JSONArray(updates).toString();
    }
}
