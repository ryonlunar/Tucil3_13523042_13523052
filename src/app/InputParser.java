package app;

import java.io.*;
import java.util.*;

public class InputParser {

    public static class Result {
        public State initState;
        public String algo;
        public String heuristic;
    }

    public static Result parse(String filename) throws Exception {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String[] dim = reader.readLine().split(" ");
            int rows = Integer.parseInt(dim[0]);
            int cols = Integer.parseInt(dim[1]);

            int pieceCount = Integer.parseInt(reader.readLine());

            char[][] board = new char[rows][cols];
            for (int i = 0; i < rows; i++) {
                String line = reader.readLine();
                for (int j = 0; j < cols; j++) {
                    board[i][j] = line.charAt(j);
                }
            }

            Map<Character, Vehicle> vehicles = new HashMap<>();
            int exitRow = -1, exitCol = -1;

            // mendeteksi posisi semua kendaraan
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    char c = board[i][j];
                    if (c == '.' || c == 'K')
                        continue;

                    if (!vehicles.containsKey(c)) {
                        Orientation orientation = null;
                        if (j + 1 < cols && board[i][j + 1] == c) {
                            orientation = Orientation.HORIZONTAL;
                        } else if (i + 1 < rows && board[i + 1][j] == c) {
                            orientation = Orientation.VERTICAL;
                        } else {
                            orientation = Orientation.SINGLE;
                        }
                        vehicles.put(c, new Vehicle(c, i, j, orientation));
                    } else {
                        vehicles.get(c).increaseLength();
                    }
                }
            }
            if (vehicles.size() != pieceCount) {
                throw new Exception("invalid piece count");
            }

            // mendeteksi posisi exit
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (board[i][j] == 'K') {
                        exitRow = i;
                        exitCol = j;
                        break;
                    }
                }
            }

            State initState = new State(vehicles, rows, cols, board, null, null, 0);
            initState.exitRow = exitRow;
            initState.exitCol = exitCol;

            try (// baca algo dari stdin
                Scanner scanner = new Scanner(System.in)) {
                System.out.print("Masukkan algoritma (UCS / GBFS / A*): ");
                String algorithm = scanner.nextLine().toUpperCase();

                // baca heuristik dari stdin
                System.out.print("Masukkan heuristik (Manhattan / Euclidean): ");
                String heuristic = scanner.nextLine().toUpperCase();

                Result result = new Result();
                result.initState = initState;
                result.algo = algorithm;
                result.heuristic = heuristic;
                return result;
            }
        } catch (Exception e) {
            throw new Exception("error parsing file", e);
        }
    }

    public static void main(String[] args) {
        try {
            Result result = parse("app/test_input.txt");
            System.out.println("Initial State:");
            System.out.println(result.initState.toString());
            System.out.println("Algorithm: " + result.algo);
            System.out.println("Heuristic: " + result.heuristic);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

