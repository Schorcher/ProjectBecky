package com.becky;

import com.becky.networking.SimpleServer;
import com.becky.world.NewGameWorld;

import java.net.InetSocketAddress;

/**
 * The main application class of the game.
 * Created by chunsinger on 9/8/2017.
 */
public class Becky {

    public static void main(final String[] args) {
        final InetSocketAddress socketAddress = new InetSocketAddress(3000);
        final NewGameWorld game = new NewGameWorld();
        final SimpleServer simpleServer = new SimpleServer(socketAddress, game);

        simpleServer.start();
        game.start();
    }
}
