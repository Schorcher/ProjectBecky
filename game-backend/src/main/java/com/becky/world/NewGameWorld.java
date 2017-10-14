package com.becky.world;


import com.becky.networking.PlayerMessageTransmitter;
import com.becky.networking.message.*;
import com.becky.world.entity.Bullet;
import com.becky.world.entity.GameEntity;
import com.becky.world.entity.Player;
import com.becky.world.entity.npc.NpcSpawner;
import com.becky.world.physics.BulletCollisionDetector;
import com.becky.world.physics.PhysicsFilter;
import com.becky.world.physics.PlayerCollisionDetector;
import com.becky.world.physics.WorldBorderCollisionDetector;
import org.java_websocket.WebSocket;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class NewGameWorld implements Runnable {
    private static final int MAX_TPS = 20;
    private static final long TIME_PER_TICK = (long) (1000.0f / MAX_TPS);

    private final HashMap<String, Player> players = new HashMap<>();
    private final HashMap<String, Player> deadPlayers = new HashMap<>();
    private final List<GameEntity> gameEntities = new ArrayList<>();
    private final List<PhysicsFilter> physicsFilters = new ArrayList<>();
    private final PlayerMessageTransmitter messageTransmitter = new PlayerMessageTransmitter();
    private final List<WorldEventListener> worldEventListeners = new ArrayList<>();
    private final NpcSpawner spawner = new NpcSpawner(this);

    public NewGameWorld() {
        physicsFilters.add(new BulletCollisionDetector(this));
        physicsFilters.add(new WorldBorderCollisionDetector(8000.0f, 8000.0f));
        physicsFilters.add(new PlayerCollisionDetector(this));
    }

    public void start() {
        final Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        long frameStart = System.currentTimeMillis();
        long frameEnd = frameStart;
        long elapsedTime;

        while(true) {
            elapsedTime = frameEnd - frameStart;
            frameStart = System.currentTimeMillis();

            //for thread safety get a copy of the game entities list
            final List<GameEntity> entities = this.getAllGameEntities();

            //update game entities
            tick(entities, elapsedTime);
            //apply relevant physics to entities
            applyPhysics(entities);
            //transmit entity details
            transmit(entities);
            //spawn npcs as necessary
            spawner.executeSpawnRules();

            //see if we need to sleep
            //sleep if necessary
            frameEnd = System.currentTimeMillis();
            elapsedTime = frameEnd - frameStart;
            if(elapsedTime < TIME_PER_TICK - 2) {
                try {
                    Thread.sleep(TIME_PER_TICK - elapsedTime - 2);
                } catch(final InterruptedException ignored) {}
            }
            frameEnd = System.currentTimeMillis();
        }
    }

    private void tick(final List<GameEntity> entities, final long elapsedTime) {
        for(final GameEntity entity: entities) {
            entity.tick(elapsedTime);
        }
    }

    private void applyPhysics(final List<GameEntity> entities) {
        for(final PhysicsFilter filter: physicsFilters) {
            filter.prepare();
            for(final GameEntity entity: entities) {
                filter.apply(entity);
            }
        }
    }

    private void transmit(final List<GameEntity> entities) {
        final List<ServerPlayerUpdate> playerUpdates = new ArrayList<>();
        final List<BulletInfo> bulletUpdates = new ArrayList<>();

        for(final GameEntity entity: entities) {
            if(entity instanceof Player) {
                final Player player = (Player)entity;
                final ServerPlayerUpdate update = new ServerPlayerUpdate();
                update.setPlayerName(player.getPlayerUsername());
                update.setPosX(player.getXPosition());
                update.setPosY(player.getYPosition());
                playerUpdates.add(update);

                if(player.isPlayerHealthUpdated()) {
                    if(player.getHealth() <= 0) {
                        removePlayerByUsername(player.getPlayerUsername());
                        synchronized (this.deadPlayers) {
                            deadPlayers.put(player.getPlayerUsername(), player);
                        }
                    }
                    final PlayerHealthMessage healthUpdate = new PlayerHealthMessage();
                    healthUpdate.setAffectedBy(player.getHealthAffectedBy());
                    healthUpdate.setHealth(player.getHealth());
                    healthUpdate.setUsername(player.getPlayerUsername());
                    messageTransmitter.transmitMessage(player, healthUpdate.jsonSerialize());
                }
                if(player.isPlayerScoreUpdated()) {
                    final PointsUpdate pointsUpdate = new PointsUpdate();
                    pointsUpdate.setNumPoints(player.getScore());
                    pointsUpdate.setUsername(player.getPlayerUsername());
                    messageTransmitter.transmitMessage(player, pointsUpdate.jsonSerialize());
                }
                player.resetStatusUpdateFlags();
            }
            else if(entity instanceof Bullet) {
                final Bullet bullet = (Bullet)entity;
                final int bulletState = bullet.getState();

                if(bulletState == Bullet.STATE_DEAD_BULLET) {
                    final BulletInfo info = new BulletInfo(
                        null, Bullet.STATE_DEAD_BULLET, bullet.getEntityId(), null, null, null, null);
                    bulletUpdates.add(info);
                }
                else if(bulletState == Bullet.STATE_NEW_BULLET) {
                    final BulletInfo info = new BulletInfo(
                        bullet.getOwner().getPlayerUsername(),
                        Bullet.STATE_NEW_BULLET,
                        bullet.getEntityId(),
                        bullet.getXVelocity(), bullet.getYVelocity(),
                        bullet.getXPosition(), bullet.getYPosition());
                    bulletUpdates.add(info);
                }
                else if(bulletState == Bullet.STATE_UPDATED_BULLET) {
                    final BulletInfo info = new BulletInfo(null, Bullet.STATE_UPDATED_BULLET, bullet.getEntityId(),
                        null, null, bullet.getXPosition(), bullet.getYPosition());
                    bulletUpdates.add(info);
                }
            }
        }

        final String playerUpdatesMessage = ServerPlayerUpdate.jsonSerializeAll(playerUpdates);
        final String bulletUpdatesMessage = BulletInfo.jsonSerialize(bulletUpdates);
        final Collection<Player> allPlayers = getAllPlayers();
        for(final Player player: allPlayers) {
            messageTransmitter.transmitMessage(player, playerUpdatesMessage);
            messageTransmitter.transmitMessage(player, bulletUpdatesMessage);
        }
    }

    public void addPlayer(final Player player) {
        synchronized (this.players) {
            players.put(player.getPlayerUsername(), player);
        }

        this.addGameEntity(player);
    }

    public void addGameEntity(final GameEntity entity) {
        synchronized (this.gameEntities) {
            gameEntities.add(entity);
        }

        synchronized (this.worldEventListeners) {
            for(final WorldEventListener listener: worldEventListeners) {
                listener.onGameEntityAdded(this, entity);
            }
        }
    }

    public void removeGameEntity(final GameEntity entity) {
        synchronized (this.gameEntities) {
            gameEntities.remove(entity);
        }

        synchronized (this.worldEventListeners) {
            for(final WorldEventListener listener: worldEventListeners) {
                listener.onGameEntityRemoved(this, entity);
            }
        }
    }

    public List<GameEntity> getAllGameEntities() {
        synchronized (this.gameEntities) {
            return new ArrayList<>(gameEntities);
        }
    }

    public Collection<Player> getAllPlayers() {
        synchronized (this.players) {
            return new ArrayList<>(players.values());
        }
    }

    public Player getPlayerByUsername(final String username) {
        return players.get(username.intern());
    }

    public Player getPlayerByConnection(final WebSocket connection) {
        synchronized (this.players) {
            for(final Player player: players.values()) {
                if(player.getConnection().equals(connection)) {
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

    public void removePlayerByUsername(final String username) {
        final String internUsername = username.intern();
        Player player;

        synchronized (this.players) {
            player = players.remove(internUsername);
        }

        if(player == null) {
            synchronized (this.deadPlayers) {
                player = deadPlayers.remove(internUsername);
            }
        }

        this.removeGameEntity(player);
    }

    public PlayerMessageTransmitter getMessageTransmitter() {
        return this.messageTransmitter;
    }

    public void addWorldEventListener(final WorldEventListener listener) {
        synchronized (this.worldEventListeners) {
            worldEventListeners.add(listener);
        }
    }

    public void removeWorldEventListener(final WorldEventListener listener) {
        synchronized (this.worldEventListeners) {
            worldEventListeners.remove(listener);
        }
    }
}
