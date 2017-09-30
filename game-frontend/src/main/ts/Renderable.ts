///<reference path="./collections/Point.ts"/>

/**
 * This interface represents any object that can be rendered.
 */
class Renderable {
    /**
     * Draws this renderable to the canvas using the provided rendering context.
     * @param context The rendering context to perform drawing operations
     * @param screenOrigin The world-space position of the top left corner of the screen.
     */
    draw(context: CanvasRenderingContext2D, screenOrigin: Point): void {}

    /**
     * Draws this renderable to the canvas using the provided rendering context.
     * @param glContext
     */
    //This is not being implemented, but is commented out in case it is implemented in the future.
    //glDraw(glContext: WebGLRenderingContext): void;
}