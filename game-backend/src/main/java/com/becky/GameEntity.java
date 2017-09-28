package com.becky;

public interface GameEntity {
    int getX_position();
    int getY_position();
    void setX_position(final int xPosition);
    void setY_position(final int yPosition);

    float getX_velocity();
    float getY_velocity();
    void setX_velocity(final float xVelocity);
    void setY_velocity(final float yVelocity);

    float getX_acceleration();
    float getY_acceleration();
    void setX_acceleration(final float xAcceleration);
    void setY_acceleration(final float yAcceleration);

    void setDecelerating(final boolean decelerating);
    boolean isDecelerating();
}
