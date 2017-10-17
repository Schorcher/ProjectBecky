abstract class Npc implements GameEntity, Updateable, Renderable {
    static STATE_NEW_NPC: number = 111;
    static STATE_UPDATE_NPC: number = 112;
    static STATE_DEAD_NPC: number = 113;

    protected position: Point;
    protected angle: number;
    protected health: number;
    protected npcId: number;
    protected parent: GameEntity;

    constructor(parent: GameEntity, npcId: number) {
        this.parent = parent;
        this.npcId = npcId;
        this.position = new Point(0, 0);
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
        this.position = new Point(x, y);
    }

    public setAngle(angle: number) {
        this.angle = angle;
    }

    public getAngle(): number {
        return this.angle;
    }

    abstract update(ellapsedTime: number): void;

    abstract draw(context: CanvasRenderingContext2D, screenOrigin: Point): void;
}