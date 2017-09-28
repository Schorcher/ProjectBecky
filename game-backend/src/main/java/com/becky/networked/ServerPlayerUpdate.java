package com.becky.networked;

import com.becky.Player;

public class ServerPlayerUpdate
{
    private int posX;
    private int posY;
    private String playerName;

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
        this.posX = player.getX_position();
        this.posY = player.getY_position();
        this.playerName = player.getPlayerUsername();
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(final int posX) {
        this.posX = posX;
    }

    public int getPosY() {
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
}
