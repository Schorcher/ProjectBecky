///<reference path="./Renderable.ts"/>

/**
 * Represents any player. Either the current player or any opponent player.
 */
interface Player extends Renderable, GameEntity {
    /**
     * Gets the username of the player
     */
    getUsername(): string;

    /**
     * Gets the current look direction of the player in RADIANS.
     */
    getAngle(): number;

    /**
     * Sets the player look angle.
     * @param angle
     */
    setAngle(angle: number): void;

    /**
     * Sets the position of the player.
     * @param x
     * @param y
     */
    setPosition(x: number, y: number): void;

    /**
     * Gets this player's score
     * @returns {number}
     */
    getScore(): number;

    /**
     * Sets this player's score.
     * @param {number} score
     */
    setScore(score: number): void;
}