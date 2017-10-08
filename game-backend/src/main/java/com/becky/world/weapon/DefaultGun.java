package com.becky.world.weapon;

import com.becky.world.entity.Bullet;
import com.becky.world.entity.DefaultBullet;
import com.becky.world.entity.Player;

public class DefaultGun implements Gun {
    private static final byte RATE_OF_FIRE = 10; //per second
    private static final float VELOCITY = 500.0f;

    private static final short TIME_PER_FIRE = 1000/RATE_OF_FIRE; //don't modify
    private final Player weilder;
    private long lastFireTime = System.currentTimeMillis();

    public DefaultGun(final Player weilder) {
        this.weilder = weilder;
    }

    @Override
    public Player getWeilder() {
        return this.weilder;
    }

    @Override
    public void fire() {
        final long currentTime = System.currentTimeMillis();
        if((currentTime - lastFireTime) < TIME_PER_FIRE) {
            return; //can only fire after specific amount of time
        }

        //determine location and velocity of bullet
        final float angles = weilder.getAngles();
        final float sin = (float)StrictMath.sin(angles);
        final float cos = (float)StrictMath.cos(angles);
        final float vX = cos * VELOCITY;
        final float vY = sin * VELOCITY;
        final float pX = cos * 32 + weilder.getXPosition();
        final float pY = sin * 32 + weilder.getYPosition();
        final Bullet bullet = new DefaultBullet(weilder, pX, pY, vX, vY);
        weilder.addBullet(bullet);
        lastFireTime = currentTime;
    }
}
