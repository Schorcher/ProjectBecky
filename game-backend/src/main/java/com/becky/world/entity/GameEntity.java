package com.becky.world.entity;

import com.becky.world.GameWorld;

import java.util.Collection;

public interface GameEntity {
    float getXPosition();
    float getYPosition();
    void setXPosition(final float xPosition);
    void setYPosition(final float yPosition);

    float getXVelocity();
    float getYVelocity();
    void setXVelocity(final float xVelocity);
    void setYVelocity(final float yVelocity);

    float getXAcceleration();
    float getYAcceleration();
    void setXAcceleration(final float xAcceleration);
    void setYAcceleration(final float yAcceleration);

    void setAngles(final float angles);
    float getAngles();

    void tick(final long elapsedTime);

    GameWorld getGameWorld();

    GameEntity getParent();

    Collection<GameEntity> getChildren();
}
