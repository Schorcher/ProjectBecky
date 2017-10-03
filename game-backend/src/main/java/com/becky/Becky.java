package com.becky;

import com.becky.networked.BulletInfo;
import com.becky.networked.ServerPlayerUpdate;
import org.java_websocket.WebSocket;
import org.json.JSONArray;

import java.net.InetSocketAddress;
import java.util.*;

/**
 * The main application class of the game.
 * Created by chunsinger on 9/8/2017.
 */
public class Becky implements Runnable {
    public static final int MAX_TPS = 20;

    private Thread thread;
    private final HashMap<String, Player> players = new HashMap<>();
    private final WorldBorder border = new WorldBorder(4000.0f, 4000.0f);


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

            final List<Bullet> bullets = player.getBulletsList();
            for(final Bullet bullet: bullets) {
                border.keepEntityInBorder(bullet);
            }
        }
    }

    private void transmit() {
        transmitPlayerUpdates();
        transmitPlayerBullets();
    }

    private void transmitPlayerBullets() {
        final List<BulletInfo> bulletInfosList = new ArrayList<>();

        //Get all bullets for all players, and convert them into bullet info objects
        final Collection<Player> allPlayers = players.values();
        for(final Player player: allPlayers) {
            final String username = player.getPlayerUsername();
            final List<Bullet> bullets = player.getBulletsList();
            for(final Bullet bullet: bullets) {
                final int bulletState = bullet.getState();
                final BulletInfo info;
                if(bulletState == Bullet.STATE_NEW_BULLET) {
                    //Since this is a new bullet, the client will need to know everything. That means the
                    //client needs to know this is a new bullet, needs to know the owner of the bullet,
                    //the id, position, and velocity of the bullet. All need to be known by the client.
                    info = new BulletInfo(username, Bullet.STATE_NEW_BULLET, bullet.getBulletId(),
                        bullet.getXVelocity(), bullet.getYVelocity(), bullet.getXPosition(), bullet.getYPosition());
                }
                else if(bulletState == Bullet.STATE_DEAD_BULLET) {
                    //We use null for position and velocity because all the client needs to know
                    //is that the bullet is dead and should be removed from the game.
                    //All the client needs to know is the bullet id and that the bullet state is dead
                    info = new BulletInfo(null, Bullet.STATE_DEAD_BULLET, bullet.getBulletId(),
                        null, null, null, null);

                    //Also since the bullet is dead, and the BulletInfo is obtained, we can go ahead and remove
                    //the bullet from the player now.
                    player.removeBullet(bullet);
                }
                else {
                    //Since bullets fly at a constant velocity, the client doesn't need to know the velocity
                    //of the bullet in a basic bullet update. The client already also knows the owner of the
                    //bullet, so it doesn't need that either.
                    //All the client needs is the bullet state (update), the bullet id, and the bullet position.
                    info = new BulletInfo(null, Bullet.STATE_UPDATED_BULLET, bullet.getBulletId(),
                        null, null, bullet.getXPosition(), bullet.getYPosition());
                }
                bulletInfosList.add(info);
            }
        }

        if(bulletInfosList.size() < 1) {
            return;
        }

        //now that all bullet infos are obtained, it is time to json serialize the array
        final JSONArray jsonBulletInfos = new JSONArray(bulletInfosList);
        final String serializedData = "BulletInfo[]:" + jsonBulletInfos.toString();

        //transmit the serialized data to all players
        for(final Player player: allPlayers) {
            final WebSocket connection = player.getConnection();
            if(connection.isOpen()) {
                connection.send(serializedData);
            }
        }
    }

    private void transmitPlayerUpdates() {
        final int length = players.values().size();
        Player[] values = new Player[length];
        values = players.values().toArray(values);
        final ServerPlayerUpdate[] updates = new ServerPlayerUpdate[length];
        for(int i = 0; i < length; i++) {
            updates[i] = new ServerPlayerUpdate(values[i]);
        }

        final JSONArray serializedData = new JSONArray(Arrays.asList(updates));
        final String data = ServerPlayerUpdate.class.getSimpleName() + "[]:" + serializedData.toString();
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

    public Collection<Player> getAllPlayers() {
        return this.players.values();
    }

    public void removePlayerByUsername(final String userName){
        players.remove(userName.intern());
    }
}
