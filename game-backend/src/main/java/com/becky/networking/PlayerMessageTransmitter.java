package com.becky.networking;

import com.becky.world.entity.Player;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Message transmitter designed to transmit messages to players.
 * Created by Clayton on 10/10/2017.
 */
public class PlayerMessageTransmitter {
    private final ExecutorService threadPool = Executors.newFixedThreadPool(10);

    public void transmitMessage(final Player client, final String message) {
        threadPool.submit(() -> {
            if(client.getConnection().isOpen()) {
                client.getConnection().send(message);
            }
        });
    }
}
