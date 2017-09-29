///<reference path="./collections/Point.ts"/>
///<reference path="./Renderable.ts"/>

/**
 * This is the interface for any renderer classes.
 */
interface Renderer {
    /**
     * Adds a renderable object to the renderer. All added objects will rendered when
     * the draw method is called.
     * @param arg
     */
    addRenderable(arg: Renderable): void;

    /**
     * Removes a renderable object so it is no longer drawn by the renderer when draw is called.
     * @param arg
     * @return Returns true if the renderable was removed, or false if it was never added to begin with.
     */
    removeRenderable(arg: Renderable): boolean;

    updateScreenOrigin(screenOrigin: Point): void;

    /**
     * Draws all added renderables to the screen.
     */
    draw(): void;
}