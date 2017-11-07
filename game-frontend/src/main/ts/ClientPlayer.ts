///<reference path="./Player.ts"/>
///<reference path="./Updateable.ts"/>
///<reference path="./GameEntity.ts"/>
///<reference path="./collections/Point.ts"/>

/**
 * Represents the current player. The avatar being controlled by the user.
 * See OpponentPlayer for the other players.
 */
class ClientPlayer implements Player, Updateable, GameEntity {
    static acceleration: number = 1800;
    static max_velocity: number = 450;

    private static points: Point[] = [
        new Point(0, -32),
        new Point(12, -10),
        new Point(32, 32),
        new Point(0, 14),
        new Point(-32, 32),
        new Point(-12, -10)
    ];
    private static polyfill1_ind: number[] = [0, 5, 3, 1];
    private static polyfill2_ind: number[] = [5, 3, 4];
    private static polyfill3_ind: number[] = [3, 1, 2];
    private static outline1_ind: number[] = [0, 1, 2, 3, 4, 5, 0];
    private static outline2_ind: number[] = [5, 3, 1];
    private static outlineWidth: number = 2;
    private static outlineEndcaps: string = "round";
    private static outlineJoins: string = "bevel";
    private static fillColor1: string = "#000000";
    private static fillColor2: string = "#434343";
    private static lineColor: string = "#00FF00";
    private static leftLegTip: number = 4;
    private static rightLegTip: number = 2;
    private static legElongateRatio: number = 5.0 / ClientPlayer.max_velocity;

    private position: Point;
    private acceleration: Point;
    private velocity: Point;
    private lagCompensateVelocity: Point;
    private lagCompensateFrames: number;
    private parent: GameEntity;
    private angle: number; //radian angle player is aiming towards
    private moveUp: boolean;
    private moveDown: boolean;
    private moveLeft: boolean;
    private moveRight: boolean;
    private shooting: boolean;
    private username: string;
    private score: number = 0;
    private health: number = 10;
    private transformedPoints: Point[] = [];

    constructor(parent: GameEntity, x: number = 0, y: number = 0, angle: number = 0, username: string) {
        this.position = new Point(x, y);
        this.velocity = new Point(0, 0);
        this.acceleration = new Point(0, 0);
        this.lagCompensateVelocity = new Point(0, 0);
        this.lagCompensateFrames = 0;
        this.angle = angle;
        this.parent = parent;
        this.username = username;

        for(let i = 0; i < ClientPlayer.points.length; i++) {
            this.transformedPoints[i] = new Point();
        }
    }

    public getUsername(): string {
        return this.username;
    }

    public getXPosition = (): number => {
        return this.position.getX();
    }

    public getYPosition = (): number => {
        return this.position.getY();
    }

    public getXVelocity = (): number => {
        return this.velocity.getX();
    }

    public getYVelocity = (): number => {
        return this.velocity.getY();
    }

    public getXAcceleration = (): number => {
        return this.acceleration.getX();
    }

    public getYAcceleration = (): number => {
        return this.acceleration.getY();
    }

    public setVelocity = (x: number, y: number): void => {
        this.velocity.setX(x);
        this.velocity.setY(y);
    }

    public setLagCompensateVelocity(velocity: Point, frames: number): void {
        this.lagCompensateVelocity = velocity;
        this.lagCompensateFrames = frames;
    }

    public setAcceleration = (x: number, y: number): void => {
        this.acceleration.setX(x);
        this.acceleration.setY(y);
    }

    public setPosition = (x: number, y: number): void => {
        this.position.setX(x);
        this.position.setY(y);
    }

    public getAngle = (): number => {
        return this.angle;
    }

    public setAngle = (angle: number): void => {
        this.angle = angle;
    }

    public getMovingUp = (): boolean => {
        return this.moveUp;
    }

    public getMovingDown = (): boolean => {
        return this.moveDown;
    }

    public getMovingLeft = (): boolean => {
        return this.moveLeft;
    }

