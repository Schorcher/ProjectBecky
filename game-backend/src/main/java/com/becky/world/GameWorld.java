package com.becky.world;

import com.becky.world.entity.Bullet;
import com.becky.world.physics.BulletCollisionDetector;
import com.becky.world.entity.Player;
import com.becky.WorldBorder;
import com.becky.networked.message.BulletInfo;
import com.becky.networked.message.PlayerHealthMessage;
import com.becky.networked.message.PointsUpdate;
import com.becky.networked.message.ServerPlayerUpdate;
import org.java_websocket.WebSocket;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GameWorld implements Runnable {
    public static final int MAX_TPS = 20;

    private final HashMap<String, Player> players = new HashMap<>();
    private final HashMap<String, Player> deadPlayers = new HashMap<>();
    private final WorldBorder border = new WorldBorder(4000.0f, 4000.0f);
    private final BulletCollisionDetector bulletCollisionDetector = new BulletCollisionDetector();

    public void start() {
        final Thread thread = new Thread(this);
        thread.start();
    }

    private void tick(final long elapsedTime, final Player[] currentPlayers) {
        for(final Player player: currentPlayers) {
            player.tick(elapsedTime);
            border.keepEntityInBorder(player);

            final List<Bullet> bullets = player.getBulletsList();
            for(final Bullet bullet: bullets) {
                border.keepEntityInBorder(bullet);
            }

            final Map<Bullet, Player> playerBulletCollisions = this.bulletCollisionDetector.getBulletCollisions(currentPlayers);
            final Set<Bullet> bulletKeys = playerBulletCollisions.keySet();
            for(final Bullet bullet: bulletKeys) {
                final Player p = playerBulletCollisions.get(bullet);
                p.setHealth(p.getHealth() - bullet.getDamage());

                //If the bullet killed the player, the shooter receives a 10th of the dead player's points
                //Also remove that player from that game
                //Also send a points update message to the scoring player
                if(p.getHealth() == 0) {
                    final Player attacker = bullet.getOwner();
                    attacker.addScore(p.getScore()/10);
                    this.removePlayerByUsername(p.getPlayerUsername());
                    this.deadPlayers.put(p.getPlayerUsername(), p);

                    //send points update to killer
                    final PointsUpdate pointsUpdate = new PointsUpdate();
                    pointsUpdate.setNumPoints(attacker.getScore());
                    pointsUpdate.setUsername(attacker.getPlayerUsername());
                    if(attacker.getConnection().isOpen()) {
                        attacker.getConnection().send(pointsUpdate.jsonSerialize());
                    }

                    //Send bullet updates so clients know to remove all remaining bullets from the player
                    final List<Bullet> deadBullets = p.getBulletsList();
                    for(final Bullet bl: deadBullets) {
                        bl.setState(Bullet.STATE_DEAD_BULLET);
                    }
                    final String jsonMessage = "BulletInfo[]:" + new JSONArray(deadBullets).toString();
                    final Collection<Player> allPlayers = this.getAllPlayers();
                    for(final Player pl: allPlayers) {
                        if(pl.getConnection().isOpen()) {
                            pl.getConnection().send(jsonMessage);
                        }
                    }
                }

                //Send a player health message to the inflicted player
                final PlayerHealthMessage playerHealthMessage = new PlayerHealthMessage();
                playerHealthMessage.setUsername(p.getPlayerUsername());
                playerHealthMessage.setAffectedBy(bullet.getOwner().getPlayerUsername());
                playerHealthMessage.setHealth(p.getHealth());
                if(p.getConnection().isOpen()) {
                    p.getConnection().send(playerHealthMessage.jsonSerialize());
                }
            }
        }
    }

    private void transmit(final Player[] currentPlayers) {
        transmitPlayerUpdates(currentPlayers);
        transmitPlayerBullets(currentPlayers);
    }

    private void transmitPlayerBullets(final Player[] currentPlayers) {
        final List<BulletInfo> bulletInfosList = new ArrayList<>();

        //Get all bullets for all players, and convert them into bullet info objects
        for(final Player player: currentPlayers) {
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
        for(final Player player: currentPlayers) {
            final WebSocket connection = player.getConnection();
            if(connection.isOpen()) {
                connection.send(serializedData);
            }
        }
    }

    private void transmitPlayerUpdates(final Player[] currentPlayers) {
        final int length = currentPlayers.length;
        final ServerPlayerUpdate[] updates = new ServerPlayerUpdate[length];
        for(int i = 0; i < length; i++) {
            updates[i] = new ServerPlayerUpdate(currentPlayers[i]);
        }

        final JSONArray serializedData = new JSONArray(Arrays.asList(updates));
        final String data = ServerPlayerUpdate.class.getSimpleName() + "[]:" + serializedData.toString();
        for(final Player player: currentPlayers) {
            if(player.getConnection().isOpen()) {
                player.getConnection().send(data);
            }
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

            //for thread safety, move the current player list into an array
            final Player[] currentPlayers = new Player[players.values().size()];
            synchronized(players) {
                int index = 0;
                for(final Player player: players.values()) {
                    currentPlayers[index] = player;
                    index++;
                }
            }

            tick(elapsedTime, currentPlayers); //update
            transmit(currentPlayers); //send data to clients

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
        synchronized (this.players) {
            players.put(player.getPlayerUsername().intern(), player);
        }
    }

    public Player getPlayerByUsername(final String userName) {
        return players.get(userName.intern());
    }

    public Player getPlayerByConnection(final WebSocket connection) {
        synchronized (this.players) {
            for (final Player player : players.values()) {
                if (player.getConnection().equals(connection)) {
                    return player;
                }
            }
        }
        synchronized (this.deadPlayers) {
            for(final Player player: deadPlayers.values()) {
                if(player.getConnection().equals(connection)) {
                    return player;
                }
            }
        }
        return null;
    }

    public Collection<Player> getAllPlayers() {
        synchronized (this.players) {
            return new ArrayList<>(this.players.values());
        }
    }

    public void removePlayerByUsername(final String userName){
        synchronized (this.players) {
            if(players.remove(userName.intern()) != null) {
                return;
            }
        }
        synchronized (this.deadPlayers) {
            deadPlayers.remove(userName.intern());
        }
    }
}
