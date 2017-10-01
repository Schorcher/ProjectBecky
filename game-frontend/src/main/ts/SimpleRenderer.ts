///<reference path="./Renderer.ts"/>
///<reference path="./ClientPlayer.ts"/>
///<reference path="./GameBackground.ts"/>
///<reference path="./collections/Set.ts"/>
///<reference path="./OpponentPlayer.ts"/>

/**
 * A simple 2D renderer class.
 */
class SimpleRenderer implements Renderer {
    private clientPlayer: ClientPlayer;
    private opponentPlayers: Set<OpponentPlayer> = new Set<OpponentPlayer>();
    private gameBackground: GameBackground;
    private renderingContext: CanvasRenderingContext2D;
    private screenOrigin: Point;

    constructor(context: CanvasRenderingContext2D) {
        this.renderingContext = context;
        this.screenOrigin = new Point();
    }

    public addRenderable(element: Renderable): void {
        if(element instanceof ClientPlayer) {
            this.clientPlayer = element as ClientPlayer;
        }
        else if(element instanceof OpponentPlayer) {
            this.opponentPlayers.add(element as OpponentPlayer);
            console.log("ADDED OPPONENT");
        }
        else if(element instanceof GameBackground) {
            this.gameBackground = element as GameBackground;
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
        if (this.opponentPlayers.contains(element as OpponentPlayer)) {
            this.opponentPlayers.remove(element as OpponentPlayer);
            console.log('Removing player...');
        }
    }

    public updateScreenOrigin(screenOrigin: Point): void {
        this.screenOrigin = screenOrigin;
    }

    public draw(): void {
        this.renderingContext.clearRect(0, 0, this.renderingContext.canvas.width, this.renderingContext.canvas.height);

        this.gameBackground.draw(this.renderingContext, this.screenOrigin);
        for(let i = 0; i < this.opponentPlayers.length; i++) {
            let p: OpponentPlayer = this.opponentPlayers.get(i);
            p.draw(this.renderingContext, this.screenOrigin);
        }
        this.clientPlayer.draw(this.renderingContext, this.screenOrigin);
    }
}