    public getMovingRight = (): boolean => {
        return this.moveRight;
    }

    public setMoveUp = (up: boolean): void => {
        this.moveUp = up;
        this.acceleration.setY(-ClientPlayer.acceleration);
    }

    public setMoveDown = (down: boolean): void => {
        this.moveDown = down;
        this.acceleration.setY(ClientPlayer.acceleration);
    }

    public setMoveLeft = (left: boolean): void => {
        this.moveLeft = left;
        this.acceleration.setX(ClientPlayer.acceleration);
    }

    public setMoveRight = (right: boolean): void => {
        this.moveRight = right;
        this.acceleration.setX(-ClientPlayer.acceleration);
    }

    public getChildEntities = (): Set<GameEntity> => {
        return new Set<GameEntity>();
    }

    public getParentEntity = (): GameEntity => {
        return this.parent;
    }

    public setShooting = (shooting: boolean): void => {
        this.shooting = shooting;
    }

    public isShooting = (): boolean => {
        return this.shooting;
    }

    public setScore = (score: number): void => {
        this.score = score;
    }

    public getScore = (): number => {
        return this.score;
    }

    public setHealth = (health: number): void => {
        this.health = health;
    }

    public getHealth = () => {
        return this.health;
    }

    public draw(context: CanvasRenderingContext2D, screenOrigin: Point): void {
        this.preparePoints(); //generate the points themselves
        this.rotatePoints(); //rotate the generated points around the origin
        this.transformPoints(context.canvas.width/2, context.canvas.height/2); //shift the points to the center of the screen

        //draw the main ship body
        context.fillStyle = ClientPlayer.fillColor1;
        this.fillPolygon(context, ClientPlayer.polyfill1_ind);
        context.fillStyle = ClientPlayer.fillColor2;
        this.fillPolygon(context, ClientPlayer.polyfill2_ind);
        this.fillPolygon(context, ClientPlayer.polyfill3_ind);

        //draw the outline
        context.strokeStyle = ClientPlayer.lineColor;
        context.lineJoin = ClientPlayer.outlineJoins;
        context.lineWidth = ClientPlayer.outlineWidth;
        context.lineCap = ClientPlayer.outlineEndcaps;
        this.linePolygon(context, ClientPlayer.outline1_ind);
        this.linePolygon(context, ClientPlayer.outline2_ind);
        context.lineJoin = "";
        context.lineCap = "";

        // Draw the username under player
        context.font = "12px Arial";
        context.fillStyle = "yellow";
        context.fillText(this.username, (context.canvas.width / 2), (context.canvas.height / 2));

    }

    public update(elapsedTime: number): void {
        //this.updateVelocity(fracSecond);
        if(LagCompensator.enabled) {
            let fracSecond: number = elapsedTime / 1000.0;
            this.updateVelocity(fracSecond);
            this.updatePosition(fracSecond);
        }
    }

    private updateVelocity(fracSecond: number): void {
        //x component
        if(Math.abs(this.acceleration.getX()) < 0.05) {
            //decelerating
            if(this.velocity.getX() > 0.05) {
                this.velocity.addX(-ClientPlayer.acceleration * fracSecond);
                if(this.velocity.getX() < 0.0) {
                    this.velocity.setX(0);
                }
            }
            else {
                this.velocity.addX(ClientPlayer.acceleration * fracSecond);
                if(this.velocity.getX() > 0.0) {
                    this.velocity.setX(0);
                }
            }
        }
        else {
            //accelerating
            this.velocity.addX(this.acceleration.getX() * fracSecond);
        }

        //y component
        if(Math.abs(this.acceleration.getY()) < 0.05) {
            //decelerating
            if(this.velocity.getY() > 0.05) {
                this.velocity.addY(-ClientPlayer.acceleration * fracSecond);
                if(this.velocity.getY() < 0.0) {
                    this.velocity.setY(0);
                }
            }
            else {
                this.velocity.addY(ClientPlayer.acceleration * fracSecond);
                if(this.velocity.getY() > 0.0) {
                    this.velocity.setY(0);
                }
            }
        }
        else {
            //accelerating
            this.velocity.addY(this.acceleration.getY() * fracSecond);
        }

        //cap velocity
        ClientPlayer.capVelocity(this.velocity);
    }

