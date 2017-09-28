/**
 * A simple 2D renderer class.
 */
class SimpleRenderer implements Renderer {
    private clientPlayer: ClientPlayer;
    private opponentPlayers: Set<OpponentPlayer> = new Set<OpponentPlayer>();
    private gameBackground: GameBackground;
    private renderingContext: CanvasRenderingContext2D;
    private screenOrigin: Point;

    private

    constructor(context: CanvasRenderingContext2D) {
        this.renderingContext = context;
        this.screenOrigin = new Point();
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
        if(element instanceof ClientPlayer) {
            this.clientPlayer = null;
            return true;
        }
        if(element instanceof GameBackground) {
            this.gameBackground = null;
            return true;
        }
        if(this.opponentPlayers.contains(element)) {
            this.opponentPlayers.remove(element);
        }
    }

    public updateScreenOrigin(screenOrigin: Point): void {
        this.screenOrigin = screenOrigin;
    }

    public draw(): void {
        this.renderingContext.clearRect(0, 0, this.renderingContext.canvas.width, this.renderingContext.canvas.height);

        this.gameBackground.draw(this.renderingContext, this.screenOrigin);

        this.clientPlayer.draw(this.renderingContext, this.screenOrigin);
    }
}