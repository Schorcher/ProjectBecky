///<reference path="./GameEntity.ts"/>
///<reference path="./Updateable.ts"/>
///<reference path="./Renderable.ts"/>
///<reference path="./collections/Point.ts"/>
///<reference path="./collections/Set.ts"/>

abstract class Npc implements GameEntity, Updateable, Renderable {
    static STATE_NEW_NPC: number = 111;
    static STATE_UPDATE_NPC: number = 112;
    static STATE_DEAD_NPC: number = 113;

    protected position: Point = new Point(0, 0);
    protected velocity: Point = new Point(0, 0);
    protected acceleration: Point = new Point(0, 0);
    protected angle: number = 0;
    protected health: number;
    protected npcId: number;
    protected parent: GameEntity;
    protected compensationVelocity: Point = new Point(0, 0);
    protected compensationFrames: number = 0;
    protected max_velocity: number = 0;

    constructor(parent: GameEntity, npcId: number) {
        this.parent = parent;
        this.npcId = npcId;
    }

    public getChildEntities(): Set<GameEntity> {
        return new Set<GameEntity>();
    }

    public getParentEntity(): GameEntity {
        return this.parent;
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

    public getXAcceleration(): number {
        return this.acceleration.getX();
    }

    public getYAcceleration(): number {
        return this.acceleration.getY();
    }

    public getHealth(): number {
        return this.health;
    }

    public setHealth(health: number): void {
        this.health = health;
    }

    public getNpcId(): number {
        return this.npcId;
    }

    public setPosition(x: number, y: number): void {
        this.position.setX(x);
        this.position.setY(y);
    }

    public setVelocity(x: number, y: number): void {
        this.velocity.setX(x);
        this.velocity.setY(y);
    }

    public setCompensationVelocity(velocity: Point, numFrames: number): void {
        this.compensationVelocity = velocity;
        this.compensationFrames = numFrames;
    }

    public setAcceleration(x: number, y: number): void {
        this.acceleration.setX(x);
        this.acceleration.setY(y);
    }

    public setAngle(angle: number) {
        this.angle = angle;
    }

    public getAngle(): number {
        return this.angle;
    }

    public update(elapsedTime: number): void {
        if(!LagCompensator.enabled) {
            return;
        }

        let multiplier: number = elapsedTime / 1000.0;

        this.velocity.addX(this.acceleration.getX() * multiplier);
        this.velocity.addY(this.acceleration.getY() * multiplier);
        this.capVelocity();

        this.position.addX((this.velocity.getX() + this.compensationVelocity.getX()) * multiplier);
        this.position.addY((this.velocity.getY() + this.compensationVelocity.getY()) * multiplier);

        if(this.compensationFrames > 0) {
            this.compensationFrames--;
            if(this.compensationFrames == 0) {
                this.compensationVelocity.setX(0);
                this.compensationVelocity.setY(0);
            }
        }
    }

    abstract draw(context: CanvasRenderingContext2D, screenOrigin: Point): void;

    private capVelocity(): void {
        if(this.velocity.getX() > this.max_velocity) {
            this.velocity.setX(this.max_velocity);
        }
        else if(this.velocity.getX() < -this.max_velocity) {
            this.velocity.setX(-this.max_velocity);
        }

        if(this.velocity.getY() > this.max_velocity) {
            this.velocity.setY(this.max_velocity);
        }
        else if(this.velocity.getY() < -this.max_velocity) {
            this.velocity.setY(-this.max_velocity);
        }
    }
}