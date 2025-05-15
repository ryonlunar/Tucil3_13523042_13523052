package app;

import java.util.*;

public class TestState {
    public static void main(String[] args) {
        // 1. Buat kendaraan (contoh 3 mobil)
        Vehicle redCar = new Vehicle('X', 2, 0, Orientation.HORIZONTAL); // mobil merah horizontal
        Vehicle a = new Vehicle('A', 0, 0, Orientation.HORIZONTAL);
        Vehicle b = new Vehicle('B', 0, 2, Orientation.VERTICAL);

        // 2. Masukkan kendaraan ke map
        Map<Character, Vehicle> vehicles = new HashMap<>();
        vehicles.put(redCar.id, redCar);
        vehicles.put(a.id, a);
        vehicles.put(b.id, b);

        // 3. Buat papan 6x6 kosong
        char[][] board = new char[6][6];
        for (int i = 0; i < 6; i++) {
            Arrays.fill(board[i], '.');
        }

        // 4. Tempatkan kendaraan di papan
        for (Vehicle v : vehicles.values()) {
            if (v.orientation == Orientation.HORIZONTAL) {
                for (int i = 0; i < v.length; i++) {
                    board[v.row][v.col + i] = v.id;
                }
            } else {
                for (int i = 0; i < v.length; i++) {
                    board[v.row + i][v.col] = v.id;
                }
            }
        }

        // 5. Buat state
        State state = new State(vehicles, 6, 6, board, null, "Initial", 0);

        // 6. Print papan
        System.out.println("Initial Board State:");
        System.out.println(state.toString());

        // 7. Coba copy state dan cek perbedaan objek
        State copied = state.copy();
        System.out.println("\nCopied Board State:");
        System.out.println(copied.toString());

        // 8. Cek equals
        System.out.println("\nAre states equal? " + state.equals(copied));
        System.out.println("Are state objects the same? " + (state == copied)); // Harus false
    }
}
