/**
 * Represents an update about a player from the server.
 */

class ServerPlayerUpdate {
    public posX: number;
    public posY: number;
    public playerName: string;

    //TODO: Phase 2 uncomment the following fields
    //private accelX: number;
    //private accelY: number;
    //private velX: number;
    //private velY: number;

    //TODO: Phase 2 uncomment the following methods
    //public getVelocity(): Point {
    //    return new Point(this.velX, this.velY);
    //}

    //public getAcceleration(): Point {
    //    return new Point(this.accelX, this.accelY);
    //}

    /**
     * Checks if a json string is a valid json array of ServerPlayerUpdate objects.
     * If the json string is a valid json array, then an array of ServerPlayerUpdate objects is returned.
     * Otherwise null is returned.
     * @param {string} json
     * @returns {ServerPlayerUpdate[]}
     */
    static getValidArrayFromJson(json: string): ServerPlayerUpdate[] {
        let length: number = json.length;
        //"ServerPlayerUpdate[]:" is 21 characters
        let beginning: string = json.substr(0, Math.min(length, 21));
        if(beginning === "ServerPlayerUpdate[]:") {
            let jsonStr: string = json.substring(21, length);
            return JSON.parse(jsonStr) as ServerPlayerUpdate[];
        }
        else {
            return null;
        }
    }
}