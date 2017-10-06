package com.becky.networked.message;

public class InitialPlayerList {
    private ServerPlayerUpdate[] players;

    public ServerPlayerUpdate[] getPlayers() {
        return this.players;
    }

    public void setPlayers(final ServerPlayerUpdate[] players) {
        this.players = players;
    }
}
