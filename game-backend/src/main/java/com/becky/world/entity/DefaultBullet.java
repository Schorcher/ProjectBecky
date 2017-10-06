package com.becky.world.entity;


import com.becky.world.GameWorld;

import java.util.Collection;
import java.util.Collections;

public class DefaultBullet extends Bullet {
    private static final float TRAVEL_DISTANCE = 2000.0f;
    private static final int DAMAGE_FACTOR = 1;

    private float remainingHealth = TRAVEL_DISTANCE;
    private final GameWorld gameWorld;

    public DefaultBullet(final Player owner,
                         final float xPosition,
                         final float yPosition,
                         final float xVelocity,
                         final float yVelocity) {
        super(owner, xPosition, yPosition, xVelocity, yVelocity, DAMAGE_FACTOR);
        gameWorld = owner.getGameWorld();
    }

    @Override
    public float getRemainingHealth() {
        return this.remainingHealth;
    }

    @Override
    public void setAngles(final float angles) {}

    @Override
    public float getAngles() {
        return 0.0f;
    }

    @Override
    public Collection<GameEntity> getChildren() {
        return Collections.emptyList();
    }

    @Override
    public GameEntity getParent() {
        return null;
    }

    @Override
    public GameWorld getGameWorld() {
        return this.gameWorld;
    }

    @Override
    public void tick(final long elapsedTime) {
        final float multiplier = elapsedTime / 1000.0f;
        final float deltaX = this.velocity.x * multiplier;
        final float deltaY = this.velocity.y * multiplier;
        this.position.x += deltaX;
        this.position.y += deltaY;
        this.remainingHealth -= (deltaX + deltaY);

        if(this.remainingHealth <= 0.0f) {
            this.state = Bullet.STATE_DEAD_BULLET;
        }

        super.tick(elapsedTime);
    }
}
