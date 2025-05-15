package app;

import java.util.Map;
import java.util.HashMap;

public class State {
    public Map<Character, Vehicle> vehicles; // map of Vehicle
    public int tot_rows;
    public int tot_cols;
    public char[][] board;
    public State parent; // untuk rekonstruksi path dari goal ke start
    public String move; // untuk rekonstruksi path dari goal ke start
    public int cost; // untuk UCS dan A*
    public int exitRow, exitCol;

    public State(Map<Character, Vehicle> Vehicles, int tot_rows, int tot_cols, char[][] board, State parent, String move, int cost) {
        this.vehicles = new HashMap<>();
        for (Map.Entry<Character, Vehicle> entry : Vehicles.entrySet()) {
            this.vehicles.put(entry.getKey(), new Vehicle(entry.getValue()));
        }
        this.tot_rows = tot_rows;
        this.tot_cols = tot_cols;
        this.board = new char [tot_rows][tot_cols];
        for (int i = 0; i < tot_rows; i++) {
            for (int j = 0; j < tot_cols; j++) {
                this.board[i][j] = board[i][j];
            }
        }
        this.parent = parent;
        this.move = move;
        this.cost = cost;
    }

    public State(State other) {
        this(other.vehicles,other.tot_rows, other.tot_cols, other.board, other.parent, other.move, other.cost);
        this.exitRow = other.exitRow;
        this.exitCol = other.exitCol;
    }
    // deep copy constructor
    // biar gk shallow copy
    // jadi State yang lama gk ikut berubah
    public State copy() {
        // deep copy kendaraan
        Map<Character, Vehicle> newVehicles = new HashMap<>();
        for (Map.Entry<Character, Vehicle> entry : vehicles.entrySet()) {
            newVehicles.put(entry.getKey(), entry.getValue().copy());
        }

        char[][] newBoard = new char[this.tot_rows][this.tot_cols];
        for (int i = 0; i < this.tot_rows; i++) {
            for (int j = 0; j < this.tot_cols; j++) {
                newBoard[i][j] = this.board[i][j];
            }
        }

        return new State(newVehicles, this.tot_rows, this.tot_cols, newBoard, this.parent, this.move, this.cost);
    }

    /*
     * Agar kita tidak mengunjungi ulang State yang sama, kita harus menyimpan semua State yang pernah dikunjungi dalam Set.
    */
    @Override
    public int hashCode() {
        StringBuilder sb = new StringBuilder();
                for (Vehicle v : vehicles.values()) {
            sb.append(v.id).append(v.row).append(v.col);
        }
        return sb.toString().hashCode();
    }

    // buat cek duplikat
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof State)) {
            return false;
        }
        State other = (State) obj;
        for (char id : vehicles.keySet()) {
            Vehicle v = vehicles.get(id);
            Vehicle otherV = other.vehicles.get(id);
            if (v.row != otherV.row || v.col != otherV.col || v.orientation != otherV.orientation) {
                return false;
            }
        }
        return true;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("State{");
        sb.append("vehicles=").append(vehicles.toString()).append("\n");
        sb.append("tot_rows=").append(tot_rows).append("\n");
        sb.append("tot_cols=").append(tot_cols).append("\n");
        sb.append("board=\n");
        for (int i = 0; i < tot_rows; i++) {
            for (int j = 0; j < tot_cols; j++) {
                sb.append(String.format("%2c ", board[i][j]));
            }
            sb.append("\n");
        }
        sb.append("parent=").append(parent).append("\n");
        sb.append("move=").append(move).append("\n");
        sb.append("cost=").append(cost).append("}");
        sb.append("exitRow=").append(exitRow).append("\n");
        sb.append("exitCol=").append(exitCol).append("\n");
        return sb.toString();
    }
}

