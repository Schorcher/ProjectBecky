///<reference path="./GameEntity.ts"/>
///<reference path="./ClientPlayer.ts"/>
///<reference path="./SimpleRenderer.ts"/>
///<reference path="./GameBackground.ts"/>
///<reference path="./collections/Set.ts"/>
///<reference path="./OpponentPlayer.ts"/>
///<reference path="./networked/ServerPlayerUpdate.ts"/>
///<reference path="./networked/ClientInputStateUpdate.ts"/>
///<reference path="./networked/PlayerListChange.ts"/>
///<reference path="./networked/InitialPlayerList.ts"/>
///<reference path="./networked/PointsUpdate.ts"/>
///<reference path="./networked/PlayerHealthMessage.ts"/>
///<reference path="./Bullet.ts"/>
///<reference path="./GameUI.ts"/>

/**
 * This class is the base class to the game client itself.
 * This class ultimately contains everything.
 */
class GameClient implements GameEntity {
    static FRAMES_PER_SECOND: number = 60;
    static TIME_PER_FRAME: number = 1000.0 / GameClient.FRAMES_PER_SECOND;

    //keep these the same as the server. Might have server send message to client with these values in future
    private worldWidth: number = 8000;
    private worldHeight: number = 8000;

    private gameUI: GameUI;
    private canvas: HTMLCanvasElement;
    private connection: WebSocket;
    private player: ClientPlayer;
    private opponents: Set<OpponentPlayer> = new Set<OpponentPlayer>();
    private bullets: Set<Bullet> = new Set<Bullet>();
    private npcs: Set<Npc> = new Set<Npc>();
    private renderer: SimpleRenderer;
    private playing: boolean = true;
    private username: string;
    private authenticationString: string;
    private background: GameBackground;
    private numFrames: number = 0;
    private lagCompensator: LagCompensator = new LagCompensator(30);

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
        this.gameUI = new GameUI(this.canvas.width, this.canvas.height);
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
            let p: Player = this.getPlayerByUsername(ib.owner);

            if(p === null) {
                continue;
            }
            else if(this.getBulletEntityById(ib.bulletId) === null){
                let b: Bullet = new Bullet(p, ib.bulletId, new Point(ib.positionX, ib.positionY), new Point(ib.velocityX, ib.velocityY));
                this.renderer.addRenderable(b);
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

        //Once every 4 frames send the current state of the client input to the server
        if(this.numFrames % 4 === 0) {
            this.sendInputState();
        }
        if(this.numFrames % GameClient.FRAMES_PER_SECOND == 0) {//every 60 frames/once per second
            this.connection.send("PING:" + Date.now());
        }
        this.numFrames++;

        //30 fps is 33 milliseconds per frame
        //if frame took less than 34 millis to complete then waitout the remaining time
        let waitTime: number = GameClient.TIME_PER_FRAME - elapsedTime;

        if(waitTime < 0) {
            waitTime = 0;
        }

        //wait out the remainder to limit frame rate to 30 fps
        setTimeout(this.execGameFrame, waitTime);
    }

    private update = (elapsedTime: number): void => {
        //update current player
        this.player.update(elapsedTime);

        //update opponent player
        for(let i: number = 0; i < this.opponents.length; i++) {
            this.opponents.get(i).update(elapsedTime);
        }

        //update bullets
        for(let i: number = 0; i < this.bullets.length; i++) {
            this.bullets.get(i).update(elapsedTime);
        }

        //update npcs
        for(let i: number = 0; i < this.npcs.length; i++) {
            this.npcs.get(i).update(elapsedTime);
        }
    }

    private draw = (): void => {
        //this might be all that has to be done. Maybe.
        let sx = this.player.getXPosition() - this.canvas.width/2;
        let sy = this.player.getYPosition() - this.canvas.height/2;
        this.renderer.updateScreenOrigin(new Point(sx, sy));
        this.renderer.draw();
    }

    private sendInputState = (): void => {
        let state: ClientInputStateUpdate = new ClientInputStateUpdate();
        state.movingDown = this.player.getMovingDown();
        state.movingUp = this.player.getMovingUp();
        state.movingLeft = this.player.getMovingLeft();
        state.movingRight = this.player.getMovingRight();
        state.angle = this.player.getAngle();
        state.shooting = this.player.isShooting();
        state.username = this.player.getUsername();
        state.authString = this.authenticationString;

        this.connection.send("ClientInputStateUpdate:" + JSON.stringify(state));
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
        this.gameUI = new GameUI(this.canvas.width, this.canvas.height);
        this.gameUI.linkPlayer(this.player);
    }

