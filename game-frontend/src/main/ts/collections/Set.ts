/**
 * A simple set class
 */
class Set<T> {
    public length: number = 0;
    private elements: T[] = [];

    public add(element: T): void {
        this.length++;
        this.elements.push(element);
    }

    public remove(element: T): boolean {
        for(let i: number = 0; i < this.length; i++) {
            if(this.elements[i] == element) {
                length--;
                this.elements.splice(i, 1);
                return true;
            }
        }
        return false;
    }

    public get(index: number): T {
        return this.elements[index];
    }

    public contains(element: T): boolean {
        for(let i = 0; i < this.length; i++) {
            if(element === this.elements[i]) {
                return true;
            }
        }
    }

    public empty(): boolean {
        return (this.elements.length === 0);
    }
}