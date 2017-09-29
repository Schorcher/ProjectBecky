package com.becky;

import com.becky.networked.InitialServerJoinState;
import com.becky.networked.InputStateChange;
import com.becky.networked.ServerUsernameRequestStatus;
import com.becky.networked.UsernameChangeRequest;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;

import java.net.InetSocketAddress;

public class SimpleServer extends WebSocketServer {
    private final Becky gameInstance;

    public SimpleServer(final InetSocketAddress addr, final Becky gameInstance){
        super(addr);
        this.gameInstance = gameInstance;
    }

    @Override
    public void onError(final WebSocket webSocket, final Exception e) {
        System.out.println("It done fucked up. Here's your info:" + e.toString());
    }

    @Override
    public void onStart() {
        System.out.println("Yay it started..... \nHopefully you have no errors......\nHave fun....");
    }

    @Override
    public void onClose(final WebSocket webSocket, final int i, final String s, final boolean val) {
        final Player player = gameInstance.getPlayerByConnection(webSocket);
        if(player == null) {
            System.out.println("Unknown player disconnected. Reason: " + s);
        }
        else {
            gameInstance.removePlayerByUsername(player.getPlayerUsername());
            System.out.println("Player " + player.getPlayerUsername() + " disconnected. Reason: " + s);
        }
    }

    @Override
    public void onOpen(final WebSocket webSocket, final ClientHandshake clientHandshake) {
        final InetSocketAddress remoteAddress = webSocket.getRemoteSocketAddress();
        final String ip = remoteAddress.getHostName();
        final int port = remoteAddress.getPort();
        System.out.println(String.format("Connection received from: %s:%d", ip, port));

        //Create and add the player to the game
        final String username = generateRandomUsername();
        final String auth = generateAuthToken();
        final Player player = new Player(username, auth, webSocket);
        gameInstance.addPlayer(player);

        //Setup the initial join state of the player
        final InitialServerJoinState initialJoinState = new InitialServerJoinState();
        initialJoinState.setAuthenticationString(auth);
        initialJoinState.setInitialUsername(username);
        initialJoinState.setInitialLocationX(player.getX_position());
        initialJoinState.setInitialLocationY(player.getY_position());

        //json serialize and transmit the initial join state to the client
        webSocket.send(initialJoinState.jsonSerialize());
        System.out.println("On open finished...");
    }

    @Override
    public void onMessage(final WebSocket webSocket, final String message) {
        System.out.println(message);
        //is this a username change request
        if(message.startsWith(UsernameChangeRequest.class.getSimpleName())) {
            System.out.println("Username Change Request");
            final UsernameChangeRequest request = new UsernameChangeRequest(message);
            final Player player = gameInstance.getPlayerByUsername(request.getOldUsername());
            if(!player.getAuthenticationString().equals(request.getAuthenticationString())) {
                System.out.println("Bad Auth string..");
                return; //bad authentication string
            }
            final ServerUsernameRequestStatus status = new ServerUsernameRequestStatus();
            if(gameInstance.getPlayerByUsername(request.getNewUsername()) == null) {
                gameInstance.removePlayerByUsername(request.getOldUsername());
                player.setPlayerUsername(request.getNewUsername());
                gameInstance.addPlayer(player);
                status.setStatus("success");
                status.setMessage(request.getNewUsername());
            }
            else {
                status.setStatus("failed");
                status.setMessage("Username already exists.");
            }
            webSocket.send(status.jsonSerialize());
        }
        else if(message.startsWith(InputStateChange.class.getSimpleName())){
            final InputStateChange stateChange = new InputStateChange(message);
            final Player player = gameInstance.getPlayerByUsername(stateChange.getUsername());
            if(player == null || !player.getAuthenticationString().equals(stateChange.getAuthenticationString())) {
                return; //bad username or bad authentication string
            }

            //update player state based on input
            updatePlayerState(player, stateChange);
        }
    }

    private void updatePlayerState(final Player player, final InputStateChange stateChange) {
        final String inputKey = stateChange.getInputName();
        boolean meHandleIt = false;
        if("w".equals(inputKey)) {
            player.setY_acceleration(-Player.ACCELERATION);
            meHandleIt = true;
        }
        else if("s".equals(inputKey)) {
            player.setY_acceleration(Player.ACCELERATION);
            meHandleIt = true;
        }
        if("a".equals(inputKey)) {
            player.setX_acceleration(-Player.ACCELERATION);
            meHandleIt = true;
        }
        else if("d".equals(inputKey)) {
            player.setX_acceleration(Player.ACCELERATION);
            meHandleIt = true;
        }

        if(meHandleIt) {
            player.setDecelerating(!stateChange.isFlag());
        }

        //TODO: In phase 2 update player angles and whether or not player is shooting
    }

    private static final String CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private String generateAuthToken() {
        final StringBuilder builder = new StringBuilder();
        for(int i = 0; i < 19; i++) {
            if(i % 4 == 0) {
                builder.append('-');
            }
            else {
                final int rnd = (int)Math.round(Math.random() * 62);
                builder.append(CHARACTERS.charAt(rnd));
            }
        }
        return builder.toString();
    }

    private String generateRandomUsername() {
        int random = 0;
        while(gameInstance.getPlayerByUsername("unnamed" + random) != null) {
            random = (int)Math.round(Math.random() * 999);
        }
        return "unnamed" + random;
    }
}
