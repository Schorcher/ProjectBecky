import {SimpleRenderer} from "./SimpleRenderer";
import {ClientPlayer} from "./ClientPlayer";
import {Renderer} from "./Renderer";
import {GameBackground} from "./GameBackground";
import {GameEntity} from "./GameEntity";
import {OpponentPlayer} from "./OpponentPlayer";
import Set from "typescript-collections/dist/lib/Set";
import {InputStateChange} from "./networked/InputStateChange";

/**
 * This class is the base class to the game client itself.
 * This class ultimately contains everything.
 */
class GameClient implements GameEntity {
    //keep these the same as the server. Might have server send message to client with these values in future
    private worldWidth: number = 10000;
    private worldHeight: number = 10000;

    private canvas: HTMLCanvasElement;
    private connection: WebSocket;
    private player: ClientPlayer;
    private opponents: Set<OpponentPlayer>;
    private renderer: Renderer;
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
        this.canvas = canvas;
        this.connection = connection;
        this.username = username;
        this.authenticationString = authenticationString;
        this.init();
    }

    public getParentEntity(): GameEntity {
        return null; //GameClient has no parent. It IS the container.
    }

    public getChildEntities(): Set<GameEntity> {
        let entities: Set<GameEntity> = new Set();
        entities.add(this.player);
        for(let i: number = 0; i < this.opponents.size(); i++) {
            entities.add(this.opponents[i]);
        }
        return entities;
    }

    private frameStart: number;
    public run(): void {
        this.frameStart = this.currentTimeMillis();
        this.execGameFrame();
    }

    private execGameFrame(): void {
        let frameEnd: number = this.currentTimeMillis();
        let elapsedTime: number = frameEnd - this.frameStart;
        this.frameStart = frameEnd;

        this.update(elapsedTime);
        this.draw();

        //30 fps is 33 milliseconds per frame
        //if frame took less than 34 millis to complete then waitout the remaining time
        let waitTime: number = 34 - elapsedTime;

        if(waitTime < 0) {
            waitTime = 0;
        }

        //wait out the remainder to limit frame rate to 30 fps
        setTimeout(this.execGameFrame, waitTime);
    }

    private update(elapsedTime: number): void {
        //update current player
        this.player.update(elapsedTime);

        //update opponent player
        for(let i: number = 0; i < this.opponents.size(); i++) {
            this.opponents[i].update(elapsedTime);
        }
    }

    private draw(): void {
        //this might be all that has to be done. Maybe.
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

    private currentTimeMillis(): number {
        return performance.now() || Date.now();
    }

    private initPlayer(): void {
        this.background = new GameBackground(this.canvas.width, this.canvas.height, this.worldWidth, this.worldHeight);
        this.player = new ClientPlayer(this, 0, 0, 0, this.username);
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

    private handleConnectionError(message: string) {
        this.playing = false;
        console.log(message);
    }

    private handleMessageFromServer(message: string): void {
        //TODO: Handle messages from server
    }

    private handleKeyDownInput(event: KeyboardEvent): void {
        let meHandleIt: boolean = false;
        let changeType: string = "w";

        if(event.keyCode == 87) //w key
        {
            meHandleIt = true;
            changeType = "w";
            this.player.setMoveUp(true);
        }
        else if(event.keyCode == 83) //s key
        {
            meHandleIt = true;
            changeType = "s";
            this.player.setMoveDown(true);
        }
        else if(event.keyCode == 65) //a key
        {
            meHandleIt = true;
            changeType = "a";
            this.player.setMoveLeft(true);
        }
        else if(event.keyCode == 68) //d key
        {
            meHandleIt = true;
            changeType = "d";
            this.player.setMoveRight(true);
        }

        if(meHandleIt) {
            //cancel default browser action
            event.preventDefault();

            //generate a state change event
            let stateChange: InputStateChange = new InputStateChange();
            stateChange.inputName = changeType;
            stateChange.flag = true;
            stateChange.username = this.username;
            stateChange.authenticationString = this.authenticationString;

            //send that state change event to the server as a json object
            let json: string = JSON.stringify(stateChange);
            this.connection.send(json);
        }
    }

    private handleKeyUpInput(event: KeyboardEvent): void {
        let meHandleIt: boolean = false;
        let changeType: string = "";

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
            //cancel default browser action
            event.preventDefault();

            //generate a state change event
            let stateChange: InputStateChange = new InputStateChange();
            stateChange.inputName = changeType;
            stateChange.flag = false;
            stateChange.username = this.username;
            stateChange.authenticationString = this.authenticationString;

            //send that state change event to the server as a json object
            let json: string = JSON.stringify(stateChange);
            this.connection.send(json);
        }
    }
}