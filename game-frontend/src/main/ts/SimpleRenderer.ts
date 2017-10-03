///<reference path="./Renderer.ts"/>
///<reference path="./ClientPlayer.ts"/>
///<reference path="./GameBackground.ts"/>
///<reference path="./collections/Set.ts"/>
///<reference path="./OpponentPlayer.ts"/>
///<reference path="./Bullet.ts"/>

/**
 * A simple 2D renderer class.
 */
class SimpleRenderer implements Renderer {
    private clientPlayer: ClientPlayer;
    private opponentPlayers: Set<OpponentPlayer> = new Set<OpponentPlayer>();
    private bullets: Set<Bullet> = new Set<Bullet>();
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
        else if(element instanceof Bullet) {
            this.bullets.add(element as Bullet);
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
        }
        else if(this.bullets.contains(element as Bullet)) {
            this.bullets.remove(element as Bullet);
        }
    }

    public updateScreenOrigin(screenOrigin: Point): void {
        this.screenOrigin = screenOrigin;
    }

    public draw(): void {
        //clear the screen
        this.renderingContext.clearRect(0, 0, this.renderingContext.canvas.width, this.renderingContext.canvas.height);

        //render the background
        this.gameBackground.draw(this.renderingContext, this.screenOrigin);

        //render opponents
        for(let i = 0; i < this.opponentPlayers.length; i++) {
            let p: OpponentPlayer = this.opponentPlayers.get(i);
            p.draw(this.renderingContext, this.screenOrigin);
        }

        //render bullets
        for(let i = 0; i < this.bullets.length; i++) {
            let b: Bullet = this.bullets.get(i);
            b.draw(this.renderingContext, this.screenOrigin);
        }

        //render the client player
        this.clientPlayer.draw(this.renderingContext, this.screenOrigin);
    }
}