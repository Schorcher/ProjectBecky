
/**
 * The Downloader class
 */
class Downloader {
    static resourceDirectory = '/resource';
    static resourcesLoaded: number = 0;
    static resourcesTotal: number = 0;
    static allResources;
    static ship;
    static enemyShip;


    static getNumbOfResourcesLoaded(){
        this.resourcesLoaded++;
    }

    static getResources(): void {
        this.allResources = [];

        // Resources and resource data listed and stored here
        this.resourcesTotal++;
        this.ship = document.createElement("img");
        this.ship.src = this.resourceDirectory + "/shipOutline.png";
        this.ship.onload = Downloader.getNumbOfResourcesLoaded();
        this.allResources["ship"] = this.ship

        this.resourcesTotal++;
        this.enemyShip = document.createElement("img");
        this.enemyShip.src = this.resourceDirectory + "/enemyOutline.png";
        this.enemyShip.onload = Downloader.getNumbOfResourcesLoaded();
        this.allResources["enemyShip"] = this.enemyShip;
    }
}