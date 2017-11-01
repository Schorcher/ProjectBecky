package com.becky.world.entity.npc;

import com.becky.world.NewGameWorld;

/**
 * Npc class which is a type of npc that spawns from the death of an infected npc.
 * Created by Clayton Hunsinger on 10/26/2017.
 */
public class VirusNpc extends Npc {
    private boolean positiveX;
    private boolean positiveY;

    protected VirusNpc(final NewGameWorld gameWorld) {
        super(gameWorld);
        super.collisionRadius = 16;
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
        if(Math.abs(super.velocity.x) < 5.0f || Math.abs(super.velocity.y) < 5.0f) {
            super.velocity.x = (float)(Math.random() * 200.0f);
            super.velocity.y = (float)(Math.random() * 200.0f);
            if(super.velocity.x >= 0.0f) {
                super.acceleration.x = -100.0f;
                super.velocity.x += 50.0f;
            }
            else {
                super.acceleration.x = 100.0f;
                super.velocity.x -= 50.0f;
            }
            if(super.velocity.y >= 0.0f) {
                super.acceleration.y = -100.0f;
                super.velocity.y += 50.0f;
            }
            else {
                super.acceleration.y = 100.0f;
                super.velocity.y -= 50.0f;
            }
            super.angles = (float)StrictMath.atan2(super.velocity.y, super.velocity.x);
        }

        super.tick(elapsedTime);
    }
}
