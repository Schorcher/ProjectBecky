///<reference path="./networked/InitialServerJoinState.ts"/>
///<reference path="./networked/UsernameChangeRequest.ts"/>
///<reference path="./networked/ServerUsernameRequestStatus.ts"/>
///<reference path="./GameClient.ts"/>
///<reference path="./networked/InitialPlayerList.ts"/>

/**
 * This class handles the handshake with the server when the client joins the game
 */
class JoinGame {
    private connection: WebSocket;
    private username: string;
    private authenticationString: string;
    private canvas: HTMLCanvasElement;
    private initialJoinState: InitialServerJoinState;
    private initialList: InitialPlayerList = null;
    private usernameStatus: ServerUsernameRequestStatus = null;
    private usernameStatusReceived: boolean = false;
    private initialListReceived: boolean = false;

    constructor(username: string, canvas: HTMLCanvasElement) {
        this.username = username;
        this.canvas = canvas;
    }

    public join(): void {
        this.init();
    }

    public changeUsername(oldName: string, newName: string, auth: string): void {
        let request = new UsernameChangeRequest();
        request.oldUsername = oldName;
        request.newUsername = newName;
        request.authenticationString = auth;
        this.connection.send("UsernameChangeRequest:" + JSON.stringify(request));
    }

    /**
     *  Init join game
     */
    private init(): void {
        this.initWebSocket();
        this.initSocketListeners();
    }

    private initWebSocket(): void {
        this.connection = new WebSocket('ws://localhost:3000');
    }

    private initSocketListeners(): void {
        this.connection.onopen = (event: Event) => {
            this.handleSuccessfulConnection();
        }

        this.connection.onmessage = (event: MessageEvent) => {
            this.handleMessage(event.data);
        }
    }

    private handleSuccessfulConnection(): void {
        console.log("Successful connection to server!");
    }

    private handleMessage(message: string): void {
        // Deserialize the initial state object
        let state: InitialServerJoinState = InitialServerJoinState.getValidJsonObject(message);
        if(state === null) {
            return;
        }

        this.changeUsername(state.initialUsername, this.username, state.authenticationString);

        this.initialJoinState = state;
        this.connection.onmessage = (event: MessageEvent) => {
            this.handleUsernameMessage(event.data);
        }
    }

    private handleUsernameMessage(message: string): void {
        let username: ServerUsernameRequestStatus = ServerUsernameRequestStatus.getValidJsonObject(message);
        if (username === null) {
            let initialPlayers: InitialPlayerList = InitialPlayerList.getValidObjectFromJson(message);
            if(initialPlayers === null) {
                return;
            }
            else {
                this.initialList = initialPlayers;
                this.initialListReceived = true;
            }
        }
        else if (username.status === 'failed') {
            this.resetJoinGame("Username rejected by server.");
            //TODO: Display the error message in the gui
            return;
        }
        else {
            this.usernameStatusReceived = true;
            this.usernameStatus = username;
        }

        //create the client and kill the current listener
        //this empty listener is so we don't receive more events
        if(this.usernameStatusReceived && this.initialListReceived) {
            this.connection.onmessage = (event: MessageEvent) => {};
            let gameClient: GameClient = new GameClient(this.canvas, this.connection, username.message, this.initialJoinState.authenticationString);
            gameClient.setInitialPlayers(this.initialList);
            gameClient.run();
        }
    }

    private resetJoinGame(reason: string): void {
        this.initialList = null;
        this.usernameStatus = null;
        this.usernameStatusReceived = false;
        this.initialListReceived = false;
        if(this.connection.readyState == WebSocket.OPEN || this.connection.readyState == WebSocket.CONNECTING) {
            this.connection.close(1000, reason);
        }
        this.connection = null;
    }
}