    private initInput(): void {
        document.addEventListener("keydown", this.handleKeyDownInput);
        document.addEventListener("keyup", this.handleKeyUpInput);
        document.addEventListener("mouseup", this.handleMouseUp);
        document.addEventListener("mousedown", this.handleMouseDown);
        document.addEventListener("mousemove", this.handleMouseMove);
    }

    private initRenderer(): void {
        this.renderer = new SimpleRenderer(this.canvas.getContext("2d"));
        //reminder, renderer stores elements in a set which acts like an arraylist
        //items will be rendered in the order in which they are added.
        //consequently, the background object needs to be the first thing added as it will then be rendered before anything else
        this.renderer.addRenderable(this.background);
        this.renderer.addRenderable(this.player);
        this.renderer.addRenderable(this.gameUI);
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
        //TODO: Display join screen and a connection error string
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
                    this.lagCompensator.compensateClientPlayer(this.player, serverUpdate);
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
        if((object = BulletInfo.getValidArrayFromJson(message)) !== null) {
            let bulletInfos: BulletInfo[] = object as BulletInfo[];
            let length: number = bulletInfos.length;

            for(let i = 0; i < length; i++) {
                let bulletInfo: BulletInfo = bulletInfos[i];
                if(bulletInfo.state === 0) {//new bullet
                    let p: Player = this.getPlayerByUsername(bulletInfo.owner);
                    if(p === null) {
                        continue;
                    }
                    let position: Point = new Point(bulletInfo.positionX, bulletInfo.positionY);
                    let velocity: Point = new Point(bulletInfo.velocityX, bulletInfo.velocityY);
                    let bullet: Bullet = new Bullet(p, bulletInfo.bulletId, position, velocity);
                    if(p !== this.player) {
                        bullet.setFillColor("#ff543c");
                    }
                    this.bullets.add(bullet);
                    this.renderer.addRenderable(bullet);
                    this.lagCompensator.compensateBullet(bullet, bulletInfo);
                }
                else if(bulletInfo.state === 1) { //updated bullet
                    let bullet: Bullet = this.getBulletEntityById(bulletInfo.bulletId);
                    if(bullet === null) {
                        return;
                    }
                    this.lagCompensator.compensateBullet(bullet, bulletInfo);
                }
                else if(bulletInfo.state === 2) { //dead bullet
                    let bullet: Bullet = this.getBulletEntityById(bulletInfo.bulletId);
                    if(bullet !== null) {
                        this.renderer.removeRenderable(bullet);
                        this.bullets.remove(bullet);
                    }
                }
            }
        }
        else if((object = NpcInfo.getValidArrayFromJson(message)) !== null) {
            let npcInfos: NpcInfo[] = object as NpcInfo[];
            let length: number = npcInfos.length;

            for(let i = 0; i < length; i++) {
                let npcInfo: NpcInfo = npcInfos[i];
                if(npcInfo.state === Npc.STATE_NEW_NPC) {
                    let n: Npc = this.getNpcById(npcInfo.npcId);
                    if(n !== null) {
                        this.lagCompensator.compensateNpc(n, npcInfo);
                    }
                    else {
                        this.spawnNpc(npcInfo);
                    }
                }
                else if(npcInfo.state === Npc.STATE_UPDATE_NPC) {
                    let npc: Npc = this.getNpcById(npcInfo.npcId);
                    if(npc !== null) {
                        this.lagCompensator.compensateNpc(npc, npcInfo);
                    }
                    else {
                        this.spawnNpc(npcInfo);
                    }
                }
                else if(npcInfo.state === Npc.STATE_DEAD_NPC) {
                    let npc: Npc = this.getNpcById(npcInfo.npcId);
                    if(npc !== null) {
                        this.renderer.removeRenderable(npc);
                        this.npcs.remove(npc);
                    }
                }
            }
        }
        else if((object = PlayerListChange.getValidObjectFromJson(message)) !== null) {
            //It should be noted that the server only sends this message when a client disconnects from the server
            //or when the server force closes a connection
            let change: PlayerListChange = object as PlayerListChange;
            if(change.joined) {
                //player joined game, so add them to the renderer and opponents list
                let opponent: OpponentPlayer = new OpponentPlayer(this, change.username);
                this.opponents.add(opponent);
                this.renderer.addRenderable(opponent);
            }
            else {
                //player left game, so remove from renderer and opponents list
                let username: string = change.username;
                let player: Player = this.getPlayerByUsername(username);
                if(player instanceof ClientPlayer) {
                    this.connection.close(1000, "Disconnected");
                    this.resetGamePage("Disconnected from server.");
                }
                else {
                    let opponent: OpponentPlayer = player as OpponentPlayer;
                    this.renderer.removeRenderable(opponent);
                    this.opponents.remove(opponent);
                }
            }
        }
        else if(message.substring(0, 5) === "PING:") {
            let time: number = parseInt(message.substring(5));
            let latency: number = Math.floor((Date.now() - time) / 2);
            if(latency < this.lagCompensator.latency) {
                this.lagCompensator.latency = Math.max(latency, this.lagCompensator.latency - 5);
            }
            else {
                this.lagCompensator.latency = Math.min(latency, this.lagCompensator.latency + 5);
            }
        }
        else if((object = PointsUpdate.getValidObjectFromJson(message)) !== null) {
            let points: PointsUpdate = object as PointsUpdate;
            if(this.player.getUsername() === points.username) {
                this.player.setScore(points.numPoints);
                console.log(points.numPoints);
            }
        }
        else if((object = PlayerHealthMessage.getValidObjectFromJson(message)) !== null) {
            let health: PlayerHealthMessage = object as PlayerHealthMessage;
            if (this.player.getUsername() === health.username) {
                this.player.setHealth(health.health);
            }

            if (health.health < 1) {
                this.connection.close(1000, "Player died.");
                this.resetGamePage("Killed by " + health.affectedBy + ". You had " + this.player.getScore() + " points.");
            }
        }
    }

