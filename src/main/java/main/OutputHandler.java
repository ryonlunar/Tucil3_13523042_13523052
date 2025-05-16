package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
// import bufferedimage
import java.awt.image.BufferedImage;
import com.madgag.gif.fmsware.AnimatedGifEncoder;

import static java.lang.System.out;

public class OutputHandler {
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m"; // primary piece
    public static final String GREEN = "\u001B[32m"; // exit
    public static final String BLUE = "\u001B[34m"; // moving piece

    public static void printSolutionPath(State goalState, long startTime, long endTime, int visitedNodesCount) {
        List<State> path = new ArrayList<>();
        State currState = goalState;
        while (currState != null) {
            path.add(currState);
            currState = currState.parent;
        }
        Collections.reverse(path);
        for (State state : path) {
            out.println(state);
        }
        out.println("Banyaknya node yang dikunjungi: " + visitedNodesCount);
        out.printf("Waktu eksekusi: %.4f detik\n\n", (endTime - startTime) / 1000.0);

        // cetak board
        for (int i = 0; i < path.size(); i++){
            State state = path.get(i);
            if (i == 0) {
                out.println("Papan Awal: ");
            } else {
                out.println("Gerakan  " + i + ": " + state.move);
            }
            printBoard(state);
            out.println();
        }
    }

    public static void printBoard(State state) {
        char[][] board = state.board;
        char movedPiece = ' ';
        if (state.move != null && state.move.length() > 0) {
            movedPiece = state.move.charAt(0);
        }

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                char c = board[i][j];
                if (c == 'P') {
                    out.print(RED + c + RESET);
                } else if (c == 'K') {
                    out.print(GREEN + c + RESET);
                } else if (c == movedPiece) {
                    out.print(BLUE + c + RESET);
                } else {
                    out.print(c);
                }
            }
            out.println();
        }
    }

    // Tambahkan metode ini untuk menciptakan dan menyimpan GIF dari rangkaian state
    private void generateSolutionGif(State goalState, String outputFilePath) {
        List<State> path = new ArrayList<>();
        State currState = goalState;
        while (currState != null) {
            path.add(0, currState);
            currState = currState.parent;
        }
        
        // Di sini perlu tambahkan library pihak ketiga untuk membuat GIF
        // Misalnya, bisa menggunakan library seperti gifencoder, JAnimatedGIF, dll.
        // Contoh:
        AnimatedGifEncoder encoder = new AnimatedGifEncoder();
        encoder.start(outputFilePath);
        encoder.setDelay(500); // 0.5 detik per frame
        
        for (State state : path) {
            BufferedImage frame = createImageFromState(state);
            encoder.addFrame(frame);
        }
        encoder.finish();
    }

    private BufferedImage createImageFromState(State state) {
        char[][] board = state.board;
        char movedPiece = ' ';
        if (state.move != null && state.move.length() > 0) {
            movedPiece = state.move.charAt(0);
        }
        // TODO Auto-generated method stub
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                char c = board[i][j];
                if (c == 'P') {
                    out.print(RED + c + RESET);
                } else if (c == 'K') {
                    out.print(GREEN + c + RESET);
                } else if (c == movedPiece) {
                    out.print(BLUE + c + RESET);
                } else {
                    out.print(c);
                }
            }
            out.println();
        }
        return null;
    }
}
