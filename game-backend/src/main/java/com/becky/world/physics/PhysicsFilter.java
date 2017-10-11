package com.becky.world.physics;

import com.becky.world.entity.GameEntity;

/**
 * Represents any physics filter.
 * Created by Clayton on 10/10/2017.
 */
public interface PhysicsFilter {
    /**
     * Applies this filter's physics properties to a given entity.
     * All filters are responsible for deciding whether or not they apply to a particular entity.
     * @param gameEntity
     */
    void apply(final GameEntity gameEntity);

    /**
     * Notifies the physics filter to prepare itself for the next "round" of use
     * This should be called at the beginning of each game frame.
     */
    void prepare();
}
