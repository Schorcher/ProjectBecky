package com.becky;

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
}
