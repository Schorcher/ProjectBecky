package com.becky.networking.message;

import org.json.JSONObject;

public class PlayerListChange implements NetworkedMessage{
    private String username;
    private boolean joined;

    public PlayerListChange() {}

    public PlayerListChange(final String json) {
        final int jsonStartIndex = json.indexOf('{');
        if(jsonStartIndex == -1) {
            throw new IllegalArgumentException("Invalid JSON Object");
        }
        if(!json.startsWith(PlayerListChange.class.getSimpleName())) {
            throw new IllegalArgumentException("Json object does not define a PlayerListChange object.");
        }

        final JSONObject obj = new JSONObject(json.substring(jsonStartIndex));
        this.username = obj.getString("username");
        this.joined = obj.getBoolean("joined");
    }

    public String getUsername() {
        return this.username;
    }

    public boolean isJoined() {
        return this.joined;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public void setJoined(final boolean joined) {
        this.joined = joined;
    }

    @Override
    public String jsonSerialize() {
        return PlayerListChange.class.getSimpleName() + ":" + new JSONObject(this).toString();
    }
}
