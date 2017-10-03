///<reference path="./collections/Set.ts"/>

/**
 * Represents any game entity. A game entity can be contained within any other
 * game entity, and can contain other game entities.
 */
interface GameEntity {
    /**
     * Gets a set of all game entities contained within this game entity.
     * @returns {Set<GameEntity>}
     */
    getChildEntities(): Set<GameEntity>;

    /**
     * Gets the game entity this game entity is contained within.
     * @returns {GameEntity}
     */
    getParentEntity(): GameEntity;

    /**
     * Gets the X position of this game entity.
     */
    getXPosition(): number;

    /**
     * Gets the Y position of this game entity.
     */
    getYPosition(): number;

    /**
     * Sets the position of the game entity;
     * @param x
     * @param y
     */
    setPosition(x: number, y:number): void;
}