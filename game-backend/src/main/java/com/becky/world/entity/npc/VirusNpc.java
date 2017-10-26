package com.becky.world.entity.npc;

import com.becky.util.MathUtils;
import com.becky.world.NewGameWorld;
import com.becky.world.entity.Player;

/**
 * Npc class which is a type of npc that spawns from the death of an infected npc.
 * Created by Clayton Hunsinger on 10/26/2017.
 */
public class VirusNpc extends Npc {
    private static final float TURNING_SPEED = (float)(Math.PI * 2.0); //max turning speed
    private static final long WAIT_TIME_BETWEEN_STATES = 500L; //time to wait between move and turn states

    private static final float TURNING_SPEED_PER_MILLISECOND = TURNING_SPEED / 1000.0f; //DO NOT MODIFY
    private enum State { WAITING, MOVING, TURNING }

    //state info
    private State currentState = State.WAITING;
    private State nextState = State.MOVING;
    private float remainingTurn = 0.0f; //the remaining radians to turn before choosing a new state
    private float turnDirection = 1.0f; //always equal to 1.0f or -1.0f
    private float remainingMoveDistance = 0.0f; //when moving, the remaining distance to mov
    private long timeToNextState = 0L; //Number of milliseconds before the next state

    protected VirusNpc(final NewGameWorld gameWorld) {
        super(gameWorld);
        super.collisionRadius = 16;
        super.maxVelocity = Player.MAX_VELOCITY / 1.5f;
        super.npcHealth = 6;
        super.pointsValue = 5;
    }

    @Override
    public void tick(final long elapsedTime) {
        //The NPC can be in one of three states:
        // WAITING: timeToNextState > 0 and NPC is doing nothing until timeToNextState <= 0
        // MOVING: remainingMoveDistance > 0.0f AND no collision with the world border
        // TURNING: abs(remainingTurn) > 0.0f

        final float multiplier = elapsedTime / 1000.0f;
        if(this.currentState == State.WAITING) {
            timeToNextState -= elapsedTime;
            if(timeToNextState <= 0) {
                currentState = nextState;
                nextState = State.WAITING;
                timeToNextState = WAIT_TIME_BETWEEN_STATES; //so we wait long enough on the next wait state iteration
            }
        }
        else if(this.currentState == State.MOVING) {
            //still moving
            if(remainingMoveDistance > 0.0) {
                final float currentX = super.position.x;
                final float currentY = super.position.y;
                super.position.x += multiplier * super.velocity.x;
                super.position.y += multiplier * super.velocity.y;
                final float travelDistance = MathUtils.distance(currentX, currentY, super.position.x, super.position.y);
                remainingMoveDistance -= travelDistance;
            }
            else {
                //still moving, but we need to decelerate now
                super.tick(elapsedTime);
                if(Math.abs(super.velocity.x) < 1.0f && Math.abs(super.velocity.y) < 1.0f) {
                    super.velocity.x = 0.0f;
                    super.velocity.y = 0.0f;
                    currentState = nextState;
                    remainingMoveDistance = 0.0f; //reset remaining move distance
                    final int random = Math.round((float)Math.random() * 5);
                    if(random == 0) {
                        nextState = State.MOVING;
                        prepareMoveState();
                    }
                    else {
                        nextState = State.TURNING;
                        prepareTurnState();
                    }
                }
            }
        }
        else if(this.currentState == State.TURNING) {
            final float incrementAngle = turnDirection * TURNING_SPEED_PER_MILLISECOND * elapsedTime;
            remainingTurn -= incrementAngle;
            super.angles += incrementAngle;

            if(remainingTurn <= 0.0f) {
                remainingTurn = 0.0f; //reset remaining turn
                currentState = nextState;
                final int random = Math.round((float)Math.random() * 5);
                if(random == 0) {
                    nextState = State.TURNING;
                    prepareTurnState();
                }
                else {
                    nextState = State.MOVING;
                    prepareMoveState();
                }
            }
        }
    }

    private void prepareTurnState() {
        final float newAngle = (float)(Math.random() * 2 * Math.PI);
        remainingTurn = super.angles - newAngle;
        turnDirection = remainingTurn < 0.0f ? -1.0f : 1.0f;
    }

    private void prepareMoveState() {
        remainingMoveDistance = (float)(Math.random() * 150) + 100.0f;
        super.velocity.x = (float)StrictMath.cos(super.angles) * super.maxVelocity;
        super.velocity.y = (float)StrictMath.sin(super.angles) * super.maxVelocity;
    }
}
