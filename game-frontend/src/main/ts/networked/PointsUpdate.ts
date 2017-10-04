

class PointsUpdate {
    public username: string;
    public numPoints: number;

    static getValidObjectFromJson(json: string): PointsUpdate {
        let length: number = json.length;

        //"PointsUpdate:" is 13 characters long
        let beginning: string = json.substr(0, Math.min(length, 13));
        if(beginning === "PointsUpdate:") {
            let jsonStr: string = json.substring(13, length);
            return JSON.parse(jsonStr) as PointsUpdate;
        }
        else {
            return null;
        }
    }
}