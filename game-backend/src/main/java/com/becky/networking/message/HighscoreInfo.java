package com.becky.networking.message;

import org.json.JSONObject;

public class HighscoreInfo implements NetworkedMessage {
    private String[] players;
    private int[] scores;

    public String[] getPlayers() {
        return this.players;
    }

    public int[] getScores() {
        return this.scores;
    }

    public void setPlayers(final String... players) {
        this.players = players;
    }

    public void setScores(final int... scores) {
        this.scores = scores;
    }

    @Override
    public String jsonSerialize() {
        return HighscoreInfo.class.getSimpleName() + ":" + new JSONObject(this).toString();
    }
}
