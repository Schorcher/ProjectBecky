package com.becky.world.entity.npc;

import com.becky.world.NewGameWorld;
import com.becky.world.entity.GameEntity;
import com.becky.world.physics.WorldBorderCollisionDetector;

public abstract class Npc extends GameEntity {
    public static final int NPC_STATE_NEW = 111;
    public static final int NPC_STATE_UPDATE = 12;
    public static final int NPC_STATE_DEAD = 13;

    private int npcState;
    private int npcHealth;

    protected Npc(final NewGameWorld gameWorld) {
        super(gameWorld);
        super.addPhysicsFilter(WorldBorderCollisionDetector.class);
    }

    public int getNpcState() {
        if(npcState == NPC_STATE_NEW) {
            npcState = NPC_STATE_UPDATE;
            return NPC_STATE_NEW;
        }

        return npcState;
    }

    public void setNpcState(final int npcState) {
        this.npcState = npcState;
    }

    public int getNpcHealth() {
        return this.npcHealth;
    }

    public void setNpcHealth(final int health) {
        this.npcHealth = health;
        if(this.npcHealth < 0) {
            this.npcHealth = 0;
            this.npcState = NPC_STATE_DEAD;
        }
    }

    @Override
    public void tick(final long elapsedTime) {
        final float multiplier = elapsedTime / 1000.0f;
        this.velocity.x += this.acceleration.x * multiplier;
        this.velocity.y += this.acceleration.y * multiplier;
        this.position.x += this.velocity.x * multiplier;
        this.position.y += this.velocity.y * multiplier;
    }

    /**
     * This abstract method allows multiple NPCs to exist using the default NPCPlayerCollisionDetector.
     * The default collision detector will call this method.
     *
     * If you create a new NPC you may implement this method as a blank method and write your own
     * NPC Physics filter class to handle special collision logic. Or you can just let the default
     * npc physics filter handle this part and perform any collision logic here.
     * @param entity
     */
    public abstract void onCollisionWith(final GameEntity entity);
}
