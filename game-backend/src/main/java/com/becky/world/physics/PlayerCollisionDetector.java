package com.becky.world.physics;

import com.becky.world.NewGameWorld;
import com.becky.world.entity.GameEntity;
import com.becky.world.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerCollisionDetector implements PhysicsFilter {
    private final NewGameWorld gameWorld;
    private final List<Player> worldPlayer = new ArrayList<>();

    public PlayerCollisionDetector(final NewGameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }

    @Override
    public void apply(final GameEntity gameEntity) {
        if(!gameEntity.doesPhysicsApply(PlayerCollisionDetector.class)) {
            return;
        }

        //Check if any bullets are colliding with the player and handle that
        //the main game loop will handle transmitting status changes
        if(gameEntity instanceof Player) {
            final Player player = (Player)gameEntity;
            for(int i = 0; i < worldPlayer.size(); i++) {
                final Player player1 = worldPlayer.get(i);
                if(isPlayerColliding(player, player1)) {
                    player.setXVelocity(0-player.getXVelocity()*2);
                    player.setYVelocity(0-player.getYVelocity()*2);
                    player.setHealth(player.getHealth()-1, "player Collision");
                    player1.setXVelocity(0-player1.getXVelocity()*2);
                    player1.setYVelocity(0-player1.getYVelocity()*2);
                    player1.setHealth(player1.getHealth()-1, "player Collision");
                }
            }
        }
    }

    @Override
    public void prepare() {
        worldPlayer.clear();
        final List<GameEntity> entities = gameWorld.getAllGameEntities();
        for(final GameEntity entity: entities) {
            if(entity instanceof Player) {
                worldPlayer.add((Player)entity);
            }
        }
    }

    private boolean isPlayerColliding(final Player player, final Player player2) {
        final float delta = distance(player.getXPosition(), player.getYPosition(), player2.getXPosition(), player2.getYPosition());
        final float collisionDistance = player.getCollisionRadius() + player2.getCollisionRadius();
        return delta <= collisionDistance;
    }

    private float distance(final float x1, final float y1, final float x2, final float y2) {
        return (float)StrictMath.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));
    }
}
