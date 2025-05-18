package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class State {
    public Map<Character, Vehicle> vehicles; // map of Vehicle
    public int tot_rows;
    public int tot_cols;
    public char[][] board;
    public State parent; // untuk rekonstruksi path dari goal ke start
    public String move; // untuk rekonstruksi path dari goal ke start
    public int cost; // untuk UCS dan A*
    public int exitRow, exitCol;
    public int heuristicCost;
    public String methode;

    public State(Map<Character, Vehicle> Vehicles, int tot_rows, int tot_cols, char[][] board, State parent,
            String move, int cost) {
        this.vehicles = new HashMap<>();
        for (Map.Entry<Character, Vehicle> entry : Vehicles.entrySet()) {
            this.vehicles.put(entry.getKey(), new Vehicle(entry.getValue()));
        }
        this.tot_rows = tot_rows;
        this.tot_cols = tot_cols;
        this.board = new char[tot_rows][tot_cols];
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
        this(other.vehicles, other.tot_rows, other.tot_cols, other.board, other.parent, other.move, other.cost);
        this.exitRow = other.exitRow;
        this.exitCol = other.exitCol;
    }

    public void setMethode(String methode) {
        this.methode = methode;
    }

    // deep copy constructor
    // biar gk shallow copy
    // jadi State yang lama gk ikut berubah
    // deep copy untuk generate successors
    public State copy() {
        Map<Character, Vehicle> newVehicles = new HashMap<>();
        for (Map.Entry<Character, Vehicle> entry : vehicles.entrySet()) {
            newVehicles.put(entry.getKey(), entry.getValue().copy());
        }
        char[][] newBoard = new char[this.tot_rows][this.tot_cols];
        for (int i = 0; i < this.tot_rows; i++) {
            newBoard[i] = Arrays.copyOf(this.board[i], this.tot_cols);
        }
        State newState = new State(newVehicles, this.tot_rows, this.tot_cols, newBoard, this, null, this.cost);
        // copy exit coordinates
        newState.exitRow = this.exitRow;
        newState.exitCol = this.exitCol;
        newState.heuristicCost = this.heuristicCost;
        newState.methode = this.methode;

        return newState;
    }

    /*
     * Agar kita tidak mengunjungi ulang State yang sama, kita harus menyimpan semua
     * State yang pernah dikunjungi dalam Set.
     */

    @Override
    public int hashCode() {
        return vehicles.values().stream()
                .mapToInt(v -> Objects.hash(v.id, v.row, v.col, v.orientation))
                .sum();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof State))
            return false;
        State other = (State) obj;
        return vehicles.entrySet().stream()
                .allMatch(e -> {
                    Vehicle ov = other.vehicles.get(e.getKey());
                    return ov != null &&
                            e.getValue().row == ov.row &&
                            e.getValue().col == ov.col &&
                            e.getValue().orientation == ov.orientation;
                });
    }

    // Di dalam class State.java

    public boolean isGoal(Vehicle primary) {
        if (primary == null)
            return false;
        int pr = primary.row;
        int pc = primary.col;
        int len = primary.length;
        int boardLen = board.length;
        Orientation or = primary.orientation;
        // System.out.println("DEBUG isGoal: Checking primary at (" + pr + "," + pc + ") len=" + len +
        //         " orientation=" + or + " against exit at (" + exitRow + "," + exitCol + ")");
        if (primary.orientation == Orientation.VERTICAL &&
                primary.col == exitCol &&
                primary.row + primary.length == exitRow) {
            return true;
        }
        // Cek orientasi primary vehicle
        if (or == Orientation.HORIZONTAL && exitCol == boardLen) {
            int frontCol = pc + len;
            // System.out.println(
            //         "DEBUG isGoal: Case 1 - Horizontal right edge, result: " + (pr == exitRow && frontCol == exitCol));
            return pr == exitRow && frontCol == exitCol;
        } else if (or == Orientation.HORIZONTAL && exitCol == 0) {
            int backCol = pc - 1;
            // System.out.println(
            //         "DEBUG isGoal: Case 2 - Horizontal left edge, result: " + (pr == exitRow && backCol == exitCol));
            return pr == exitRow && backCol == exitCol;
        } else if (or == Orientation.VERTICAL && exitRow == boardLen) {
            int frontRow = pr + len;
            // System.out.println(
            //         "DEBUG isGoal: Case 3 - Vertical bottom edge, result: " + (pc == exitCol && frontRow == exitRow));
            return pc == exitCol && frontRow == exitRow;
        } else if (or == Orientation.VERTICAL && exitRow == 0) {
            int backRow = pr - 1;
            // System.out.println(
            //         "DEBUG isGoal: Case 4 - Vertical top edge, result: " + (pc == exitCol && backRow == exitRow));
            return pc == exitCol && backRow == exitRow;
        }

        // System.out.println(
        //         "DEBUG isGoal: PERHATIAN - Exit tidak pada tepi papan! exitRow=" + exitRow + ", exitCol=" + exitCol);
        // // print posisi vehicle H
        // System.out.println("Posisi Kendaraan H: " + vehicles.get('H').row + "," + vehicles.get('H').col);
        return false;
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

    // tambahkan validasi bahwa mobil tidak bisa mundur ke tempat yang ada spasi
    // atau yang ada "K"
    // tambahkan validasi juga bahwa tidak akan nabrak ke mobil sampingnya

    public boolean isValidMove(Vehicle vehicle, int row, int col) {
        if (board[row][col] == ' ' || board[row][col] == 'K') {
            return false;
        }

        if (vehicle.orientation == Orientation.HORIZONTAL) {
            if ((col > 0 && board[row][col - 1] != '.' && board[row][col - 1] != vehicle.id) ||
                    (col < tot_cols - 1 && board[row][col + 1] != '.' && board[row][col + 1] != vehicle.id)) {
                return false;
            }
        } else if (vehicle.orientation == Orientation.VERTICAL) {
            if ((row > 0 && board[row - 1][col] != '.' && board[row - 1][col] != vehicle.id) ||
                    (row < tot_rows - 1 && board[row + 1][col] != '.' && board[row + 1][col] != vehicle.id)) {
                return false;
            }
        }

        return true;
    }

    // tambahkan logika move mobil
    public void moveVehicle(Vehicle vehicle, int row, int col) {
        if (isValidMove(vehicle, row, col)) {
            board[vehicle.row][vehicle.col] = '.';
            vehicle.row = row;
            vehicle.col = col;
            board[vehicle.row][vehicle.col] = vehicle.id;
        }
    }

    public List<State> generateSucc() {
        List<State> succ = new ArrayList<>();

        for (Map.Entry<Character, Vehicle> entry : vehicles.entrySet()) {
            Vehicle v = entry.getValue();

                if (v.orientation == Orientation.HORIZONTAL) {
                tryMoveHorizontal(v, succ, -1);
                tryMoveHorizontal(v, succ, 1);
                } else {
                tryMoveVertical(v, succ, -1);
                tryMoveVertical(v, succ, 1);
            }

            if (succ.isEmpty()) {
                // System.out.println("DEBUG generateSucc: PERHATIAN - Tidak ada successor yang dihasilkan!");
            }

                }
        return succ;
    }



    private void tryMoveHorizontal(Vehicle vehicle, List<State> successors, int direction) {
        if (direction < 0) {
            // geser ke kiri le
            int maxSpaces = 0;
            for (int col = vehicle.col - 1; col >= 0; col--) {
                if (board[vehicle.row][col] == '.') {
                    maxSpaces++;
                } else {
                    break; 
                }
            }
            
            // coba semua, masukin ke successor
            for (int spaces = 1; spaces <= maxSpaces; spaces++) {
                int newCol = vehicle.col - spaces;
                
                int backPos = vehicle.col + vehicle.length - 1;
                if (backPos >= tot_cols || board[vehicle.row][backPos] != vehicle.id) {
                    continue; 
                }
                
                State newState = this.copy();
                Vehicle movedVehicle = newState.vehicles.get(vehicle.id);
                
                for (int i = 0; i < vehicle.length; i++) {
                    if (newCol + i < tot_cols) {
                        newState.board[vehicle.row][newCol + i] = vehicle.id;
                    }
                }
                
                for (int i = 0; i < spaces; i++) {
                    newState.board[vehicle.row][backPos - i] = '.';
                }
                
                // Update
                movedVehicle.col = newCol;
                
                newState.parent = this;
                newState.move = String.valueOf(vehicle.id) + " left " + spaces;
                newState.cost = this.cost + 1;
                if (this.methode != null) newState.heuristicCost = newState.getHeuristicCost(this.methode);
                
                successors.add(newState);
            }
        } else {
            // geser ke kanan ,le
            int maxSpaces = 0;
            for (int col = vehicle.col + vehicle.length; col < tot_cols; col++) {
                if (board[vehicle.row][col] == '.') {
                    maxSpaces++;
                } else {
                    break; 
                }
            }
            
            // masukin semua possibility ke successors
            for (int spaces = 1; spaces <= maxSpaces; spaces++) {
                int newCol = vehicle.col + spaces;
                
                State newState = this.copy();
                Vehicle movedVehicle = newState.vehicles.get(vehicle.id);
                
                for (int i = 0; i < vehicle.length; i++) {
                    newState.board[vehicle.row][vehicle.col + i] = '.';
                }
                
                for (int i = 0; i < vehicle.length; i++) {
                    newState.board[vehicle.row][newCol + i] = vehicle.id;
                }
                
                // Update
                movedVehicle.col = newCol;
                
                newState.parent = this;
                newState.move = String.valueOf(vehicle.id) + " right " + spaces;
                newState.cost = this.cost + 1;
                if (this.methode != null) newState.heuristicCost = newState.getHeuristicCost(this.methode);
                
                successors.add(newState);
            }
        }
    }

    private void tryMoveVertical(Vehicle vehicle, List<State> successors, int direction) {
        if (direction < 0) {
            // Geser ke atas
            int maxSpaces = 0;
            for (int row = vehicle.row - 1; row >= 0; row--) {
                if (board[row][vehicle.col] == '.') {
                    maxSpaces++;
                } else {
                    break;
                }
            }
            
            // sama kaya yang horizontal
            for (int spaces = 1; spaces <= maxSpaces; spaces++) {
                int newRow = vehicle.row - spaces;
                
                int bottomRow = vehicle.row + vehicle.length - 1;
                if (bottomRow >= tot_rows || board[bottomRow][vehicle.col] != vehicle.id) {
                    continue;
                }
                
                State newState = this.copy();
                Vehicle movedVehicle = newState.vehicles.get(vehicle.id);
                
                for (int i = 0; i < spaces; i++) {
                    newState.board[newRow + i][vehicle.col] = vehicle.id;
                }
                
                for (int i = 0; i < spaces; i++) {
                    newState.board[bottomRow - i][vehicle.col] = '.';
                }
                
                movedVehicle.row = newRow;
                
                newState.parent = this;
                newState.move = String.valueOf(vehicle.id) + " up " + spaces;
                newState.cost = this.cost + 1; // Each multi-space move counts as 1
                if (this.methode != null) newState.heuristicCost = newState.getHeuristicCost(this.methode);
                
                successors.add(newState);
            }
        } else {
            // geser ka bawah
            int maxSpaces = 0;
            for (int row = vehicle.row + vehicle.length; row < tot_rows; row++) {
                if (board[row][vehicle.col] == '.') {
                    maxSpaces++;
                } else {
                    break;
                }
            }
            
            // Sama
            for (int spaces = 1; spaces <= maxSpaces; spaces++) {
                int newRow = vehicle.row + spaces;
                
                State newState = this.copy();
                Vehicle movedVehicle = newState.vehicles.get(vehicle.id);
                
                for (int i = 0; i < vehicle.length; i++) {
                    newState.board[vehicle.row + i][vehicle.col] = '.';
                }
                
                for (int i = 0; i < vehicle.length; i++) {
                    newState.board[newRow + i][vehicle.col] = vehicle.id;
                }
                
                movedVehicle.row = newRow;
                
                newState.parent = this;
                newState.move = String.valueOf(vehicle.id) + " down " + spaces;
                newState.cost = this.cost + 1; // Each multi-space move counts as 1
                if (this.methode != null) newState.heuristicCost = newState.getHeuristicCost(this.methode);
                
                successors.add(newState);
            }
        }
    }
    public int getHeuristicCost(String methode) {
        // System.out.println("DEBUG getHeuristicCost: metode heuristik = " + methode);
        methode = methode.toLowerCase();
        if (methode.equals("manhattan")) {
            return this.getManhattanDistance();
        } else if (methode.equals("blocked")) {
            // System.out.println("DEBUG getHeuristicCost: blocked vehicles = " + this.getBlockedVehicles());
            return this.getBlockedVehicles();
        } else if (methode.equalsIgnoreCase("combinedMB")) {
            double ceil = exitRow == vehicles.get('P').row ? exitRow : vehicles.get('P').col;
            return (int) Math.ceil((double) this.getManhattanDistance() / ceil) + this.getBlockedVehicles();
        } else if (methode.equals("Chebyshev")) {
            return this.getChebysevDistance();
        }    else if (methode.equalsIgnoreCase("EVOLVED")) {
            return (int)(0.4 * getManhattanDistance() + 
                    0.3 * getBlockedVehicles() + 
                    0.2 * getChebysevDistance() + 
                    0.1 * getCombinedMB());
        }else {
            // System.out.println("DEBUG getHeuristicCost: PERHATIAN - Metode heuristik tidak valid!");
            return -1;
        }
    }

    private int getCombinedMB() {
        return getManhattanDistance() + getBlockedVehicles();
    }
    public int getManhattanDistance() {
        Vehicle primary = vehicles.get('P');
        if (primary == null) return -1;

        int pr = primary.row;
        int pc = primary.col;
        int len = primary.length;
        Orientation or = primary.orientation;

        if (or == Orientation.HORIZONTAL) {
            int pEndCol = pc + len;
            return Math.abs(exitRow - pr) + Math.abs(exitCol - pEndCol);
        } else {
            int pEndRow = pr + len;
            return Math.abs(exitRow - pEndRow) + Math.abs(exitCol - pc);
        }
    }


    private int getBlockedVehicles() {
    Set<Character> blockedVehicles = new HashSet<>();

    Vehicle player = vehicles.get('P');
    if (player == null) return 0;

    // Tidak ada pintu keluar (K)
    if (exitRow == -1 || exitCol == -1) return 0;

    int pr = player.row;
    int pc = player.col;
    int len = player.length;

    if (player.orientation == Orientation.HORIZONTAL) {
        if (pr != exitRow) return 0; // Harus di baris yang sama

        int pStart = pc;
        int pEnd = pc + len - 1;

        if (exitCol < pStart) {
            // Cek ke kiri
            for (int c = exitCol; c < pStart; c++) {
                char ch = board[pr][c];
                if (ch != '.' && ch != 'P' && ch != 'K') {
                    blockedVehicles.add(ch);
                }
            }
        } else if (exitCol > pEnd) {
            // Cek ke kanan
            for (int c = pEnd + 1; c <= exitCol; c++) {
                char ch = board[pr][c];
                if (ch != '.' && ch != 'P' && ch != 'K') {
                    blockedVehicles.add(ch);
                }
            }
        }

    } else if (player.orientation == Orientation.VERTICAL) {
        if (pc != exitCol) return 0; // Harus di kolom yang sama

        int pStart = pr;
        int pEnd = pr + len - 1;

        if (exitRow < pStart) {
            // Cek ke atas
            for (int r = exitRow; r < pStart; r++) {
                char ch = board[r][pc];
                if (ch != '.' && ch != 'P' && ch != 'K') {
                    blockedVehicles.add(ch);
                }
            }
        } else if (exitRow > pEnd) {
            // Cek ke bawah
            for (int r = pEnd + 1; r <= exitRow; r++) {
                char ch = board[r][pc];
                if (ch != '.' && ch != 'P' && ch != 'K') {
                    blockedVehicles.add(ch);
                    }
                }
            }
        }
        return blockedVehicles.size();
    }

    public int getChebysevDistance() {
        Vehicle primary = vehicles.get('P');
        if (primary == null) return -1;

        int pr = primary.row;
        int pc = primary.col;
        int len = primary.length;
        Orientation or = primary.orientation;

        if (or == Orientation.HORIZONTAL) {
            int pEndCol = pc + len - 1;
            return Math.max(Math.abs(exitRow - pr), Math.abs(exitCol - pEndCol));
        } else {
            int pEndRow = pr + len - 1;
            return Math.max(Math.abs(exitRow - pEndRow), Math.abs(exitCol - pc));
        }
    }

    public void printState() {
        System.out.println("Current State:");
        for (int i = 0; i < tot_rows; i++) {
            for (int j = 0; j < tot_cols; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }

}
