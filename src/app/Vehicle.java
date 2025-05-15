package app;

public class Vehicle {
    public char id;
    public int row, col;
    public int length;
    public boolean isHorizontal;

    public Vehicle(char id, int row, int col, int length, boolean isHorizontal) {
        this.id = id;
        this.row = row;
        this.col = col;
        this.length = length;
        this.isHorizontal = isHorizontal;
    }

    // deep copy constructor
    public Vehicle copy() {
        return new Vehicle(this.id, this.row, this.col, this.length, this.isHorizontal);
    }
}