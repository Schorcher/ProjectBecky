import Set from "typescript-collections/dist/lib/Set";

/**
 * Represents any game entity. A game entity can be contained within any other
 * game entity, and can contain other game entities.
 */
export interface GameEntity {
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