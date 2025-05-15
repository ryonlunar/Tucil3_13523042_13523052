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

            List<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }

            // Membuat papan dengan ukuran yang ditentukan
            char[][] board = new char[rows+1][cols+1];
            for (int i = 0; i < rows && i < lines.size(); i++) {
                line = lines.get(i);
                for (int j = 0; j < cols && j < line.length(); j++) {
                    board[i][j] = line.charAt(j);
                }
            }

            // Membuat daftar kendaraan
            Map<Character, Vehicle> vehicles = new HashMap<>();
            int exitRow = -1, exitCol = -1;

            // Mendeteksi posisi exit (K) di seluruh file termasuk di luar batas papan
            for (int i = 0; i < lines.size(); i++) {
                line = lines.get(i);
                for (int j = 0; j < line.length(); j++) {
                    if (line.charAt(j) == 'K') {
                        exitRow = i;
                        exitCol = j;
                        break;
                    }
                }
                if (exitRow != -1)
                    break;
            }
            // mendeteksi posisi semua kendaraan
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    char c = board[i][j];
                    if (c == '.' || c == 'K' || c == ' ')
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
            if (vehicles.size() - 1 != pieceCount) {
                throw new Exception("invalid piece count");
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
            // System.out.println(result.initState.toString());
            System.out.println("Algorithm: " + result.algo);
            System.out.println("Heuristic: " + result.heuristic);
            // if (result.algo.equals("UCS")) {
            //     UCS ucs = new UCS(result.initState, result.heuristic);
            //     ucs.search();
            // } else if (result.algo.equals("GBFS")) {
            //     GBFS gbfs = new GBFS(result.initState, result.heuristic);
            //     gbfs.search();
            // } else if (result.algo.equals("A*")) {
            //     AStar astar = new AStar(result.initState, result.heuristic);
            //     astar.search();
            // }
            if (result.initState.isGoal()){
                System.out.println("Goal state found");
                System.out.println(result.initState.toString());
            }else{
                // print posisi vehicle p dan posisi exit
                System.out.println("Posisi Kendaraan P: " + result.initState.vehicles.get('P').row + "," + result.initState.vehicles.get('P').col);
                System.out.println("Posisi Exit: " + result.initState.exitRow + "," + result.initState.exitCol);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
