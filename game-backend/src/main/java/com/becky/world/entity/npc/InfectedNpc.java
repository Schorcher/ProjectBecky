package com.becky.world.entity.npc;

import com.becky.util.MathUtils;
import com.becky.world.NewGameWorld;
import com.becky.world.WorldEventListener;
import com.becky.world.entity.GameEntity;
import com.becky.world.entity.Player;

import java.util.Collection;
import java.util.List;

public class InfectedNpc extends Npc implements WorldEventListener {
    private static final float MAX_VIEW_DISTANCE = 2048.0f;
    private static final float ACCELERATION = Player.ACCELERATION;

    private Player trackedPlayer;

    public InfectedNpc(final NewGameWorld world, final Player trackedPlayer) {
        super(world);
        this.trackedPlayer = trackedPlayer;
        super.maxVelocity = Player.MAX_VELOCITY / 3.0f;
        super.collisionRadius = 16;
        super.npcHealth = 15;
        super.pointsValue = 25;
        world.addWorldEventListener(this);
    }

    @Override
    public void tick(final long elapsedTime) {
        if(trackedPlayer == null) {
            trackedPlayer = findClosestPlayer();
        }

        if(trackedPlayer != null) {
            final float deltaX = this.trackedPlayer.getXPosition() - super.position.x;
            final float deltaY = this.trackedPlayer.getYPosition() - super.position.y;
            final float angle = (float)StrictMath.atan2(deltaY, deltaX);

            super.acceleration.x = ACCELERATION * (float)StrictMath.cos(angle);
            super.acceleration.y = ACCELERATION * (float)StrictMath.sin(angle);
        }
        else {
            super.acceleration.x = 0.0f;
            super.acceleration.y = 0.0f;
        }

        super.tick(elapsedTime);
    }

    @Override
    public void onGameEntityRemoved(final NewGameWorld gameWorld, final GameEntity entity) {
        if(this.trackedPlayer != null && trackedPlayer.equals(entity)) {
            this.trackedPlayer = null;
        }
    }

    @Override
    public void onGameEntityAdded(final NewGameWorld gameWorld, final GameEntity entity) {}

    @Override
    public void setNpcHealth(final int health) {
        if(health <= 0) {
            this.onHealthZero();
        }
        super.setNpcHealth(health);
    }

    private Player findClosestPlayer() {
        final Collection<Player> players = super.getGameWorld().getAllPlayers();
        float distance = Float.MAX_VALUE;
        Player closestPlayer = null;
        for(final Player player: players) {
            final float currentDistance = MathUtils.distance(player.getXPosition(), player.getYPosition(), super.getXPosition(), super.getYPosition());
            if(currentDistance < distance && currentDistance < InfectedNpc.MAX_VIEW_DISTANCE) {
                distance = currentDistance;
                if(player.getConnection().isOpen() && player.getHealth() > 0) {
                    closestPlayer = player;
                }
            }
        }
        return closestPlayer;
    }

    private void onHealthZero() {
        //remove self from world listeners
        super.getGameWorld().removeWorldEventListener(this);

        //spawn two VirusNpc instances
        final NewGameWorld gameWorld = super.getGameWorld();
        final VirusNpc virus1 = new VirusNpc(gameWorld);
        virus1.setXPosition(super.position.x);
        virus1.setYPosition(super.position.y);
        gameWorld.addGameEntity(virus1);

        //the game world itself will recognize the new NPCs on the next call to transmit network messages
        //since the default npc state is NEW. This way clients will be alerted to the new NPCs being spawned.
    }

    /**
     * Every 30 seconds, these spawn rules choose up to 10 players and surrounds each of them with
     * an ambush of multiple infected NPCs surrounding them.
     */
    public static class InfectedNpcSpawnRules extends SpawnRules {
        //how many npc instances to ambush players with at once
        private static final int NUM_AMBUSH_NPCS = 10;

        /**
         * The constructor of your spawn rules class may not have any parameters
         * The class will be completely ignored if no constructor with zero parameters
         * is found.
         */
        public InfectedNpcSpawnRules() {
            super(InfectedNpc.class);
            super.setSpawnInterval(90000); //90 second spawn interval
        }

        //called automatically when the population gets low enough
        @Override
        public void spawn(final NewGameWorld gameWorld) {
            final List<Player> players = gameWorld.getAllPlayers();
            if(players.isEmpty()) {
                super.setMaxPopulation(10);
                return;
            }
            final int numRandomPlayers = Math.min(10, players.size());
            super.setMaxPopulation(numRandomPlayers * 10 + InfectedNpcRandomSpawnerRules.MAX_POPULATION);
            for(int i = 0; i < numRandomPlayers; i++) {
                //pick a player at random
                final Player chosenPlayer = players.get((int)(Math.random() * players.size()));
                players.remove(chosenPlayer);

                //spawn 10 InfectedNpc entities around
                for(int j = 0; j < NUM_AMBUSH_NPCS; j++) {
                    final float angle = j * (float)Math.PI/5.0f;
                    final float x = 512 * (float)StrictMath.cos(angle) + chosenPlayer.getXPosition();
                    final float y = 512 * (float)StrictMath.sin(angle) + chosenPlayer.getYPosition();
                    final InfectedNpc npc = new InfectedNpc(gameWorld, chosenPlayer);
                    npc.setXPosition(x);
                    npc.setYPosition(y);
                    gameWorld.addGameEntity(npc);
                }
            }
        }
    }

    /**
     * Every 30 seconds these rules spawn more InfectedNpc instances randomly in the world.
     * Anywhere between 0 and 20 are spawned in each round.
     */
    public static class InfectedNpcRandomSpawnerRules extends SpawnRules {
        private static final int MAX_POPULATION = 200;

        public InfectedNpcRandomSpawnerRules() {
            super(InfectedNpc.class);
            super.setMaxPopulation(MAX_POPULATION);
            super.setSpawnInterval(30000);
        }

        @Override
        public void spawn(final NewGameWorld world) {
            final int numSpawns = (int)(Math.random() * 21);
            if(numSpawns == 0) {
                return;
            }

            final float width = world.getWorldWidth();
            final float height = world.getWorldHeight();

            for(int i = 0; i < numSpawns; i++) {
                final InfectedNpc npc = new InfectedNpc(world, null);
                npc.setXPosition((float)(Math.random() * width));
                npc.setYPosition((float)(Math.random() * height));
                world.addGameEntity(npc);
            }
        }
    }
}
