
class LagCompensator {
    public latency: number;

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
}