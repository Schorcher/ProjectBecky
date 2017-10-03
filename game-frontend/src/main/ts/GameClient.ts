///<reference path="./GameEntity.ts"/>
///<reference path="./ClientPlayer.ts"/>
///<reference path="./SimpleRenderer.ts"/>
///<reference path="./GameBackground.ts"/>
///<reference path="./collections/Set.ts"/>
///<reference path="./OpponentPlayer.ts"/>
///<reference path="./networked/ServerPlayerUpdate.ts"/>
///<reference path="./networked/InputStateChange.ts"/>
///<reference path="./networked/PlayerListChange.ts"/>
///<reference path="./Bullet.ts"/>

/**
 * This class is the base class to the game client itself.
 * This class ultimately contains everything.
 */
class GameClient implements GameEntity {
    //keep these the same as the server. Might have server send message to client with these values in future
    private worldWidth: number = 4000;
    private worldHeight: number = 4000;

    private canvas: HTMLCanvasElement;
    private connection: WebSocket;
    private player: ClientPlayer;
    private opponents: Set<OpponentPlayer> = new Set<OpponentPlayer>();
    private bullets: Set<Bullet> = new Set<Bullet>();
    private renderer: SimpleRenderer;
    private playing: boolean = true;
    private username: string;
    private authenticationString: string;
    private background: GameBackground;

    /**
     * Creates a new GameClient instance.
     * @param canvas A reference to the canvas element to render to.
     * @param connection An established connection to the server.
     * @param Username of the player
     */
    constructor(canvas: HTMLCanvasElement, connection: WebSocket, username: string, authenticationString: string) {
        canvas.width = window.innerWidth;
        canvas.height = window.innerHeight;
        this.canvas = canvas;
        this.connection = connection;
        this.username = username;
        this.authenticationString = authenticationString;
        this.background = new GameBackground(this.canvas.width, this.canvas.height, 2000, 2000);
        this.init();
    }

    public getParentEntity = (): GameEntity  => {
        return null; //GameClient has no parent. It IS the container.
    }

    public getChildEntities = (): Set<GameEntity> => {
        let entities: Set<GameEntity> = new Set<GameEntity>();
        entities.add(this.player);
        for (let i: number = 0; i < this.opponents.length; i++) {
            entities.add(this.opponents[i]);
        }
        return entities;
    }

    public setInitialPlayers = (initialPlayers: InitialPlayerList): void => {
        let players: ServerPlayerUpdate[] = initialPlayers.players;
        if(!players) {
            return;
        }
        let length: number = players.length;
        for(let i = 0; i < length; i++) {
            let player: ServerPlayerUpdate = players[i];
            let opponent = new OpponentPlayer(this, player.playerName);
            opponent.setPosition(player.posX, player.posY);
            this.renderer.addRenderable(opponent);
            this.opponents.add(opponent);
        }
    }

    public setInitialBullets = (initialBullets: BulletInfo[]): void => {
        let length: number = initialBullets.length;
        for(let i = 0; i < length; i++) {
            let ib: BulletInfo = initialBullets[i];
            let p: Player = null;
            if(ib.bulletOwner === this.player.getUsername()) {
                p = this.player;
            }
            else {
                p = this.getOpponentPlayerByUsername(ib.bulletOwner);
            }

            if(p === null) {
                continue;
            }
            else {
                let b: Bullet = new Bullet(p, new Point(ib.positionX, ib.positionY), new Point(ib.velocityX, ib.velocityY));
                this.renderer.add(b);
                this.bullets.add(b);
            }
        }
    }

    private frameStart: number;
    public run = (): void => {
        this.frameStart = Date.now();
        this.execGameFrame();
    }

    private execGameFrame = (): void => {
        let frameEnd: number = Date.now();
        let elapsedTime: number = frameEnd - this.frameStart;
        this.frameStart = frameEnd;

        this.update(elapsedTime);
        this.draw();

        //30 fps is 33 milliseconds per frame
        //if frame took less than 34 millis to complete then waitout the remaining time
        let waitTime: number = 16.6 - elapsedTime;

        if(waitTime < 0) {
            waitTime = 0;
        }

        //wait out the remainder to limit frame rate to 30 fps
        setTimeout(this.execGameFrame, waitTime);
    }

    private getOpponentPlayerByUsername = (username: string): OpponentPlayer => {
        let length: number = this.opponents.length;
        for(let i = 0; i < length; i++) {
            if(this.opponents.get(i).getUsername() === username) {
                return this.opponents.get(i);
            }
        }
        return null;
    }

    private update = (elapsedTime: number): void => {
        //update current player
        this.player.update(elapsedTime);

        //update opponent player
        for(let i: number = 0; i < this.opponents.length; i++) {
            this.opponents.get(i).update(elapsedTime);
        }
    }

    private draw = (): void => {
        //this might be all that has to be done. Maybe.
        let sx = this.player.getXPosition() - this.canvas.width/2;
        let sy = this.player.getYPosition() - this.canvas.height/2;
        this.renderer.updateScreenOrigin(new Point(sx, sy));
        this.renderer.draw();
    }

    /**
     * All steps to initialize the client game go here
     */
    private init(): void {
        this.initPlayer();
        this.initRenderer();
        this.initSocketListeners()
        this.initInput();
    }

    private initPlayer(): void {
        this.background = new GameBackground(this.canvas.width, this.canvas.height, this.worldWidth, this.worldHeight);
        this.player = new ClientPlayer(this, 0, 0, 0, this.username);
        this.background.linkPlayer(this.player);
    }

