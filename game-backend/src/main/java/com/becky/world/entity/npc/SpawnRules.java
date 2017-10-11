package com.becky.world.entity.npc;

import com.becky.world.NewGameWorld;
import com.becky.world.WorldEventListener;
import com.becky.world.entity.GameEntity;

/**
 * Represents NPC Spawn rules to be followed by an NPC Spawner
 * Created by Clayton on 10/11/2017.
 */
public abstract class SpawnRules implements WorldEventListener {
    public static final int INFINITE_POPULATION = Integer.MAX_VALUE;
    public static final int NO_INTERVAL = -1;

    private int maxPopulation;
    private int currentPopulation;
    private long spawnInterval;
    private long nextIntervalTime;
    private final Class<? extends GameEntity> npcClassType;

    protected SpawnRules(final Class<? extends GameEntity> npcClassType) {
        maxPopulation = INFINITE_POPULATION;
        spawnInterval = NO_INTERVAL;
        nextIntervalTime = NO_INTERVAL;
        this.npcClassType = npcClassType;
    }

    /**
     * Gets the maximum number of NPCs that are allowed to exist at any given moment.
     * Even if the time interval is up, spawning will not commence if there are this many or more NPCs of the type
     * remaining on the game world.
     * @return
     */
    public int getMaxPopulation() {
        return maxPopulation;
    }

    /**
     * Gets the current population of NPCs that this spawn rules instance represents.
     * @return
     */
    public int getCurrentPopulation() {
        return this.currentPopulation;
    }

    /**
     * Gets the minimum amount of time that must pass since the last spawn sequence in order for NPCs of the type to
     * be able to spawn again.
     * @return
     */
    public long getSpawnInterval() {
        return this.spawnInterval;
    }

    /**
     * Sets the maximum NPC population. NPCs will not spawn if there are this many NPCs or more in the game world.
     * @param maxPopulation
     */
    public void setMaxPopulation(final int maxPopulation) {
        this.maxPopulation = maxPopulation;
    }

    /**
     * Sets the number of milliseconds per spawn cycle. After NPCs are spawned,
     * this is the minimum amount of time that must pass before NPCs can be spawned.
     * @param spawnInterval
     */
    public void setSpawnInterval(final long spawnInterval) {
        this.spawnInterval = spawnInterval;
    }

    /**
     * Gets the next timestamp (in the form of milliseconds since epoch) in which this NPC type will be ready to spawn
     * again. This is how the NPC spawner knows if it's ok to spawn these NPCs again.
     * @return
     */
    public long getNextIntervalTime() {
        return this.nextIntervalTime;
    }

    /**
     * Updates the timestamp (in the form of milliseconds since epoch) which indicates the next time this NPC type
     * can spawn again.
     */
    public void resetNextIntervalTime() {
        //if spawnInterval == -1 (NO_INTERVAL) then it will subtract 1 from this.nextIntervalTime
        //This is nothing to worry about because it would take centuries for this to cause an overflow for the
        //long int data type.
        this.nextIntervalTime += this.spawnInterval;
    }

    @Override
    public void onGameEntityAdded(final NewGameWorld gameWorld, final GameEntity entity) {
        if(this.npcClassType.isInstance(entity)) {
            this.currentPopulation++;
        }
    }

    @Override
    public void onGameEntityRemoved(final NewGameWorld gameWorld, final GameEntity entity) {
        if(this.npcClassType.isInstance(entity)) {
            this.currentPopulation--;
        }
    }

    /**
     * Spawns NPCs. This method must be implemented and is called by the NPC spawner utility.
     * This method calls when the npc population is below the max population (if there is a max population)
     * and System.currentTimeMillis() > this.nextIntervalTime.
     *
     * Any spawning logic is valid here. Any whatsoever.
     *
     * @param gameWorld The game world that the NPCs will be spawning in to.
     */
    public abstract void spawn(final NewGameWorld gameWorld);
}
