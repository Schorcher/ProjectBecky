class HighscoreInfo {
    public players: string[];
    public scores: number[];

    static getValidObjectFromJson(json: string): HighscoreInfo {
        let length: number = json.length;

        //"HighscoreInfo:" is 14 characters long
        let beginning: string = json.substr(0, Math.min(length, 14));
        if(beginning === "HighscoreInfo:") {
            let jsonStr: string = json.substring(14, length);
            return JSON.parse(jsonStr) as HighscoreInfo;
        }
        else {
            return null;
        }
    }
}