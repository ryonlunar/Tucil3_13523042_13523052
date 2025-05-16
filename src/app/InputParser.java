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
            char[][] board = new char[lines.size()][lines.get(0).length()];
            for (int i = 0; i < lines.size(); i++) {
                line = lines.get(i);
                for (int j = 0; j < line.length(); j++) {
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
            for (int i = 0; i < lines.size(); i++) {
                for (int j = 0; j < lines.get(0).length(); j++) {
                    char c = board[i][j];
                    if (c == '.' || c == 'K' || c == ' ')
                        continue;

                    if (!vehicles.containsKey(c)) {
                        Orientation orientation = null;
                        if (j + 1 < lines.get(0).length() && board[i][j + 1] == c || 
                            (j > 0 && board[i][j - 1] == c)) {
                            orientation = Orientation.HORIZONTAL;
                        } else if ((i + 1 < lines.size() && board[i + 1][j] == c) || 
                                   (i > 0 && board[i - 1][j] == c)) {
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
            // print board
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[0].length; j++) {
                    System.out.print(board[i][j]);
                }
                System.out.println();
            }
            if (vehicles.size() - 1 != pieceCount) {
                // print vehicles.size() - 1
                System.out.println("vehicles.size() - 1 = " + (vehicles.size() - 1));
                System.out.println(vehicles);
                System.out.println("pieceCount = " + pieceCount);
                // print pieceCount
                throw new Exception("invalid piece count");
            }

            State initState = new State(vehicles, lines.size(), lines.get(0).length(), board, null, null, 0);
            initState.exitRow = exitRow;
            initState.exitCol = exitCol;
            // print letak primary vehicle
            System.out.println("Posisi Kendaraan P: " + vehicles.get('P').row + "," + vehicles.get('P').col);
            // print letak exit
            System.out.println("Posisi Exit: " + exitRow + "," + exitCol);
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
                System.out.println("DEBUG Parser: Posisi exit terdeteksi: (" + exitRow + "," + exitCol + ")");

                // Setelah mendeteksi kendaraan
                System.out.println("DEBUG Parser: Jumlah kendaraan: " + (vehicles.size() - 1));
                System.out.println("DEBUG Parser: Primary vehicle position: (" +
                        vehicles.get('P').row + "," + vehicles.get('P').col +
                        "), orientation: " + vehicles.get('P').orientation +
                        ", length: " + vehicles.get('P').length);
                return result;
            }
        } catch (Exception e) {
            throw new Exception("error parsing file", e);
        }
    }

    public static void main(String[] args) {
        try {
            Result result = parse("app/t4.txt");
            System.out.println("Initial State:");
            // System.out.println(result.initState.toString());
            System.out.println("Algorithm: " + result.algo);
            System.out.println("Heuristic: " + result.heuristic);
            // if (result.algo.equals("UCS")) {
            // UCS ucs = new UCS(result.initState, result.heuristic);
            // ucs.search();
            // } else if (result.algo.equals("GBFS")) {
            // GBFS gbfs = new GBFS(result.initState, result.heuristic);
            // gbfs.search();
            // } else if (result.algo.equals("A*")) {
            // AStar astar = new AStar(result.initState, result.heuristic);
            // astar.search();
            // }
            if (result.initState.isGoal(result.initState.vehicles.get('P'))) {
                System.out.println("Goal state found");
                System.out.println(result.initState.toString());
            } else {
                // print posisi vehicle p dan posisi exit
                System.out.println("Posisi Kendaraan P: " + result.initState.vehicles.get('P').row + ","
                        + result.initState.vehicles.get('P').col);
                System.out.println("Posisi Exit: " + result.initState.exitRow + "," + result.initState.exitCol);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
