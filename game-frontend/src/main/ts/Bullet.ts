///<reference path="./Renderable.ts"/>
///<reference path="./Updateable.ts"/>
///<reference path="./collections/Point.ts"/>
///<reference path="./GameEntity.ts"/>
///<reference path="./Player.ts"/>
///<reference path="./LagCompensator.ts"/>

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
    private lagCompensateVelocity: Point;
    private lagCompensateFrames: number;
    private fillColor: string = "#a8ff96";
    private strokeColor: string = "#000000";
    private id: number;

    constructor(owner: Player, id: number, position: Point, velocity: Point) {
        this.owner = owner;
        this.position = new Point(position.getX(), position.getY());
        this.velocity = new Point(velocity.getX(), velocity.getY());
        this.lagCompensateVelocity = new Point(0, 0);
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

    public getXVelocity(): number {
        return this.velocity.getX();
    }

    public getYVelocity(): number {
        return this.velocity.getY();
    }

    public setPosition(x: number, y: number): void {
        this.position = new Point(x, y);
    }

    public update(elapsedTime: number): void {
        if(!LagCompensator.enabled) {
            return;
        }

        let multiplier: number = elapsedTime / 1000.0;
        this.position.addX((this.velocity.getX() + this.lagCompensateVelocity.getX()) * multiplier);
        this.position.addY((this.velocity.getY() + this.lagCompensateVelocity.getY()) * multiplier);

        if(this.lagCompensateFrames > 0) {
            this.lagCompensateFrames--;
            if(this.lagCompensateFrames == 0) {
                this.lagCompensateVelocity.setX(0);
                this.lagCompensateVelocity.setY(0);
            }
        }
    }

    public getChildEntities(): Set<GameEntity> {
        return new Set<GameEntity>();
    }

    public getParentEntity(): GameEntity {
        return this.owner;
    }

    public setLagCompensateVelocity(lagCompensateVelocity: Point, correctionFps: number): void {
        this.lagCompensateVelocity = lagCompensateVelocity;
        this.lagCompensateFrames = correctionFps;
    }
}