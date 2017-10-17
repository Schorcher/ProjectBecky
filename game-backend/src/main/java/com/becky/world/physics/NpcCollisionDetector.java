package com.becky.world.physics;

import com.becky.util.MathUtils;
import com.becky.world.NewGameWorld;
import com.becky.world.WorldEventListener;
import com.becky.world.entity.GameEntity;
import com.becky.world.entity.Player;
import com.becky.world.entity.npc.Npc;

import java.util.ArrayList;
import java.util.List;

public class NpcCollisionDetector implements PhysicsFilter, WorldEventListener {
    private final List<Npc> npcs = new ArrayList<>();
    private final List<Npc> processedNpcs = new ArrayList<>();
    private final List<Player> players = new ArrayList<>();

    public NpcCollisionDetector(final NewGameWorld gameWorld) {
        gameWorld.addWorldEventListener(this);
    }

    @Override
    public void apply(final GameEntity gameEntity) {
        if(!(gameEntity instanceof Npc)) {
            return;
        }
        final Npc npc = (Npc)gameEntity;
        applyNpcCollisionWithNpc(npc);
        applyNpcCollisionWithPlayer(npc);
    }

    private void applyNpcCollisionWithNpc(final Npc npc) {
        for(final Npc entity: npcs) {
            if(processedNpcs.contains(npc)) {
                continue;
            }
            if(MathUtils.distance(npc, entity) > npc.getCollisionRadius() + entity.getCollisionRadius()) {
                continue;
            }
            processedNpcs.add(npc);
            final float collisionAngle = MathUtils.getAngleBetweenEntities(npc, entity);
            final float collisionAngle1 = MathUtils.getAngleBetweenEntities(entity, npc);
            npc.setXVelocity(npc.getXVelocity() + 20.0f * (float)StrictMath.cos(collisionAngle));
            npc.setYVelocity(npc.getYVelocity() + 20.0f * (float)StrictMath.sin(collisionAngle));
            entity.setXVelocity(entity.getXVelocity() + 20.0f * (float)StrictMath.cos(collisionAngle1));
            entity.setYVelocity(entity.getYVelocity() + 20.0f * (float)StrictMath.sin(collisionAngle1));
        }
    }

    private void applyNpcCollisionWithPlayer(final Npc npc) {
        for(final Player player: players) {
            if(MathUtils.distance(npc, player) > npc.getCollisionRadius() + player.getCollisionRadius()) {
                continue;
            }
            final float collisionAngle = MathUtils.getAngleBetweenEntities(player, npc);
            player.setXVelocity(player.getXVelocity() + 300.0f * (float)StrictMath.cos(collisionAngle));
            player.setYVelocity(player.getYVelocity() + 300.0f * (float)StrictMath.sin(collisionAngle));
            player.setHealth(player.getHealth() - npc.getNpcHealth(), npc.getClass().getSimpleName());
            npc.setNpcHealth(0);
            npc.setNpcState(Npc.NPC_STATE_DEAD);
        }
    }

    @Override
    public void prepare() {
        processedNpcs.clear();
    }

    @Override
    public void onGameEntityRemoved(final NewGameWorld world, final GameEntity entity) {
        if(entity instanceof Npc) {
            this.npcs.remove(entity);
        }
        else if(entity instanceof Player) {
            this.players.remove(entity);
        }
    }

    @Override
    public void onGameEntityAdded(final NewGameWorld world, final GameEntity entity) {
        if(entity instanceof Npc) {
            this.npcs.add((Npc)entity);
        }
        else if(entity instanceof Player) {
            this.players.add((Player)entity);
        }
    }
}
