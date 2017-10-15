package com.becky.networking.message;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class NpcInfo implements NetworkedMessage {
    private String type; //the classname of the npc type. Doesn't need to be set if state != NPC_STATE_NEW
    private int npcId;
    private float positionX;
    private float positionY;
    private float angle;
    private int health;
    private int state;

    public void setType(final String type) {
        this.type = type;
    }

    public void setNpcId(final int npcId) {
        this.npcId = npcId;
    }

    public void setPositionX(final float positionX) {
        this.positionX = positionX;
    }

    public void setPositionY(final float positionY) {
        this.positionY = positionY;
    }

    public void setAngle(final float angle) {
        this.angle = angle;
    }

    public void setHealth(final int health) {
        this.health = health;
    }

    public void setState(final int state) {
        this.state = state;
    }

    public String getType() {
        return this.type;
    }

    public int getNpcId() {
        return this.npcId;
    }

    public float getPositionX() {
        return this.positionX;
    }

    public float getPositionY() {
        return this.positionY;
    }

    public float getAngle() {
        return this.angle;
    }

    public int getHealth() {
        return this.health;
    }

    public int getState() {
        return this.state;
    }

    @Override
    public String jsonSerialize() {
        return NpcInfo.class.getSimpleName() + ":" + new JSONObject(this).toString();
    }

    public static String jsonSerializeAll(final List<NpcInfo> npcInfoList) {
        return NpcInfo.class.getSimpleName() + "[]:" + new JSONArray(npcInfoList).toString();
    }
}
