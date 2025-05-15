package app;

public class Vehicle {
    public char id;
    public int row, col;
    public int length;
    public boolean isHorizontal;
    public boolean isPrimary;


    public Vehicle(char id, int row, int col, int length, boolean isHorizontal) {
        this.id = id;
        this.row = row;
        this.col = col;
        this.length = length;
        this.isHorizontal = isHorizontal;
        this.isPrimary = (id == 'P');
    }

    public Vehicle(Vehicle other) {
        this.id = other.id;
        this.row = other.row;
        this.col = other.col;
        this.length = other.length;
        this.isHorizontal = other.isHorizontal;
        this.isPrimary = other.isPrimary;
    }

    // deep copy constructor
    public Vehicle copy() {
        return new Vehicle(this.id, this.row, this.col, this.length, this.isHorizontal);
    }

    @Override
    public String toString() {
        return "\033[1;34mVehicle\033[0m{" +
                "\n\tid=" + id +
                ",\n\trow=" + row +
                ",\n\tcol=" + col +
                ",\n\tlength=" + length +
                ",\n\tisHorizontal=" + isHorizontal +
                ",\n\tisPrimary=" + isPrimary +
                "\n}";
    }
}