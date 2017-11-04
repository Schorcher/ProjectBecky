///<reference path="./Renderable.ts"/>
///<reference path="./ClientPlayer.ts"/>
///<reference path="./collections/Set.ts"/>
///<reference path="./OpponentPlayer.ts"/>
///<reference path="./Player.ts"/>

/**
 * Created by davidmcfall on 10/3/17.
 */


class GameUI implements Renderable {

    private healthBoarderColor: string = "white";
    private healthBorderThickness: number = 10;
    private healthInnerColor: string = "green";

    private scoreFont: string = "Arial";
    private scoreFontSize: string = "22px";
    private scoreFontColor: string = "yellow";

    private leaderBoardFont: string = "Arial";
    private leaderBoardFontSize: string = "16px";
    private leaderBoardFontColor: string = "white";

    private leaderBoardBoarderColor: string = "white";
    private leaderBoardBoarderThickness: number = 10;

    private viewWidth: number;
    private viewHeight: number;
    private player: ClientPlayer;
    private leaderBoardList: Set<Player>;


    constructor(viewWidth: number, viewHeight: number) {
        this.player = null;
        this.leaderBoardList = null;
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
    }

    /**
     * Links a player to the UI. This is for the drawing of their health bar and score.
     * @param player
     */
    public linkPlayer(player: ClientPlayer): void {
        this.player = player;
    }

    /**
     * Links players to the UI. This is for the drawing of their scores on the leader board.
     * @param leaderBoardList
     */
    public linkLeaderBoardList(leaderBoardList: Set<Player>): void {
        this.leaderBoardList = leaderBoardList;
    }

    /**
     *  This is the public method called to draw the different components of the games ui.
     *
     *  @param context
     *  @param screenOrigin
     */
    public draw(context: CanvasRenderingContext2D, screenOrigin: Point): void {
        // Make sure player is not null before attempting to draw
        if (this.player !== null) {
            // Draws the health bar
            this.drawHealthBar(context);

            // Draws the score
            this.drawScore(context);
        }

        // Make sure list is not null before attempting to draw
        if (this.leaderBoardList !== null) {
            // Make sure the list is not empty before drawing
            if (!this.leaderBoardList.empty()) {
                // Draws the leader board
                this.drawLeaderBoard(context);
            }
        }
    }

    /**
     *  This will draw a leader board in the top left of the screen for the set list of opponents given.
     *
     *  @param context
     */
    private drawLeaderBoard(context: CanvasRenderingContext2D): void {
        for (let i = 0; i < this.leaderBoardList.length; i++) {
            // Draw their score based on their position in the set

        }
    }

    /**
     *  This draws the players personal health bar in the top left of the screen canvas
     *
     *  @param context
     */
    private drawHealthBar(context: CanvasRenderingContext2D): void {
        // Draw border of health bar
        context.strokeStyle = this.healthBoarderColor;
        context.lineWidth = this.healthBorderThickness;
        //context.beginPath();

        // Potentially more complex health bar
        // // Begin drawing
        //
        //
        // context.stroke();
        // context.closePath();

        // Draw the internals of the health bar

        // Basic Health Bar
        context.fillRect(0, 0, (this.player.getHealth() / 100) * 140, 25);
    }

    /**
     *  This draws the players score in the top center of the screen canvas.
     *
     *  @param context
     */
    private drawScore(context: CanvasRenderingContext2D): void {
        // Set the location of the score to the top center of the screen canvas
        // Draw the score at the location
        context.font = this.scoreFontSize + " " + this.scoreFont;
        context.fillStyle = this.scoreFontColor;
        context.fillText(this.player.getScore().toString(), (context.canvas.width / 2), (context.canvas.height - 26));
    }
}