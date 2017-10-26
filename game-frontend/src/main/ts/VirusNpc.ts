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

    }
}