///<reference path="./Renderer.ts"/>
///<reference path="./ClientPlayer.ts"/>
///<reference path="./GameBackground.ts"/>
///<reference path="./collections/Set.ts"/>

/**
 * A simple 2D renderer class.
 */
class SimpleRenderer implements Renderer {
    private clientPlayer: ClientPlayer;
    private gameBackground: GameBackground;
    private renderingContext: CanvasRenderingContext2D;
    private offScreenRenderingContext: CanvasRenderingContext2D;
    private offScreenCanvas = document.createElement('canvas');
    private screenOrigin: Point;
    private elements: Set<Renderable> = new Set<Renderable>();

    constructor(context: CanvasRenderingContext2D) {
        this.renderingContext = context;
        this.offScreenRenderingContext = new CanvasRenderingContext2D;
        this.screenOrigin = new Point();

    }

    public addRenderable(element: Renderable): void {
        if(element instanceof ClientPlayer) {
            this.clientPlayer = element as ClientPlayer;
        }
        else if(element instanceof GameBackground) {
            this.gameBackground = element as GameBackground;
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

        return this.elements.remove(element);
    }

    public updateScreenOrigin(screenOrigin: Point): void {
        this.screenOrigin = screenOrigin;
    }

    public offScreenDraw(): void{
        //render the background
        this.gameBackground.draw(this.offScreenRenderingContext, this.screenOrigin);

        //render opponents
        for(let i = 0; i < this.elements.length; i++) {
            this.elements.get(i).draw(this.offScreenRenderingContext, this.screenOrigin);
        }

        //render the client player
        this.clientPlayer.draw(this.offScreenRenderingContext, this.screenOrigin);
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
    }
}