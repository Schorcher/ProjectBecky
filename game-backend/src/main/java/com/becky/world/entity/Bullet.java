package com.becky.world.entity;

import java.awt.geom.Point2D;

/**
 * Defines a type of bullet shot by a player
 */
public abstract class Bullet implements GameEntity {
    public static final int STATE_NEW_BULLET = 0;
    public static final int STATE_UPDATED_BULLET = 1;
    public static final int STATE_DEAD_BULLET = 2;

    private static long BULLET_ID_COUNT = 0;
    private final long bulletId;

    protected final Point2D.Float position = new Point2D.Float(0.0f, 0.0f);
    protected final Point2D.Float velocity = new Point2D.Float(0.0f, 0.0f);
    protected final Player owner;
    protected final int damageAmount;
    protected int state = Bullet.STATE_NEW_BULLET;
    protected int collisionRadius = 24;

    protected Bullet(final Player owner,
                     final float xPosition,
                     final float yPosition,
                     final float xVelocity,
                     final float yVelocity,
                     final int damageAmount) {
        this.bulletId = BULLET_ID_COUNT;
        BULLET_ID_COUNT++;
        this.owner = owner;
        position.x = xPosition;
        position.y = yPosition;
        velocity.x = xVelocity;
        velocity.y = yVelocity;
        this.damageAmount = damageAmount;
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

    /**
     * Gets the unique id of this bullet.
     * @return
     */
    public long getBulletId() {
        return this.bulletId;
    }

    @Override
    public float getXPosition() {
        return position.x;
    }

    @Override
    public float getYPosition() {
        return position.y;
    }

    @Override
    public void setXPosition(final float xPosition) {
        position.x = xPosition;
    }

    @Override
    public void setYPosition(final float yPosition) {
        position.y = yPosition;
    }

    @Override
    public float getXVelocity() {
        return velocity.x;
    }

    @Override
    public float getYVelocity() {
        return velocity.y;
    }

    @Override
    public void setXVelocity(final float xVelocity) {
        velocity.x = xVelocity;
        if(xVelocity < 0.1f) {
            this.state = STATE_DEAD_BULLET;
        }
    }

    @Override
    public void setYVelocity(final float yVelocity) {
        velocity.y = yVelocity;
        if(yVelocity < 0.1f) {
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
    public void setXAcceleration(final float xAcceleration) {
    }

    @Override
    public void setYAcceleration(final float yAcceleration) {
    }

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