    private initInput(): void {
        document.addEventListener("keydown", this.handleKeyDownInput);
        document.addEventListener("keyup", this.handleKeyUpInput);
    }

    private initRenderer(): void {
        this.renderer = new SimpleRenderer(this.canvas.getContext("2d"));
        //reminder, renderer stores elements in a set which acts like an arraylist
        //items will be rendered in the order in which they are added.
        //consequently, the background object needs to be the first thing added as it will then be rendered before anything else
        this.renderer.addRenderable(this.background);
        this.renderer.addRenderable(this.player);
    }

    private initSocketListeners(): void {
        this.connection.onerror = (event: ErrorEvent) => {
            this.handleConnectionError(event.message);
        }

        this.connection.onmessage = (event: MessageEvent) => {
            this.handleMessageFromServer(event.data);
        }
    }

    private handleConnectionError = (message: string) => {
        this.playing = false;
    }

    private handleMessageFromServer(message: string): void {
        let object = null;
        if((object = ServerPlayerUpdate.getValidArrayFromJson(message)) !== null) {
            let updates: ServerPlayerUpdate[] = object as ServerPlayerUpdate[];

            // Update current players
            for(let i = 0; i < updates.length; i++) {
                let serverUpdate: ServerPlayerUpdate = updates[i];

                //is the player update for me
                if(this.player.getUsername() === serverUpdate.playerName) {
                    this.player.setPosition(serverUpdate.posX, serverUpdate.posY);
                }
                else {
                    //the player update is likely for another joined player
                    for(let j = 0; j < this.opponents.length; j++) {
                        let opponent: OpponentPlayer = this.opponents.get(j);
                        if(opponent.getUsername() === serverUpdate.playerName) {
                            opponent.setPosition(serverUpdate.posX, serverUpdate.posY);
                            break;
                        }
                    }
                }
            }
        }
        else if((object = PlayerListChange.getValidObjectFromJson(message)) !== null) {
            let change: PlayerListChange = object as PlayerListChange;
            if(change.joined) {
                //player joined game, so add them to the renderer and opponents list
                let opponent: OpponentPlayer = new OpponentPlayer(this, change.username);
                this.opponents.add(opponent);
                this.renderer.addRenderable(opponent);
            }
            else {
                //player joined game, so remove from renderer and opponents list
                let username: string = change.username;
                for(let i = 0; i < this.opponents.length; i++) {
                    let opponent: OpponentPlayer = this.opponents.get(i);
                    if(opponent.getUsername() === username) {
                        //found the opponent, so remove
                        this.renderer.removeRenderable(opponent);
                        this.opponents.remove(opponent);
                        break;
                    }
                }
            }
        }
        else if((object = BulletInfo.getValidArrayFromJson(message)) !== null) {
            let bulletInfos: BulletInfo[] = object as BulletInfo[];
        }
    }

    private handleKeyDownInput = (event: KeyboardEvent): void => {
        let meHandleIt: boolean = false;
        let changeType: string = "w";

        //cancel default browser action
        event.preventDefault();

        if(event.keyCode == 87) //w key
        {
            if(this.player.getMovingUp()) {
                return;
            }
            meHandleIt = true;
            changeType = "w";
            this.player.setMoveUp(true);
        }
        else if(event.keyCode == 83) //s key
        {
            if(this.player.getMovingDown()) {
                return;
            }
            meHandleIt = true;
            changeType = "s";
            this.player.setMoveDown(true);
        }
        else if(event.keyCode == 65) //a key
        {
            if(this.player.getMovingLeft()) {
                return;
            }
            meHandleIt = true;
            changeType = "a";
            this.player.setMoveLeft(true);
        }
        else if(event.keyCode == 68) //d key
        {
            if(this.player.getMovingRight()) {
                return;
            }
            meHandleIt = true;
            changeType = "d";
            this.player.setMoveRight(true);
        }

        if(meHandleIt) {
            this.player.setDecelerating(false);

            //generate a state change event
            let stateChange: InputStateChange = new InputStateChange();
            stateChange.inputName = changeType;
            stateChange.flag = true;
            stateChange.username = this.username;
            stateChange.authenticationString = this.authenticationString;

            //send that state change event to the server as a json object
            let json: string = "InputStateChange:" + JSON.stringify(stateChange);
            this.connection.send(json);
        }
    }

    private handleKeyUpInput = (event: KeyboardEvent): void => {
        let meHandleIt: boolean = false;
        let changeType: string = "";

        //cancel default browser action
        event.preventDefault();

        if(event.keyCode == 87) //w key
        {
            meHandleIt = true;
            changeType = "w";
            this.player.setMoveUp(false);
        }
        else if(event.keyCode == 83) //s key
        {
            meHandleIt = true;
            changeType = "s";
            this.player.setMoveDown(false);
        }
        else if(event.keyCode == 65) //a key
        {
            meHandleIt = true;
            changeType = "a";
            this.player.setMoveLeft(false);
        }
        else if(event.keyCode == 68) //d key
        {
            meHandleIt = true;
            changeType = "d";
            this.player.setMoveRight(false);
        }

        if(meHandleIt) {
            //generate a state change event
            let stateChange: InputStateChange = new InputStateChange();
            stateChange.inputName = changeType;
            stateChange.flag = false;
            stateChange.username = this.username;
            stateChange.authenticationString = this.authenticationString;

            //send that state change event to the server as a json object
            let json: string = "InputStateChange:" + JSON.stringify(stateChange);
            this.connection.send(json);
        }
    }
}