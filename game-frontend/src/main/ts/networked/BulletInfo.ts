/**
 * Networked class carrying bullet info from the server to the client
 */
class BulletInfo {
    public owner: string;
    public state: number;
    public bulletId: number;
    public velocityX: number;
    public velocityY: number;
    public positionX: number;
    public positionY: number;

    static getValidArrayFromJson(json: string): BulletInfo[] {
        let length: number = json.length;

        //"BulletInfo[]:" is 13 characters long
        let beginning: string = json.substr(0, Math.min(length, 13));
        if(beginning === "BulletInfo[]:") {
            let jsonStr: string = json.substring(13, length);
            return JSON.parse(jsonStr) as BulletInfo[];
        }
        else {
            return null;
        }
    }
}