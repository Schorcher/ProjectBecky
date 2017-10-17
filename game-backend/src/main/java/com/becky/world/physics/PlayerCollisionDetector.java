package com.becky.world.physics;

import com.becky.util.MathUtils;
import com.becky.world.NewGameWorld;
import com.becky.world.WorldEventListener;
import com.becky.world.entity.GameEntity;
import com.becky.world.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerCollisionDetector implements PhysicsFilter, WorldEventListener {
    private final NewGameWorld gameWorld;
    private final List<Player> worldPlayer = new ArrayList<>();

    public PlayerCollisionDetector(final NewGameWorld gameWorld) {
        this.gameWorld = gameWorld;
        this.gameWorld.addWorldEventListener(this);
    }

    @Override
    public void apply(final GameEntity gameEntity) {
        //Check if any bullets are colliding with the player and handle that
        //the main game loop will handle transmitting status changes
        if(gameEntity instanceof Player) {
            final Player player = (Player)gameEntity;
            boolean colliding = false;
            for(int i = 0; i < worldPlayer.size(); i++) {
                final Player player1 = worldPlayer.get(i);
                if(player1.equals(player)) {
                    continue;
                }
                if(isPlayerColliding(player, player1)) {
                    colliding = true;

                    //get angles
                    final float angle = MathUtils.getAngleBetweenEntities(player, player1);
                    final float angle1 = MathUtils.getAngleBetweenEntities(player1, player);

                    //set velocity of players
                    player.setXVelocity(player.getXVelocity() + 300.0f * (float)StrictMath.cos(angle));
                    player.setYVelocity(player.getYVelocity() + 300.0f * (float)StrictMath.sin(angle));
                    player1.setXVelocity(player1.getXVelocity() + 300.0f * (float)StrictMath.cos(angle1));
                    player1.setYVelocity(player1.getYVelocity() + 300.0f * (float)StrictMath.sin(angle1));

                    //set player positions so they are just barely apart from each other
                    player.setXPosition(player1.getXPosition()
                            + (player1.getCollisionRadius() + player.getCollisionRadius())
                            * (float)StrictMath.cos(angle));
                    player.setYPosition(player1.getYPosition()
                            + (player1.getCollisionRadius() + player.getCollisionRadius())
                            * (float)StrictMath.sin(angle));
                }
            }

            if(colliding) {
                worldPlayer.remove(player);
            }
        }
    }

    @Override
    public void prepare() {}

    @Override
    public void onGameEntityAdded(final NewGameWorld world, final GameEntity entity) {
        if(entity instanceof Player) {
            worldPlayer.add((Player)entity);
        }
    }

    @Override
    public void onGameEntityRemoved(final NewGameWorld world, final GameEntity entity) {
        if(entity instanceof Player) {
            worldPlayer.remove(entity);
        }
    }

    private boolean isPlayerColliding(final Player player, final Player player2) {
        final float delta = MathUtils.distance(player.getXPosition(), player.getYPosition(), player2.getXPosition(), player2.getYPosition());
        final float collisionDistance = player.getCollisionRadius() + player2.getCollisionRadius();
        return delta <= collisionDistance;
    }
}
