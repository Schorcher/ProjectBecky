
/**
 * The Downloader class
 */
class Downloader {
    static resourceDirectory = '~/resource/images';
    static resourcesLoaded = 0;
    static resourcesTotal = 0;
    static allResources;
    static ship;
    static bullet;
    static infected;


    static getNumbOfResourcesLoaded(){
        this.resourcesLoaded++;
    }

    static getResources(): void{
        this.allResources = [];

        // Resources and resource data listed and stored here
        this.resourcesTotal++;
        this.ship = document.createElement("img");
        this.ship.src = this.resourceDirectory + "/ship.png";
        this.ship.onload = Downloader.getNumbOfResourcesLoaded();
        this.allResources["ship"] = this.ship

        this.resourcesTotal++;
        this.bullet = document.createElement("img");
        this.bullet.src = this.resourceDirectory + "/bullet.png";
        this.bullet.onload = Downloader.getNumbOfResourcesLoaded();
        this.allResources["bullet"] = this.bullet;

        this.resourcesTotal++;
        this.infected = document.createElement("img");
        this.infected.src = this.resourceDirectory + "/infected.png";
        this.infected.onload = Downloader.getNumbOfResourcesLoaded();
        this.allResources["infected"] = this.infected;
    }
}