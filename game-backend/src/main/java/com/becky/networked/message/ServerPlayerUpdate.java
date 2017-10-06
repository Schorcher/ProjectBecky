package com.becky.networked.message;

import com.becky.world.entity.Player;
import org.json.JSONObject;

public class ServerPlayerUpdate
{
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

    public ServerPlayerUpdate(final Player player) {
        this.posX = player.getXPosition();
        this.posY = player.getYPosition();
        this.playerName = player.getPlayerUsername();
    }

    public float getPosX() {
        return posX;
    }

    public void setPosX(final int posX) {
        this.posX = posX;
    }

    public float getPosY() {
        return posY;
    }

    public void setPosY(final int posY) {
        this.posY = posY;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(final String playerName) {
        this.playerName = playerName;
    }

    public String jsonSerialize() {
        return ServerPlayerUpdate.class.getSimpleName() + ":" + new JSONObject(this).toString();
    }
}
