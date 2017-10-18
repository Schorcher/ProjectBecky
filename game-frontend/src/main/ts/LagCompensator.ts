
class LagCompensator {
    static enabled: boolean = true;

    //when a message is received containing the position of an entity,
    //there is likely to be some difference in client position and position
    //from the network. The compensator has three ways to handle things.
    //If the difference in position is <= THRESHHOLD_MAX_IGNORE, the compensator
    //will do absolutely nothing.
    static THRESHOLD_MAX_IGNORE: number = 0.4;
    //If THRESHOLD_MAX_IGNORE <= difference <= THRESHOLD_MAX_ADJUST then
    //use velocity adjustments to correct position within a certain number of frames.
    static THRESHOLD_MAX_ADJUST: number = 200.0;
    //Anything above THRESHOLD_MAX_ADJUST and the object will simply be teleported

    public latency: number = 0;
    private correctionFps: number;

    /**
     * Creates a new lag compensator object which can be used to attempt to correct positioning based on the known
     * connection latency.
     * @param {number} fps The amount of time, specified in number of display frames, it should take for a game entity
     * to be moved from its current position into its predicted position after a server position message is received.
     */
    constructor(fps: number) {
        this.correctionFps = fps;
    }

    public compensateBullet(bullet: Bullet, bulletInfo: BulletInfo): void {
        if(!LagCompensator.enabled) {
            bullet.setPosition(bulletInfo.positionX, bulletInfo.positionY);
            return;
        }

        //multiplier based on latency
        let multiplier: number = this.latency / 1000.0;

        //based on latency determine the correct X/Y position
        bulletInfo.positionX += bullet.getXVelocity() * multiplier;
        bulletInfo.positionY += bullet.getYVelocity() * multiplier;

        //how "off" is the game client
        let delta: number = this.distance(bullet.getXPosition(), bullet.getYPosition(), bulletInfo.positionX, bulletInfo.positionY);
        if(delta > LagCompensator.THRESHOLD_MAX_ADJUST) {
            bullet.setPosition(bulletInfo.positionX, bulletInfo.positionY);
        }
        else {
            //how much time available to correct?
            let millis: number = GameClient.TIME_PER_FRAME * this.correctionFps;

            //determine how much EXTRA distance per millisecond must be covered by the bullet
            //to be in the correct position within 8 game frames
            let deltaX: number = bulletInfo.positionX - bullet.getXPosition();
            let deltaY: number = bulletInfo.positionY - bullet.getYPosition();

            //delta/millis = delta per millisecond
            //delta per millisecond * 1000 = distance per second to add to velocity
            let velocity: Point = new Point(deltaX/millis * 1000, deltaY/millis * 1000);
            bullet.setLagCompensateVelocity(velocity, this.correctionFps);
        }
    }

    public compensateClientPlayer(player: ClientPlayer, playerInfo: ServerPlayerUpdate): void {
        if(!LagCompensator.enabled) {
            player.setPosition(playerInfo.posX, playerInfo.posY);
            player.setAngle(playerInfo.angle);
            return;
        }

        //multiplier based on latency
        let multiplier: number = this.latency / 1000.0;

        //based on latency, adjust the velocity and position vectors
        let velocity: Point = new Point(playerInfo.velX, playerInfo.velY);
        velocity.addX(multiplier * playerInfo.accelX);
        velocity.addY(multiplier * playerInfo.accelY);
        ClientPlayer.capVelocity(velocity); //cap velocity for us
        let position: Point = new Point(playerInfo.posX, playerInfo.posY);
        position.addX(velocity.getX() * multiplier);
        position.addY(velocity.getY() * multiplier);

        //set velocity and acceleration to the player
        player.setAcceleration(playerInfo.accelX, playerInfo.accelY);
        player.setVelocity(velocity.getX(), velocity.getY());

        //how "off" is the client on player position?
        let delta: number = this.distance(player.getXPosition(), player.getYPosition(), position.getX(), position.getY());

        if(delta > LagCompensator.THRESHOLD_MAX_ADJUST) {
            player.setPosition(position.getX(), position.getY());
        }
        else {
            //how much time available to correct
            let millis: number = GameClient.TIME_PER_FRAME * this.correctionFps;

            //how much EXTRA distance must be covered per millisecond to be corrected in time
            let deltaX: number = position.getX() - player.getXPosition();
            let deltaY: number = position.getY() - player.getYPosition();

            //delta/millis = delta per millisecond
            //delta per millisecond * 1000 = distance per second to add to velocity
            let adjustVelocity: Point = new Point(deltaX/millis * 1000, deltaY/millis * 1000);
            player.setLagCompensateVelocity(adjustVelocity, this.correctionFps);
        }
    }

    public compensateNpc(npc: Npc, npcInfo: NpcInfo): void {
        npc.setHealth(npcInfo.health);
        if(!LagCompensator.enabled) {
            npc.setPosition(npcInfo.positionX, npcInfo.positionY);
            npc.setAngle(npcInfo.angle);
            return;
        }

        //multiplier based on latency
        let multiplier: number = this.latency / 1000.0;

        //adjust the velocity and position to account for latency
        let velocity: Point = new Point(npcInfo.velocityX, npcInfo.velocityY);
        velocity.addX(multiplier * npcInfo.accelerationX);
        velocity.addY(multiplier * npcInfo.accelerationY);
        let position: Point = new Point(npcInfo.positionX, npcInfo.positionY);
        position.addX(multiplier * velocity.getX());
        position.addY(multiplier * velocity.getY());

        //how "off" is the client
        let delta: number = this.distance(npc.getXPosition(), npc.getYPosition(), position.getX(), position.getY());

        //no matter what, we always set acceleration and velocity
        npc.setAcceleration(npcInfo.accelerationX, npcInfo.accelerationY);
        npc.setVelocity(npcInfo.velocityX, npcInfo.velocityY);

        //figure out how to handle a difference in values
        if(delta > LagCompensator.THRESHOLD_MAX_ADJUST) {
            npc.setPosition(position.getX(), position.getY());
        }
        else {
            let millis: number = GameClient.TIME_PER_FRAME * this.correctionFps;

            let deltaX: number = position.getX() - npc.getXPosition();
            let deltaY: number = position.getY() - npc.getYPosition();

            let adjustVelocity: Point = new Point(deltaX/millis * 1000, deltaY/millis * 1000);
            npc.setCompensationVelocity(adjustVelocity, this.correctionFps);
        }
    }

    private distance(x1: number, y1: number, x2: number, y2: number): number {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }
}