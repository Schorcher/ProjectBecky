///<reference path="./Npc.ts"/>
///<reference path="./collections/Point.ts"/>
///<reference path="./GameEntity.ts"/>
///<reference path="./LagCompensator.ts"/>

class VirusNpc extends Npc {
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
        // context.beginPath();
        // context.arc(this.position.getX() - screenOrigin.getX(), this.position.getY() - screenOrigin.getY(), 16, 0, 2*Math.PI, false);
        // context.fillStyle = "blue";
        // context.fill();
        // context.lineWidth = 5;
        // context.strokeStyle = "#000133";
        // context.stroke();
        // context.closePath();

        //translate the points to screen space
        this.setLegEndpoints();
        //this.rotatePoints();
        this.translatePoints(screenOrigin);

        //now prepare to draw the points
        context.strokeStyle = "#00fffc";
        context.lineWidth = 1;

        //draw main body parts
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
        let changeY: number = Math.min(1.0, 0.000032 * speedSquared);
        console.log(super.getYVelocity());

        //note, still working with local points as in, points that have not
        //been transformed or rotated
        this.transformedPoints[11].setX(-8 + changeX);
        this.transformedPoints[11].setY(15 + changeY);
        this.transformedPoints[12].setX(8 - changeX);
        this.transformedPoints[12].setY(15 + changeY);
    }

    private rotatePoints(): void {
        let sinAngle: number = Math.sin(this.angle);
        let cosAngle: number = Math.cos(this.angle);

        //this is the first 11 points, which excludes the leg endpoints
        for(let i = 0; i < 11; i++) {
            let x: number = VirusNpc.drawPoints[i].getX();
            let y: number = VirusNpc.drawPoints[i].getY();
            this.transformedPoints[i].setX(x*cosAngle - y*sinAngle);
            this.transformedPoints[i].setY(y*cosAngle + x*sinAngle);
        }

        //leg endpoints aka the last two points in the array
        for(let i = 11; i < 13; i++) {
            let x: number = this.transformedPoints[i].getX();
            let y: number = this.transformedPoints[i].getY();
            this.transformedPoints[i].setX(x*cosAngle - y*sinAngle);
            this.transformedPoints[i].setY(x*sinAngle + y*cosAngle);
        }
    }

    private translatePoints(screenOrigin: Point): void {
        let tX: number = screenOrigin.getX();
        let tY: number = screenOrigin.getY();

        for(let i = 0; i < 11; i++) {
            this.transformedPoints[i].setX(VirusNpc.drawPoints[i].getX());
            this.transformedPoints[i].setY(VirusNpc.drawPoints[i].getY());
            this.transformedPoints[i].addX(super.getXPosition() - tX);
            this.transformedPoints[i].addY(super.getYPosition() - tY);
        }
        for(let i = 11; i < 13; i++) {
            this.transformedPoints[i].addX(super.getXPosition() - tX);
            this.transformedPoints[i].addY(super.getYPosition() - tY);
        }
    }
}