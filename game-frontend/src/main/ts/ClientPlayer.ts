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

    constructor(parent: GameEntity, x: number = 0, y: number = 0, angle: number = 0, username: string) {
        this.position = new Point(x, y);
        this.velocity = new Point(0, 0);
        this.acceleration = new Point(0, 0);
        this.lagCompensateVelocity = new Point(0, 0);
        this.lagCompensateFrames = 0;
        this.angle = angle;
        this.parent = parent;
        this.username = username;
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
        //the current player is simply drawn in the center of the screen always. Never elsewhere
        //other players will be drawn in the proper position.

        //draw a red circle
        //in phase 2 draw an image
        context.beginPath();
        context.arc(context.canvas.width/2, context.canvas.height/2, 32, 0, 2*Math.PI, false);
        context.fillStyle = "green";
        context.fill();
        context.lineWidth = 5;
        context.strokeStyle = "#003300";
        context.stroke();
        context.closePath();

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
            this.handleBorderCollision();
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

    private handleBorderCollision(): void {
        //TODO: Handle collisions with the world border (blocked phase 1)
    }
}