/**
 * Contains an array of ServerPlayerUpdate objects.
 * This is the initial list of players and their locations.
 */
class InitialPlayerList {
    public players: ServerPlayerUpdate[];

    static getValidObjectFromJson(json: string): InitialPlayerList {
        let length: number = json.length;
        //"PlayerListChange:" is 23 characters
        let beginning: string = json.substr(0, Math.min(length, 18));
        if(beginning === "InitialPlayerList:") {
            let jsonStr: string = json.substring(18, json.length);
            return JSON.parse(jsonStr) as InitialPlayerList;
        }
        else {
            return null;
        }
    }
}