    private resetGamePage = (message: string) => {
        //TODO: David, reset the game and web page or reload it or something. Hopefully display message to the username input screen
        //in the meantime here is something to refresh the page
        alert(message);
        window.location.reload(true);
    }

    private handleKeyDownInput = (event: KeyboardEvent): void => {
        //cancel default browser action
        event.preventDefault();

        if(event.keyCode == 87) { //w key
            this.player.setMoveUp(true);
        }
        else if(event.keyCode == 83) { //s key
            this.player.setMoveDown(true);
        }
        else if(event.keyCode == 65) { //a key
            this.player.setMoveLeft(true);
        }
        else if(event.keyCode == 68) { //d key
            this.player.setMoveRight(true);
        }
    }

    private handleKeyUpInput = (event: KeyboardEvent): void => {
        //cancel default browser action
        event.preventDefault();

        if(event.keyCode == 87) { //w key
            this.player.setMoveUp(false);
        }
        else if(event.keyCode == 83) { //s key
            this.player.setMoveDown(false);
        }
        else if(event.keyCode == 65) { //a key
            this.player.setMoveLeft(false);
        }
        else if(event.keyCode == 68) { //d key
            this.player.setMoveRight(false);
        }
    }

    private handleMouseDown = (event: MouseEvent): void => {
        if(event.button === 0) {
            this.player.setShooting(true);
        }
    }

    private handleMouseUp = (event: MouseEvent): void => {
        if(event.button === 0) {
            this.player.setShooting(false);
        }
    }

    private handleMouseMove = (event: MouseEvent): void => {
        let deltaX: number = event.clientX - this.canvas.width/2;
        let deltaY: number = event.clientY - this.canvas.height/2;
        let angle: number = Math.atan2(deltaY, deltaX);
        this.player.setAngle(angle);
    }

    private getNpcById = (npcId: number): Npc => {
        let length: number = this.npcs.length;
        for(let i = 0; i < length; i++) {
            if(this.npcs.get(i).getNpcId() === npcId) {
                return this.npcs.get(i);
            }
        }
        return null;
    }

    private getPlayerByUsername = (username: string): Player => {
        if(this.player.getUsername() === username) {
            return this.player;
        }

        let length: number = this.opponents.length;
        for(let i = 0; i < length; i++) {
            if(this.opponents.get(i).getUsername() === username) {
                return this.opponents.get(i);
            }
        }
        return null;
    }

    private getBulletEntityById = (id: number): Bullet => {
        let length: number = this.bullets.length;
        for(let i = 0; i < length; i++) {
            if(this.bullets.get(i).getId() === id) {
                return this.bullets.get(i);
            }
        }

        return null;
    }

    private spawnNpc = (npcInfo: NpcInfo): void => {
        let npc: Npc = null;
        if(npcInfo.type === "VirusNpc") {
            npc = new VirusNpc(this, npcInfo.npcId);
        }
        else if(npcInfo.type === "InfectedNpc") {
            npc = new InfectedNpc(this, npcInfo.npcId);
        }

        this.lagCompensator.compensateNpc(npc, npcInfo);
        this.renderer.addRenderable(npc);
        this.npcs.add(npc);
    }

    public getXPosition():number { return 0; }
    public getYPosition():number { return 0; }
    public setPosition(x: number, y:number) {}
}