public class Cell {
    boolean alive = false;
    int xCoordinate;
    int yCoordinate;
    int numberOfNeighbors;

    public Cell() {
    }

    public void setCoordinates(int x, int y) {
        xCoordinate = x;
        yCoordinate = y;
    }

    public int getX() {
        return xCoordinate;
    }
    public int getY() {
        return yCoordinate;
    }

    public void addNeighbour() {
        numberOfNeighbors++;
    }

    public void resetNeighbours() {
        numberOfNeighbors=0;
    }

    public int getNeighbours() {
        return numberOfNeighbors;
    }

    public void setAlive() {
        alive = true;
    }

    public void kill() {
        alive = false;
    }

    public boolean isAlive() {
        return this.alive;
    }
}