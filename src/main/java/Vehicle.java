package main.java;

public class Vehicle {
    public char id;
    public int row, col;
    public int length;
    public Orientation orientation;
    public boolean isPrimary;


    public Vehicle(char id, int row, int col, Orientation Orientation) {
        this.id = id;
        this.row = row;
        this.col = col;
        this.length = 1;
        this.orientation = Orientation;
        this.isPrimary = (id == 'P');
    }

    public void increaseLength() {
        this.length++;
    }

    // deep copy constructor
    public Vehicle copy() {
        Vehicle other = new Vehicle(this);
        return other;
    }

    public Vehicle(Vehicle other) {
        this.id = other.id;
        this.row = other.row;
        this.col = other.col;
        this.length = other.length;
        this.orientation = other.orientation;
        this.isPrimary = other.isPrimary;
    }

    @Override
    public String toString() {
        return "\033[1;34mVehicle\033[0m{" +
                "\n\tid=" + id +
                ",\n\trow=" + row +
                ",\n\tcol=" + col +
                ",\n\tlength=" + length +
                ",\n\tOrientation=" + orientation +
                ",\n\tisPrimary=" + isPrimary +
                "\n}";
    }
}