package com.becky.networked.message;

/**
 * The class of data to be sent to clients
 */
public class BulletInfo {
    private final String owner;
    private final int state;
    private final long bulletId;
    private final Float velocityX;
    private final Float velocityY;
    private final Float positionX;
    private final Float positionY;

    public BulletInfo(final String owner, final int state, final long bulletId,
                      final Float velocityX, final Float velocityY,
                      final Float positionX, final Float positionY) {
        this.owner = owner;
        this.state = state;
        this.bulletId = bulletId;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.positionX = positionX;
        this.positionY = positionY;
    }

    public String getOwner() {
        return this.owner;
    }

    public int getState() {
        return this.state;
    }

    public Float getVelocityX() {
        return this.velocityX;
    }

    public Float getVelocityY() {
        return this.velocityY;
    }

    public Float getPositionX() {
        return this.positionX;
    }

    public Float getPositionY() {
        return this.positionY;
    }

    public long getBulletId() {
        return this.bulletId;
    }
}
