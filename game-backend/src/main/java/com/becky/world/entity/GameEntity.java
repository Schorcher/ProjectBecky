package com.becky.world.entity;

import com.becky.world.GameWorld;

import java.util.Collection;

public abstract class GameEntity {
    private static long entityCount = 1;
    private final long entityId;

    public GameEntity() {
        entityId = entityCount;
        entityCount++;
    }

    public long getEntityId() {
        return this.entityId;
    }

    public abstract float getXPosition();
    public abstract float getYPosition();
    public abstract void setXPosition(final float xPosition);
    public abstract void setYPosition(final float yPosition);

    public abstract float getXVelocity();
    public abstract float getYVelocity();
    public abstract void setXVelocity(final float xVelocity);
    public abstract void setYVelocity(final float yVelocity);

    public abstract float getXAcceleration();
    public abstract float getYAcceleration();
    public abstract void setXAcceleration(final float xAcceleration);
    public abstract void setYAcceleration(final float yAcceleration);

    public abstract void setAngles(final float angles);
    public abstract float getAngles();

    public abstract void tick(final long elapsedTime);

    public abstract GameWorld getGameWorld();

    public abstract GameEntity getParent();

    public abstract Collection<GameEntity> getChildren();
}
