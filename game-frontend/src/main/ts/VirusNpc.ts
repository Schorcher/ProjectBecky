///<reference path="./Npc.ts"/>
///<reference path="./collections/Point.ts"/>
///<reference path="./GameEntity.ts"/>
///<reference path="./LagCompensator.ts"/>

class VirusNpc extends Npc {
    private static firstLegIndex: number = 11;
    private static drawPoints: Point[] = [
        new Point(-12, -20),
        new Point(0, -6),
        new Point(12, -20),
        new Point(-12, -20),
        new Point(0, -28),
        new Point(12, -20),
        new Point(12, -6),
        new Point(-12, -6),
        new Point(-12, -20),
        new Point(0, -6),
        new Point(0, 8),
        new Point(), //set on each frame. Left leg
        new Point()  //set on each frame. Right leg
    ];

    private transformedPoints: Point[] = [];

    constructor(parent: GameEntity, npcId: number) {
        super(parent, npcId);
        this.max_velocity = 250.0;

        //init the point objects for the transformed points
        for(let i = 0; i < 13; i++) {
            this.transformedPoints[i] = new Point();
        }
    }

    public draw(context: CanvasRenderingContext2D, screenOrigin: Point): void {
        //translate the points to screen space
        this.setLegEndpoints();
        this.rotatePoints();
        this.translatePoints(screenOrigin);

        //prepare background draw colors and style
        context.strokeStyle = "#007875";
        context.lineWidth = 3;
        this.drawLines(context);

        //now prepare to draw the points
        context.strokeStyle = "#00fffc";
        context.lineWidth = 1;
        this.drawLines(context);
    }

    private drawLines(context: CanvasRenderingContext2D): void {
        context.beginPath();
        context.moveTo(this.transformedPoints[0].getX(), this.transformedPoints[0].getY());
        for(let i = 1; i < 9; i++) {
            context.lineTo(this.transformedPoints[i].getX(),
                this.transformedPoints[i].getY());
        }
        context.stroke();
        context.moveTo(this.transformedPoints[9].getX(), this.transformedPoints[9].getY());
        context.lineTo(this.transformedPoints[10].getX(), this.transformedPoints[10].getY());
        context.stroke();

        //draw the legs
        context.lineTo(this.transformedPoints[11].getX(), this.transformedPoints[11].getY());
        context.stroke();
        context.moveTo(this.transformedPoints[10].getX(), this.transformedPoints[10].getY());
        context.lineTo(this.transformedPoints[12].getX(), this.transformedPoints[12].getY());
        context.stroke();
        context.closePath();
    }

    private setLegEndpoints(): void {
        let speedSquared: number = Math.pow(this.getXVelocity(), 2) + Math.pow(this.getYVelocity(), 2);
        let changeX: number = Math.min(4.0, 0.000128 * speedSquared);
        let changeY: number = Math.min(1.0, 0.000064 * speedSquared);

        //note, still working with local points as in, points that have not
        //been transformed or rotated
        this.transformedPoints[11].setX(-8 + changeX);
        this.transformedPoints[11].setY(15 + changeY);
        this.transformedPoints[12].setX(8 - changeX);
        this.transformedPoints[12].setY(15 + changeY);
    }

    private rotatePoints(): void {
        let sinAngle: number = Math.sin(super.getAngle());
        let cosAngle: number = Math.cos(super.getAngle());

        //this is the first points, which excludes the leg endpoints
        for(let i = 0; i < VirusNpc.firstLegIndex; i++) {
            let x: number = VirusNpc.drawPoints[i].getX();
            let y: number = VirusNpc.drawPoints[i].getY();
            this.transformedPoints[i].setX(x*cosAngle - y*sinAngle);
            this.transformedPoints[i].setY(x*sinAngle + y*cosAngle);
        }

        //leg endpoints aka the last two points in the array
        for(let i = VirusNpc.firstLegIndex; i < this.transformedPoints.length; i++) {
            let x: number = this.transformedPoints[i].getX();
            let y: number = this.transformedPoints[i].getY();
            this.transformedPoints[i].setX(x*cosAngle - y*sinAngle);
            this.transformedPoints[i].setY(x*sinAngle + y*cosAngle);
        }
    }

    private translatePoints(screenOrigin: Point): void {
        let tX: number = screenOrigin.getX();
        let tY: number = screenOrigin.getY();

        for(let i = 0; i < this.transformedPoints.length; i++) {
            this.transformedPoints[i].addX(super.getXPosition() - tX);
            this.transformedPoints[i].addY(super.getYPosition() - tY);
        }
    }
}