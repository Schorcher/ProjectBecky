package com.becky.world.physics;

import com.becky.networking.message.PlayerHealthMessage;
import com.becky.world.NewGameWorld;
import com.becky.world.entity.Bullet;
import com.becky.world.entity.GameEntity;
import com.becky.world.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Determines if bullets are colliding with players
 * Created by Clayton on 10/3/2017.
 */
public class BulletCollisionDetector implements PhysicsFilter {
    private final NewGameWorld gameWorld;
    private final List<Bullet> worldBullets = new ArrayList<>();

    public BulletCollisionDetector(final NewGameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }

    @Override
    public void apply(final GameEntity gameEntity) {
        if(!gameEntity.doesPhysicsApply(BulletCollisionDetector.class)) {
            return;
        }

        //Check if any bullets are colliding with the player and handle that
        //the main game loop will handle transmitting status changes
        if(gameEntity instanceof Player) {
            final Player player = (Player)gameEntity;
            for(int i = 0; i < worldBullets.size(); i++) {
                final Bullet bullet = worldBullets.get(i);
                if(!player.equals(bullet.getOwner()) && isBulletColliding(player, bullet)) {
                    player.setHealth(player.getHealth() - bullet.getDamage(), bullet.getOwner().getPlayerUsername());
                    bullet.setState(Bullet.STATE_DEAD_BULLET);
                    worldBullets.remove(bullet);
                    i--;
                }
            }
        }
    }

    @Override
    public void prepare() {
        worldBullets.clear();
        final List<GameEntity> entities = gameWorld.getAllGameEntities();
        for(final GameEntity entity: entities) {
            if(entity instanceof Bullet) {
                worldBullets.add((Bullet)entity);
            }
        }
    }

    private boolean isBulletColliding(final Player player, final Bullet bullet) {
        final float delta = distance(player.getXPosition(), player.getYPosition(), bullet.getXPosition(), bullet.getYPosition());
        final float collisionDistance = player.getCollisionRadius() + bullet.getCollisionRadius();
        return delta <= collisionDistance;
    }

    private float distance(final float x1, final float y1, final float x2, final float y2) {
        return (float)StrictMath.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));
    }
}
