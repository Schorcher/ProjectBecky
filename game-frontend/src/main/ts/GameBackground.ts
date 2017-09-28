/**
 * Represents, and is responsible for the background of the game screen as well as the world border.
 */
class GameBackground implements Renderable {
    private gridSize: number = 48; //default grid size is 16 pixels
    private lineThickness: number = 0.5;
    private borderThickness: number = 30;
    private borderColor: string = "#000000";
    private gridColor: string = "#545454";

    private player: ClientPlayer;
    private viewWidth: number;
    private viewHeight: number;
    private worldWidth: number;
    private worldHeight: number;

    /**
     * Creates a new GameBackground object
     * @param viewWidth The width of the viewport
     * @param viewHeight The height of the viewport
     * @param worldWidth The width of the world itself.
     * @param worldHeight The height of the world itself.
     * @constructor
     */
    constructor(viewWidth: number,
                viewHeight: number,
                worldWidth: number,
                worldHeight: number) {
        this.player = null;
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }

    /**
     * Links a player to the background. The player is what determines the relative positioning of the background.
     * @param player
     */
    public linkPlayer(player: ClientPlayer) : void {
        this.player = player;
    }

    public draw(context: CanvasRenderingContext2D, screenOrigin: Point): void {
        let playerX : number = 0;
        let playerY : number = 0;

        if(this.player !== null) {
            playerX = this.player.getXPosition();
            playerY = this.player.getYPosition();
        }

        context.strokeStyle = this.gridColor;
        context.lineWidth = this.lineThickness;
        this.drawVerticalLines(context, screenOrigin.getX()); //draw vertical grid lines
        this.drawHorizontalLines(context, screenOrigin.getY()); //draw horizontal grid lines
        this.drawBorder(context, playerX, playerY); //draw visible parts of border
    }

    private drawVerticalLines(context: CanvasRenderingContext2D, screenX: number): void {
        //determine horizontal offset of first vertical line
        let offset: number = -(screenX % this.gridSize);

        context.beginPath();
        for(let xPos: number = offset; xPos < this.viewWidth; xPos += this.gridSize) {
            context.moveTo(xPos, 0);
            context.lineTo(xPos, this.viewHeight);
            context.stroke();
        }
        context.closePath();
        //might have to move begin and close path inside loop.
        //might be able to move stroke outside loop before closePath
    }

    private drawHorizontalLines(context: CanvasRenderingContext2D, screenY: number): void {
        //determine vertical offset of first horizontal line
        let offset: number = -(screenY % this.gridSize);

        context.beginPath();
        for(let yPos: number = offset; yPos < this.viewHeight; yPos += this.gridSize) {
            context.moveTo(0, yPos);
            context.lineTo(this.viewWidth, yPos);
            context.stroke();
        }
        context.closePath();
        //might have to move begin and close path inside loop.
        //might be able to move stroke outside loop before closePath
    }

    private drawBorder(context: CanvasRenderingContext2D, playerX: number, playerY: number): void {
        let centerScreen: Point = new Point(this.viewWidth/2, this.viewHeight/2);

        //convert world-space coordinates of border corners into screen-space coordinates
        let upperLeft: Point = new Point(centerScreen.getX() - playerX,
                                         centerScreen.getY() - playerY);
        let upperRight: Point = new Point(centerScreen.getX() + (this.worldWidth - playerX),
                                          centerScreen.getY() - playerY);
        let lowerLeft: Point = new Point(centerScreen.getX() - playerX,
                                         centerScreen.getY() + (this.worldHeight - playerY));
        let lowerRight: Point = new Point(centerScreen.getX() + (this.worldWidth - playerX),
                                          centerScreen.getY() + (this.worldHeight - playerY));

        //for each screen side, determine if part of border can be seen
        context.strokeStyle = this.borderColor;
        context.lineWidth = this.borderThickness;
        context.beginPath();

        //top line
        if(upperLeft.getY() > 0 && upperLeft.getY() < this.viewHeight) {
            context.moveTo(upperLeft.getX(), upperLeft.getY());
            context.lineTo(upperRight.getX(), upperRight.getY());
        }
        //right line
        if(upperRight.getX() > 0 && upperRight.getX() < this.viewWidth) {
            context.moveTo(upperRight.getX(), upperRight.getY());
            context.lineTo(lowerRight.getX(), lowerRight.getY());
        }
        //bottom line
        if(lowerLeft.getY() > 0 && lowerLeft.getY() < this.viewHeight) {
            context.moveTo(lowerRight.getX(), lowerRight.getY());
            context.lineTo(lowerLeft.getX(), lowerLeft.getY());
        }
        //left line
        if(upperLeft.getX() > 0 && upperLeft.getX() < this.viewWidth) {
            context.moveTo(lowerLeft.getX(), lowerLeft.getY());
            context.lineTo(upperLeft.getX(), upperLeft.getY());
        }

        context.stroke();
        context.closePath();
    }
}