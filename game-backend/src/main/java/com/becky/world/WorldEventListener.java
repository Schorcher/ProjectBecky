package com.becky.world;

import com.becky.world.entity.GameEntity;

/**
 * Abstract class representing the events which can happen in a game world.
 * An abstract class is used so the developer can override only the methods they want to override.
 * Created by Clayton on 10/11/2017.
 */
public interface WorldEventListener {

    /**
     * Called by the a game world AFTER an entity is removed from the world.
     * @param gameWorld The game world an entity is being removed from.
     * @param entity The entity instance that is being removed.
     */
    void onGameEntityRemoved(final NewGameWorld gameWorld, final GameEntity entity);

    /**
     * Called by the game world AFTER an entity is added to the world.
     * @param gameWorld The world the entity was added to.
     * @param entity The entity that was added to the game world.
     */
    void onGameEntityAdded(final NewGameWorld gameWorld, final GameEntity entity);
}
