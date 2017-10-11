package com.becky.world.entity;

import com.becky.world.NewGameWorld;
import com.becky.world.physics.PhysicsFilter;

import java.awt.geom.Point2D;
import java.util.*;

public abstract class GameEntity {
    private static long entityCount = 1;
    private final long entityId;

    //parent and children
    protected NewGameWorld container;
    protected GameEntity parentEntity;
    protected List<GameEntity> childrenEntities;

    //position
    protected Point2D.Float position = new Point2D.Float();
    protected Point2D.Float velocity = new Point2D.Float();
    protected Point2D.Float acceleration = new Point2D.Float();
    protected float angles = 0.0f;

    //used for physics filters
    private final List<Class<? extends PhysicsFilter>> filterApplies = new ArrayList<>();

    protected GameEntity(final NewGameWorld container) {
        entityId = entityCount;
        entityCount++;
        this.container = container;
    }

    public long getEntityId() {
        return this.entityId;
    }

    public float getXPosition() {
        return this.position.x;
    }

    public float getYPosition() {
        return this.position.y;
    }

    public void setXPosition(final float xPosition) {
        this.position.x = xPosition;
    }

    public void setYPosition(final float yPosition) {
        this.position.y = yPosition;
    }

    public float getXVelocity() {
        return this.velocity.x;
    }

    public float getYVelocity() {
        return this.velocity.y;
    }

    public void setXVelocity(final float xVelocity) {
        this.velocity.x = xVelocity;
    }

    public void setYVelocity(final float yVelocity) {
        this.velocity.y = yVelocity;
    }

    public float getXAcceleration() {
        return this.acceleration.x;
    }

    public float getYAcceleration() {
        return this.acceleration.y;
    }

    public void setXAcceleration(final float xAcceleration) {
        this.acceleration.x = xAcceleration;
    }

    public void setYAcceleration(final float yAcceleration) {
        this.acceleration.y = yAcceleration;
    }

    public void setAngles(final float angles) {
        this.angles = angles;
    }

    public float getAngles() {
        return this.angles;
    }

    public abstract void tick(final long elapsedTime);

    public NewGameWorld getGameWorld() {
        return this.container;
    }

    public GameEntity getParent() {
        return this.parentEntity;
    }

    public Collection<GameEntity> getChildren() {
        return Collections.unmodifiableList(this.childrenEntities);
    }

    public boolean doesPhysicsApply(final Class<? extends PhysicsFilter> physics) {
        return this.filterApplies.contains(physics);
    }

    protected void setPhysicsFilters(final Class<? extends PhysicsFilter>... filters) {
        this.filterApplies.clear();
        this.filterApplies.addAll(Arrays.asList(filters));
    }
}
