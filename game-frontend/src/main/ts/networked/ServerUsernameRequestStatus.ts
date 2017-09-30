/**
 * A status sent from the server to indicate if a username change request was successful
 */
class ServerUsernameRequestStatus {
    public status: string;
    public message: string;

    static getValidJsonObject(json: string): ServerUsernameRequestStatus {
        let length: number = json.length;
        //"ServerUsernameRequestStatus:" is 28 characters
        let beginning: string = json.substr(0, Math.min(length, 28));
        if(beginning === "ServerUsernameRequestStatus:") {
            let jsonStr: string = json.substring(28, length);
            console.log(jsonStr);
            return JSON.parse(jsonStr) as ServerUsernameRequestStatus;
        }
        else {
            return null;
        }
    }
}