    static capVelocity(velocity: Point): void {
        //cap velocity
        if(velocity.getX() > ClientPlayer.max_velocity) {
            velocity.setX(ClientPlayer.max_velocity);
        }
        else if(velocity.getX() < -ClientPlayer.max_velocity) {
            velocity.setX(-ClientPlayer.max_velocity);
        }
        if(velocity.getY() > ClientPlayer.max_velocity) {
            velocity.setY(ClientPlayer.max_velocity);
        }
        else if(velocity.getY() < -ClientPlayer.max_velocity) {
            velocity.setY(-ClientPlayer.max_velocity);
        }
    }

    private updatePosition(fracSecond: number): void {
        //update position
        this.position.addX((this.velocity.getX() + this.lagCompensateVelocity.getX()) * fracSecond);
        this.position.addY((this.velocity.getY() + this.lagCompensateVelocity.getY()) * fracSecond);

        if(this.lagCompensateFrames > 0) {
            this.lagCompensateFrames--;
            if(this.lagCompensateFrames == 0) {
                this.lagCompensateVelocity.setX(0);
                this.lagCompensateVelocity.setY(0);
            }
        }
    }

    private preparePoints(): void {
        //copy over the original positions of the points
        for(let i = 0; i < ClientPlayer.points.length; i++) {
            let src: Point = ClientPlayer.points[i];
            let dest: Point = this.transformedPoints[i];
            dest.setX(src.getX());
            dest.setY(src.getY());
        }

        //set the leg points
        let leftLegPoint: Point = this.transformedPoints[ClientPlayer.leftLegTip];
        let rightLegPoint: Point = this.transformedPoints[ClientPlayer.rightLegTip];
        let legElongateAmount: number = ClientPlayer.legElongateRatio * (Math.abs(this.velocity.getX()) + Math.abs(this.velocity.getY()));

        leftLegPoint.addX(legElongateAmount);
        leftLegPoint.addY(legElongateAmount);
        rightLegPoint.addX(-legElongateAmount);
        rightLegPoint.addY(legElongateAmount);
    }

    private rotatePoints(): void {
        let sinAngle: number = Math.sin(this.angle + Math.PI/2.0);
        let cosAngle: number = Math.cos(this.angle + Math.PI/2.0);

        //rotate all of the points around the origin
        //It is assumed the points have not been transformed
        for (let i = 0; i < ClientPlayer.points.length; i++) {
            let p: Point = this.transformedPoints[i];
            let x: number = p.getX();
            let y: number = p.getY();
            p.setX(x*cosAngle - y*sinAngle);
            p.setY(x*sinAngle + y*cosAngle);
        }
    }

    private transformPoints(screenX: number, screenY: number): void {
        for(let i = 0; i < this.transformedPoints.length; i++) {
            this.transformedPoints[i].addX(screenX);
            this.transformedPoints[i].addY(screenY);
        }
    }

    private fillPolygon(context: CanvasRenderingContext2D, indices: number[]): void {
        context.beginPath();
        let p: Point = this.transformedPoints[indices[0]];
        context.moveTo(p.getX(), p.getY());
        for(let i = 1; i < indices.length; i++) {
            p = this.transformedPoints[indices[i]];
            context.lineTo(p.getX(), p.getY());
        }
        context.closePath();
        context.fill();
    }

    private linePolygon(context: CanvasRenderingContext2D, indices: number[]): void {
        context.beginPath();
        let p: Point = this.transformedPoints[indices[0]];
        context.moveTo(p.getX(), p.getY());
        for(let i = 1; i < indices.length; i++) {
            p = this.transformedPoints[indices[i]];
            context.lineTo(p.getX(), p.getY());
        }
        context.stroke();
        context.closePath();
    }
}