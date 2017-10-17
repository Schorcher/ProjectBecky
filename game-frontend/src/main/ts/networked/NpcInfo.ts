
class NpcInfo {
    public type: string;
    public npcId: number;
    public positionX: number;
    public positionY: number;
    public angle: number;
    public health: number;
    public state: number;

    static getValidArrayFromJson(json: string): NpcInfo[] {
        let length: number = json.length;

        //"NpcInfo[]:" is 10 characters long
        let beginning: string = json.substr(0, Math.min(length, 10));
        if(beginning === "NpcInfo[]:") {
            let jsonStr: string = json.substring(10, length);
            return JSON.parse(jsonStr) as NpcInfo[];
        }
        else {
            return null;
        }
    }
}