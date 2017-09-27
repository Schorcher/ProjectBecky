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
        this.elements = new Set<Renderable>();
    }

    public addRenderable(element: Renderable): void {
        if(element instanceof ClientPlayer) {
            this.clientPlayer = element;
        }
        else if(element instanceof OpponentPlayer) {
            this.opponentPlayers.add(element);
        }
        else if(element instanceof GameBackground) {
            this.gameBackground = element;
        }
    }

    public removeRenderable(element: Renderable): boolean {
        return this.elements.remove(element);
    }

    public updateScreenOrigin(screenOrigin: Point): void {
        this.screenOrigin = screenOrigin;
    }

    public draw(): void {
        console.log("FUCK");
        this.gameBackground.draw(this.renderingContext, this.screenOrigin);

        this.clientPlayer.draw(this.renderingContext, this.screenOrigin);
    }
}