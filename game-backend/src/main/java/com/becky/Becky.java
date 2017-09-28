package com.becky;

import com.becky.networked.ServerPlayerUpdate;
import org.java_websocket.WebSocket;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

/**
 * The main application class of the game.
 * Created by chunsinger on 9/8/2017.
 */
public class Becky implements Runnable {
    public static final int MAX_TPS = 20;

    private Thread thread;
    private final HashMap<String, Player> players = new HashMap<>();
    private final WorldBorder border = new WorldBorder(2000.0f, 2000.0f);


    public static void main(final String[] args) {
        final InetSocketAddress socketAddress = new InetSocketAddress(3000);
        final Becky game = new Becky();
        final SimpleServer simpleServer = new SimpleServer(socketAddress, game);

        simpleServer.start();
        game.start();
    }

    public void start() {
        thread = new Thread(this);
        thread.start();
    }

    private void tick(final long elapsedTime) {
        for(final Player player: players.values()) {
            player.tick(elapsedTime);
            border.keepEntityInBorder(player);
        }
    }

    private void transmit() {
        final int length = players.values().size();
        Player[] values = new Player[length];
        values = players.values().toArray(values);
        final ServerPlayerUpdate[] updates = new ServerPlayerUpdate[length];
        for(int i = 0; i < length; i++) {
            updates[i] = new ServerPlayerUpdate(values[i]);
        }

        final JSONArray serializedData = new JSONArray(Arrays.asList(updates));
        final String data = serializedData.toString();
        for(final Player player: values) {
            player.getConnection().send(data);
        }
    }

    @Override
    public void run() {
        long frameStart = System.currentTimeMillis();
        long frameEnd = frameStart;
        long elapsedTime;

        while(true) {
            elapsedTime = frameEnd - frameStart;
            frameStart = System.currentTimeMillis();

            tick(elapsedTime); //update
            transmit(); //send data to clients

            //see if we need to sleep
            //sleep if necessary
            frameEnd = System.currentTimeMillis();
            elapsedTime = frameEnd - frameStart;
            if(elapsedTime < 1000/MAX_TPS - 2) {
                try {
                    Thread.sleep(1000/MAX_TPS - elapsedTime - 2);
                } catch(final InterruptedException ignored) {}
                frameEnd = System.currentTimeMillis();
            }
        }
    }

    public void addPlayer(final Player player) {
        players.put(player.getPlayerUsername().intern(), player);
    }

    public Player getPlayerByUsername(final String userName) {
        return players.get(userName.intern());
    }

    public Player getPlayerByConnection(final WebSocket connection) {
        for (final Player player: players.values()) {
            if(player.getConnection().equals(connection)) {
                return player;
            }
        }
        return null;
    }

    public void removePlayerByUsername(final String userName){
        players.remove(userName.intern());
    }
}
