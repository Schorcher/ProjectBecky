/**
 * The message sent from the server when a player dies.
 */
class PlayerHealthMessage {
    public username: string;
    public affectedBy: string;
    public health: number;

    static getValidObjectFromJson(json: string): PlayerHealthMessage {
        let length: number = json.length;

        //"PlayerHealthMessage:" is 20 characters long
        let beginning: string = json.substr(0, Math.min(length, 20));
        if(beginning === "PlayerHealthMessage:") {
            let jsonStr: string = json.substring(20, length);
            return JSON.parse(jsonStr) as PlayerHealthMessage;
        }
        else {
            return null;
        }
    }
}