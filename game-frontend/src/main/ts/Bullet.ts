///<reference path="./Renderable.ts"/>
///<reference path="./Updateable.ts"/>
///<reference path="./collections/Point.ts"/>
///<reference path="./GameEntity.ts"/>

/**
 * The bullet class
 */
class Bullet implements Renderable, Updateable, GameEntity {
    private static outlineThickness = 4     /2;
    private static fillRadius = 12;
    private static fillDiameter = Bullet.fillRadius * 2;
    private static totalRadius = Bullet.fillRadius + Bullet.outlineThickness;

    private owner: Player;
    private position: Point;
    private velocity: Point;
    private fillColor: string = "#a8ff96";
    private strokeColor: string = "#000000";
    private id: number;

    constructor(owner: Player, id: number, position: Point, velocity: Point) {
        this.owner = owner;
        this.position = new Point(position.getX(), position.getY());
        this.velocity = new Point(velocity.getX(), velocity.getY());
        this.id = id;
    }

    public getId(): number {
        return this.id;
    }

    public setFillColor(color: string): void {
        this.fillColor = color;
    }

    public setStrokeColor(color: string): void {
        this.strokeColor = color;
    }

    public draw(context: CanvasRenderingContext2D, screenOrigin: Point): void {
        let screenX: number = this.position.getX() - screenOrigin.getX();
        let screenY: number = this.position.getY() - screenOrigin.getY();
        let canvas: HTMLCanvasElement = context.canvas;
        if(screenX < -Bullet.totalRadius || screenX - canvas.width > Bullet.totalRadius ||
           screenY < -Bullet.totalRadius || screenY - canvas.height > Bullet.totalRadius) {
            return; //off screen, don't draw
        }

        //set fill and stroke style
        context.beginPath();
        context.fillStyle = this.fillColor;
        context.arc(screenX, screenY, Bullet.fillRadius, 0, 2*Math.PI, false);
        context.fill();
        context.strokeStyle = this.strokeColor;
        context.lineWidth = Bullet.outlineThickness;
        context.arc(screenX, screenY, Bullet.totalRadius, 0, 2*Math.PI, false);
        context.stroke();
        context.closePath();
    }

    public getXPosition(): number {
        return this.position.getX();
    }

    public getYPosition(): number {
        return this.position.getY();
    }

    public setPosition(x: number, y: number): void {
        this.position = new Point(x, y);
    }

    public update(ellapsedTime: number): void {
        //TODO: When lag compensation starts being a thing then update
    }

    public getChildEntities(): Set<GameEntity> {
        return new Set<GameEntity>();
    }

    public getParentEntity(): GameEntity {
        return this.owner;
    }
}