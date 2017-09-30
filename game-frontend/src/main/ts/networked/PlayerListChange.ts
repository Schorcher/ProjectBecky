/**
 * This message is sent from the server when there is a change in the player list.
 * This can be due to a player leaving or joining.
 */
class PlayerListChange {
    /**
     * The username of the player that joined or left the game.
     */
    public username: string;

    /**
     * If true, a player joined with the above username.
     * If false, a player with the above username left the game.
     */
    public joined: boolean;

    static getValidObjectFromJson(json: string): PlayerListChange {
        let length: number = json.length;
        //"PlayerListChange:" is 23 characters
        let beginning: string = json.substr(0, Math.min(length, 17));
        if(beginning === "PlayerListChange:") {
            let jsonStr: string = json.substring(17, json.length);
            return JSON.parse(jsonStr) as PlayerListChange;
        }
        else {
            return null;
        }
    }
}