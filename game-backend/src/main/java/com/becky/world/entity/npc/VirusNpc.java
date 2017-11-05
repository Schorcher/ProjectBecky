package com.becky.world.entity.npc;

import com.becky.world.NewGameWorld;

/**
 * Npc class which is a type of npc that spawns from the death of an infected npc.
 * Created by Clayton Hunsinger on 10/26/2017.
 */
public class VirusNpc extends Npc {
    private boolean positiveX;
    private boolean positiveY;
    private boolean xStopped;
    private boolean yStopped;
    private boolean readyForNextDirection = true;
    private long nextDirection = 0L;

    protected VirusNpc(final NewGameWorld gameWorld) {
        super(gameWorld);
        super.collisionRadius = 18;
        super.maxVelocity = 250.0f;
        super.npcHealth = 5;
        super.pointsValue = 2;
        super.acceleration.x = 0.0f;
        super.acceleration.y = 0.0f;
        super.velocity.x = 0.0f;
        super.velocity.y = 0.0f;
    }

    @Override
    public void tick(final long elapsedTime) {
        if(readyForNextDirection) {
            if(nextDirection <= 0) {
                super.velocity.x = ((float) Math.random() * 400.0f) - 200.0f;
                super.velocity.y = ((float) Math.random() * 400.0f) - 200.0f;
                positiveX = super.velocity.x >= 0.0f;
                positiveY = super.velocity.y >= 0.0f;

                if (super.velocity.x > 0.0f) {
                    super.acceleration.x = -100.0f;
                    super.velocity.x += 50.0f;
                } else {
                    super.acceleration.x = 100.0f;
                    super.velocity.x -= 50.0f;
                }
                if (super.velocity.y > 0.0f) {
                    super.acceleration.y = -100.0f;
                    super.velocity.y += 50.0f;
                } else {
                    super.acceleration.y = 100.0f;
                    super.velocity.y -= 50.0f;
                }
                super.angles = (float) StrictMath.atan2(super.velocity.y, super.velocity.x);
                xStopped = false;
                yStopped = false;
                readyForNextDirection = false;
                nextDirection = 0L;
            }
            else {
                nextDirection -= elapsedTime;
                return;
            }
        }

        final float multiplier = elapsedTime / 1000.0f;
        super.velocity.x += super.acceleration.x * multiplier;
        super.velocity.y += super.acceleration.y * multiplier;

        if(xStopped || positiveX != super.velocity.x >= 0.0f) {
            super.velocity.x = 0.0f;
            xStopped = true;
        }
        if(yStopped || positiveY != super.velocity.y >= 0.0f) {
            super.velocity.y = 0.0f;
            yStopped = true;
        }
        if(xStopped && yStopped) {
            readyForNextDirection = true;
            nextDirection = 500L;
        }

        super.position.x += super.velocity.x * multiplier;
        super.position.y += super.velocity.y * multiplier;
    }

    @Override
    public void setXAcceleration(final float acceleration) {}

    @Override
    public void setYAcceleration(final float acceleration) {}

    @Override
    public void setXVelocity(final float velocity) {}

    @Override
    public void setYVelocity(final float velocity) {}
}
