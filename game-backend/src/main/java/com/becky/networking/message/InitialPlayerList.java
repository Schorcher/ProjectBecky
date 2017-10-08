package com.becky.networking.message;

import org.json.JSONObject;

import java.util.List;

public class InitialPlayerList implements NetworkedMessage{
    private List<ServerPlayerUpdate> players;

    public List<ServerPlayerUpdate> getPlayers() {
        return this.players;
    }

    public void setPlayers(final List<ServerPlayerUpdate> players) {
        this.players = players;
    }

    @Override
    public String jsonSerialize() {
        return InitialPlayerList.class.getSimpleName() + ":" + new JSONObject(this).toString();
    }
}
