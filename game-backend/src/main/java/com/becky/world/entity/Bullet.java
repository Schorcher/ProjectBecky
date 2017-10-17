package com.becky.world.entity;

import com.becky.world.physics.WorldBorderCollisionDetector;

import java.awt.geom.Point2D;

/**
 * Defines a type of bullet shot by a player
 */
public abstract class Bullet extends GameEntity {
    public static final int STATE_NEW_BULLET = 0;
    public static final int STATE_UPDATED_BULLET = 1;
    public static final int STATE_DEAD_BULLET = 2;

    protected final Player owner;
    protected final int damageAmount;
    protected int state = Bullet.STATE_NEW_BULLET;

    protected Bullet(final Player owner,
                     final float xPosition,
                     final float yPosition,
                     final float xVelocity,
                     final float yVelocity,
                     final int damageAmount) {
        super(owner.getGameWorld());
        super.addPhysicsFilter(WorldBorderCollisionDetector.class);
        this.owner = owner;
        position.x = xPosition;
        position.y = yPosition;
        velocity.x = xVelocity;
        velocity.y = yVelocity;
        this.damageAmount = damageAmount;
        super.collisionRadius = 24;
    }

    /**
     * Gets the player that fired this bullet.
     * @return
     */
    public Player getOwner() {
        return this.owner;
    }

    /**
     * Get the amount of damage done by this bullet.
     * @return
     */
    public int getDamage() {
        return this.damageAmount;
    }

    /**
     * Gets whether or not this bullet is newly spawned
     * @return
     */
    public int getState() {
        if(this.state == Bullet.STATE_NEW_BULLET) {
            this.state = Bullet.STATE_UPDATED_BULLET;
            return Bullet.STATE_NEW_BULLET;
        }
        return this.state;
    }

    public void setState(final int state) {
        this.state = state;
    }

    /**
     * Gets the remaining health of this bullet.
     * @return
     */
    public abstract float getRemainingHealth();

    @Override
    public void setXVelocity(final float xVelocity) {
        velocity.x = xVelocity;
        if(Math.abs(xVelocity) < 0.1f) {
            this.state = STATE_DEAD_BULLET;
        }
    }

    @Override
    public void setYVelocity(final float yVelocity) {
        velocity.y = yVelocity;
        if(Math.abs(yVelocity) < 0.1f) {
            this.state = STATE_DEAD_BULLET;
        }
    }

    @Override
    public float getXAcceleration() {
        return 0;
    }

    @Override
    public float getYAcceleration() {
        return 0;
    }

    @Override
    public void setXAcceleration(final float xAcceleration) { }

    @Override
    public void setYAcceleration(final float yAcceleration) { }

    public int getCollisionRadius() {
        return this.collisionRadius;
    }

    public void setCollisionRadius(final int radius) {
        this.collisionRadius = radius;
    }

    @Override
    public void tick(final long elapsedTime) {
        if(Math.abs(this.velocity.x) < 0.1f && Math.abs(this.velocity.y) < 0.1f) {
            this.state = STATE_DEAD_BULLET;
        }
    }
}
