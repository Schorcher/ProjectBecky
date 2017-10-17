package com.becky.world.entity.npc;

import com.becky.world.NewGameWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The NPC spawner is responsible for literally spawning NPCs. It follows the rules provided to it.
 * Created by Clayton on 10/11/2017.
 */
public class NpcSpawner {
    private final NewGameWorld gameWorld;
    private final List<SpawnRules> npcSpawners = new ArrayList<>();

    public NpcSpawner(final NewGameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }

    /**
     * Determines which NPCs can be spawned and spawns them.
     */
    public void executeSpawnRules() {
        //The current time. More efficient that repeatedly calling System.currentTimeMillis()
        final long time = System.currentTimeMillis();

        //Get a list of SpawnRules instances which are eligible.
        //They are eligible if enough time has passed and NPC population for those rules are under the limit.
        for(final SpawnRules rule: npcSpawners) {
            if(rule.getNextIntervalTime() < time && rule.getCurrentPopulation() < rule.getMaxPopulation()) {
                rule.spawn(gameWorld);
                rule.resetNextIntervalTime();
            }
        }
    }

    public void addNpcSpawnRules(final SpawnRules rules) {
        this.npcSpawners.add(rules);
        this.gameWorld.addWorldEventListener(rules);
    }

    public void removeNpcSpawnRules(final SpawnRules rules) {
        this.npcSpawners.remove(rules);
        this.gameWorld.removeWorldEventListener(rules);
    }
}
