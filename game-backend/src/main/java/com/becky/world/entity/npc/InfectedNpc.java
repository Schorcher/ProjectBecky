package com.becky.world.entity.npc;

import com.becky.util.MathUtils;
import com.becky.world.NewGameWorld;
import com.becky.world.entity.Player;

import java.util.Collection;
import java.util.List;

public class InfectedNpc extends Npc {
    private static final float MAX_VIEW_DISTANCE = 2048.0f;
    private static final float ACCELERATION = Player.ACCELERATION;

    private Player trackedPlayer;

    public InfectedNpc(final NewGameWorld world, final Player trackedPlayer) {
        super(world);
        this.trackedPlayer = trackedPlayer;
        super.maxVelocity = Player.MAX_VELOCITY / 3.0f;
        super.collisionRadius = 12;
        super.npcHealth = 15;
        super.pointsValue = 25;
    }

    @Override
    public void tick(final long elapsedTime) {
        if(trackedPlayer == null) {
            trackedPlayer = findClosestPlayer();
        }
        else {
            final float deltaX = this.trackedPlayer.getXPosition() - super.position.x;
            final float deltaY = this.trackedPlayer.getYPosition() - super.position.y;
            final float angle = (float)StrictMath.atan2(deltaY, deltaX);

            super.acceleration.x = ACCELERATION * (float)StrictMath.cos(angle);
            super.acceleration.y = ACCELERATION * (float)StrictMath.sin(angle);
        }

        super.tick(elapsedTime);
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

    public static class InfectedNpcSpawnRules extends SpawnRules {
        /**
         * The constructor of your spawn rules class may not have any parameters
         * The class will be completely ignored if no constructor with zero parameters
         * is found.
         */
        public InfectedNpcSpawnRules() {
            super(InfectedNpc.class);
            super.setSpawnInterval(30000); //30 second spawn interval
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
            super.setMaxPopulation(numRandomPlayers * 10);
            for(int i = 0; i < numRandomPlayers; i++) {
                //pick a player at random
                final Player chosenPlayer = players.get((int)(Math.random() * players.size()));
                players.remove(chosenPlayer);

                //spawn 10 InfectedNpc entities around
                for(int j = 0; j < 10; j++) {
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
}
