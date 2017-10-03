/**
 * This contains the initial state of a player upon joining the server.
 * This includes the initially assigned username, authentication string, and position
 */
class InitialServerJoinState {
    public initialUsername: string;
    public authenticationString: string;
    public initialLocationX: number;
    public initialLocationY: number;

    /**
     * Converts a json string into a valid InitialServerJoinState IF the provided json string
     * is actually a valid InitialServerJoinState json object.
     * Null is returned if the string is not a valid json object of this type.
     * @param {string} json
     * @returns {InitialServerJoinState}
     */
    static getValidJsonObject(json: string): InitialServerJoinState {
        let length: number = json.length;
        //"InitialServerJoinState:" is 23 characters
        let beginning: string = json.substr(0, Math.min(length, 23));
        if(beginning === "InitialServerJoinState:") {
            let jsonStr: string = json.substring(23, length);
            return JSON.parse(jsonStr) as InitialServerJoinState;
        }
        else {
            return null;
        }
    }
}