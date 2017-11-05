package com.becky.world.entity.npc;

import com.becky.util.MathUtils;
import com.becky.world.NewGameWorld;

import java.awt.geom.Point2D;

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
    private boolean readyForNextTurn = false;
    private boolean makingTurn = false;
    private final Point2D.Float nextVelocity = new Point2D.Float();
    private float nextAngle = 0.0f;
    private float turnDirection = 1.0f;
    private long timeTilNextMove = 0L;

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
            if(timeTilNextMove <= 0) {
                super.velocity.x = nextVelocity.x;
                super.velocity.y = nextVelocity.y;
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

                xStopped = false;
                yStopped = false;
                readyForNextDirection = false;
                timeTilNextMove = 0L;
                return;
            }
            else {
                timeTilNextMove -= elapsedTime;
                return;
            }
        }

        if(readyForNextTurn) {
            //determine the next velocity and next angle
            nextVelocity.x = ((float) Math.random() * 400.0f) - 200.0f;
            nextVelocity.y = ((float) Math.random() * 400.0f) - 200.0f;
            nextAngle = (float) StrictMath.atan2(nextVelocity.y, nextVelocity.x) + (float)Math.PI/2.0f;
            nextAngle = MathUtils.normalizeAngle(nextAngle);

            //determine which direction to turn in and remaining turn angle
            final float angleDifference = MathUtils.normalizeAngle(nextAngle - super.angles);
            if(angleDifference >= Math.PI) {
                turnDirection = 1.0f;
            }
            else {
                turnDirection = -1.0f;
            }

            //change the states
            makingTurn = true;
            readyForNextTurn = false;
            return;
        }

        if(makingTurn) {
            final float angleChange = (float)Math.PI * (elapsedTime/1000.0f) * turnDirection;
            super.angles = MathUtils.normalizeAngle(super.angles);
            if(Math.abs(super.angles - nextAngle) <= Math.abs(angleChange)) {
                super.angles = nextAngle;
                makingTurn = false;
                readyForNextDirection = true;
            }
            else {
                super.angles -= angleChange;
            }
            return;
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
            readyForNextTurn = true;
            timeTilNextMove = 50L;
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
