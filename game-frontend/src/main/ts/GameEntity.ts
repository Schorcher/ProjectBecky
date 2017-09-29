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
}