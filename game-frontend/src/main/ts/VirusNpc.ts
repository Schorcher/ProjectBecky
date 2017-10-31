///<reference path="./Npc.ts"/>
///<reference path="./collections/Point.ts"/>
///<reference path="./GameEntity.ts"/>
///<reference path="./LagCompensator.ts"/>

class VirusNpc extends Npc {
    constructor(parent: GameEntity, npcId: number) {
        super(parent, npcId);
    }

    public update(elapsedTime: number): void {
        //call the super update method if lag compensation is enabled
        if(LagCompensator.enabled) {
            super.update(elapsedTime);
        }
    }

    public draw(context: CanvasRenderingContext2D, screenOrigin: Point): void {
        context.beginPath();
        context.arc(this.position.getX() - screenOrigin.getX(), this.position.getY() - screenOrigin.getY(), 16, 0, 2*Math.PI, false);
        context.fillStyle = "blue";
        context.fill();
        context.lineWidth = 5;
        context.strokeStyle = "#000133";
        context.stroke();
        context.closePath();
    }
}