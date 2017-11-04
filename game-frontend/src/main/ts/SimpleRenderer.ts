///<reference path="./Renderer.ts"/>
///<reference path="./ClientPlayer.ts"/>
///<reference path="./GameBackground.ts"/>
///<reference path="./collections/Set.ts"/>
///<reference path="./GameUI.ts"/>

/**
 * A simple 2D renderer class.
 */
class SimpleRenderer implements Renderer {
    private clientPlayer: ClientPlayer;
    private gameBackground: GameBackground;
    private renderingContext: CanvasRenderingContext2D;
    private screenOrigin: Point;
    private elements: Set<Renderable> = new Set<Renderable>();
    private gameUI: GameUI;

    constructor(context: CanvasRenderingContext2D) {
        this.renderingContext = context;
        this.screenOrigin = new Point();
    }

    public addRenderable(element: Renderable): void {
        if(element instanceof ClientPlayer) {
            this.clientPlayer = element as ClientPlayer;
        }
        else if(element instanceof GameBackground) {
            this.gameBackground = element as GameBackground;
        }
        else if (element instanceof GameUI) {
            this.gameUI = element as GameUI;
        }
        else {
            this.elements.add(element);
        }
    }

    public removeRenderable(element: Renderable): boolean {
        if(element instanceof ClientPlayer) {
            this.clientPlayer = null;
            return true;
        }
        if(element instanceof GameBackground) {
            this.gameBackground = null;
            return true;
        }
        if (element instanceof GameUI) {
            this.gameUI = null;
            return true;
        }

        return this.elements.remove(element);
    }

    public updateScreenOrigin(screenOrigin: Point): void {
        this.screenOrigin = screenOrigin;
    }

    public draw(): void {
        //render the background
        this.gameBackground.draw(this.renderingContext, this.screenOrigin);

        //render opponents
        for(let i = 0; i < this.elements.length; i++) {
            this.elements.get(i).draw(this.renderingContext, this.screenOrigin);
        }

        //render the client player
        this.clientPlayer.draw(this.renderingContext, this.screenOrigin);

        // Render the UI Components
        this.gameUI.draw(this.renderingContext, this.screenOrigin);

    }
}