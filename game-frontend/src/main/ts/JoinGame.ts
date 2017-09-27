/**
 * This class handles the handshake with the server when the client joins the game
 */
class JoinGame {
    private connection: WebSocket;
    private username: string;
    private authenticationString: string;
    private canvas: HTMLCanvasElement;
    private initialJoinState: InitialServerJoinState;

    constructor(username: string, canvas: HTMLCanvasElement) {
        this.username = username;
        this.canvas = canvas;
    }

    public join(): void {
        this.init();
    }

    public changeUsername(oldName: string, newName: string, auth: string): void {
        console.log('Change username requested. Requested username is: ' + newName)
        let request = new UsernameChangeRequest();
        request.oldUsername = oldName;
        request.newUsername = newName;
        request.authenticationString = auth;
        this.connection.send(JSON.stringify(request));
    }

    /**
     *  Init join game
     */
    private init(): void {
        console.log("Initializing join game...");
        this.initWebSocket();
        this.initSocketListeners();
        console.log("Now listening...");
    }

    private initWebSocket(): void {
        console.log("Creating WebSocket...");
        this.connection = new WebSocket('ws://localhost:3000');

    }

    private initSocketListeners(): void {
        console.log("Creating OnError event listener...");
        this.connection.onopen = (event: Event) => {
            this.handleSuccessfulConnection();
        }
        console.log("Creating OnMessage event listener...");
        this.connection.onmessage = (event: MessageEvent) => {
            this.handleMessage(event.data);
        }
    }

    private handleSuccessfulConnection(): void {
        console.log("Successful connection to server!");
    }

    private handleMessage(message: string): void {
        console.log("Message received from server: " + message);

        // Deserialize the initial state object
        let state: InitialServerJoinState = JSON.parse(message);
        if(state == null || state.authenticationString == null || state.initialUsername == null) {
            return;
        }

        this.changeUsername(state.initialUsername, this.username, state.authenticationString);

        this.initialJoinState = state;
        this.connection.onmessage = (event: MessageEvent) => {
            this.handleUsernameMessage(event.data);
        }
    }

    private handleUsernameMessage(message: string): void {
        console.log("Username message received from server: " + message);

        let username: ServerUsernameRequestStatus = JSON.parse(message);
        if (username == null || username.message == null || username.status == null) {
            console.log("Something is null...")
            return;
        }

        if (username.status === 'failed') {
            this.connection.close();
            //TODO: Display the error string (This is for you to do David)
            console.log("The status failed.. :" + username.message);
            return;
        }

        //create the client and kill the current listener
        //this empty listener is so we don't receive more events
        this.connection.onmessage = (event: MessageEvent) => {};

        let gameClient: GameClient = new GameClient(this.canvas, this.connection, username.message, this.initialJoinState.authenticationString);
        gameClient.run();
        console.log("Game should be running...")
    }


}