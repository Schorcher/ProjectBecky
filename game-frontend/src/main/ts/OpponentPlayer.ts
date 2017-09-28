/**
 * Represents another player. Not the player on this client.
 */
class OpponentPlayer implements Player, Updateable, GameEntity {
    private position : Point;
    private angle: number;
    private username: string;
    private parent: GameEntity;

    constructor(parent: GameEntity, username: string) {
        this.position = new Point();
        this.username = username;
        this.parent = parent;
    }

    public getUsername(): string {
        return this.username;
    }

    public getXPosition(): number {
        return this.position.getX();
    }

    public getYPosition(): number {
        return this.position.getY();
    }

    public getAngle(): number {
        return this.angle;
    }

    public setAngle(angle: number): void {
        this.angle = angle;
    }

    public setPosition(x: number, y: number): void {
        this.position.setX(x);
        this.position.setY(y);
    }

    public getParentEntity(): GameEntity {
        return this.parent;
    }

    public getChildEntities(): Set<GameEntity> {
        return new Set<GameEntity>();
    }

    public update(elapsedTime: number) : void {
        //TODO: Phase 2, lag compensation
    }

    public draw(context: CanvasRenderingContext2D, screenOrigin: Point): void {
        let screenPos: Point = this.getScreenspacePosition(screenOrigin);
        let radius: number = 32;

        if(screenPos.getX() < -radius || screenPos.getX() > context.canvas.width + radius ||
           screenPos.getY() < -radius || screenPos.getY() > context.canvas.height + radius) {
            console.log("PLAYER OOB");
            return; //no rendering needed because player is outside of renderable screen space
        }

        //draw a red circle
        //in phase 2 draw an image
        context.beginPath();
        context.arc(screenPos.getX(), screenPos.getY(), 32, 0, 2*Math.PI, false);
        context.fillStyle = "red";
        context.fill();
        context.lineWidth = 5;
        context.strokeStyle = "#330000";
        context.stroke();
        context.closePath();
    }

    private getScreenspacePosition(screenOrigin: Point): Point {
        return new Point(this.position.getX() - screenOrigin.getX(),
                         this.position.getY() - screenOrigin.getY());
    }
}