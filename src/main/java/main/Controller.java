package main;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.io.FileInputStream;

public class Controller {

    @FXML
    private TextField inputField;

    @FXML
    private Label outputLabel;

    @FXML
    private Button showButton;

    // Menambahkan elemen baru
    @FXML
    private TextField filePathField;
    @FXML
    private Button browseButton;
    @FXML
    private RadioButton ucsRadio, gbfsRadio, astarRadio;
    @FXML
    private RadioButton manhattanRadio, blockedRadio;
    @FXML
    private HBox heuristicBox;
    @FXML
    private Button runButton;
    @FXML
    private TextArea outputArea;
    @FXML
    private ImageView animationView;

    private List<WritableImage> animationFrames;
    private int currentFrame = 0;
    private Timeline animationTimeline;

    @FXML
    private Button pauseButton;
    private boolean isPaused = false;

    @FXML RadioButton idaRadio;
    // Menambahkan metode baru
    @FXML
    private void initialize() {
        // Mengatur listener untuk radio button algoritma
        astarRadio.selectedProperty().addListener((obs, oldVal, newVal) -> {
            heuristicBox.setVisible(newVal || gbfsRadio.isSelected());
        });

        gbfsRadio.selectedProperty().addListener((obs, oldVal, newVal) -> {
            heuristicBox.setVisible(newVal || astarRadio.isSelected());
        });

        idaRadio.selectedProperty().addListener((obs, oldVal, newVal) -> {
            heuristicBox.setVisible(newVal || astarRadio.isSelected() || gbfsRadio.isSelected());
        });
    }

    @FXML
    private void browseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Input File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        File selectedFile = fileChooser.showOpenDialog(filePathField.getScene().getWindow());
        if (selectedFile != null) {
            filePathField.setText(selectedFile.getAbsolutePath());
        }
    }

    @FXML
    private void runAlgorithm() {
        String filePath = filePathField.getText();
        if (filePath == null || filePath.isEmpty()) {
            showAlert("Error", "Please select an input file.");
            return;
        }

        // Mengosongkan hasil sebelumnya
        outputArea.clear();

        // Menjalankan algoritma di background agar UI tetap responsif
        CompletableFuture.runAsync(() -> {
            try {
                // Parse input file
                InputParser.Result result = InputParser.parse(filePath,
                        ucsRadio.isSelected() ? "UCS" : astarRadio.isSelected() ? "A*" : gbfsRadio.isSelected() ? "GBFS" : "IDA*",
                        blockedRadio.isSelected() ? "BLOCKED" : "BLOCKED");

                appendOutput("Running " + result.algo + " algorithm...");

                long startTime = System.currentTimeMillis();
                State goalState = null;
                int visitedNodesCount = 0;

                // Jalankan algoritma yang sesuai
                switch (result.algo) {
                    case "UCS":
                        UCS ucs = new UCS(result.initState);
                        ucs.search();
                        goalState = ucs.getGoalState();
                        visitedNodesCount = ucs.getVisitedNodesCount();
                        break;
                    case "GBFS":
                        result.initState.methode = result.heuristic;
                        GBFS gbfs = new GBFS(result.initState);
                        gbfs.search();
                        goalState = gbfs.getGoalState();
                        visitedNodesCount = gbfs.getVisitedNodesCount();
                        break;
                    case "A*":
                        result.initState.methode = result.heuristic;
                        AStar astar = new AStar(result.initState);
                        astar.search();
                        goalState = astar.getGoalState();
                        visitedNodesCount = astar.getVisitedNodesCount();
                        break;
                    case "IDA*":
                        result.initState.methode = result.heuristic;
                        IDAStar idaStar = new IDAStar(result.initState);
                        idaStar.search();
                        goalState = idaStar.getGoalState();
                        visitedNodesCount = idaStar.getVisitedNodesCount();
                        break;
                }
                long endTime = System.currentTimeMillis();

                // Simpan status akhir untuk ditampilkan di UI
                final State finalGoalState = goalState;
                final int finalVisitedNodesCount = visitedNodesCount;
                final long finalStartTime = startTime;
                final long finalEndTime = endTime;

                // Update UI di thread utama
                javafx.application.Platform.runLater(() -> {
                    if (finalGoalState != null) {
                        appendOutput("Goal state found!");
                        appendOutput("Nodes visited: " + finalVisitedNodesCount);
                        appendOutput(String.format("Execution time: %.4f seconds",
                                (finalEndTime - finalStartTime) / 1000.0));
                                
                        showRuntime(finalStartTime, finalEndTime);
                        showVisitedNodeCount(finalVisitedNodesCount);
                        // Tampilkan animasi solusi menggunakan GIF
                        createAndDisplaySolutionGif(finalGoalState);
                        // Setelah selesai pencarian dan mendapatkan goalState
                    } else {
                        appendOutput("No solution found.");
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                javafx.application.Platform.runLater(() -> {
                    showAlert("Error", "Error running algorithm: " + e.getMessage());
                });
            }
        });
    }

    private void appendOutput(String text) {
        javafx.application.Platform.runLater(() -> {
            outputArea.appendText(text + "\n");
        });
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void createAndDisplaySolutionGif(State goalState) {
        // Untuk implementasi sementara, kita akan menampilkan solusi sebagai text
        // Pada implementasi akhir, metode ini akan membuat GIF animasi dan
        // menampilkannya

        appendOutput("\n--- Solution Path ---");

        List<State> path = new ArrayList<>();
        State currState = goalState;
        while (currState != null) {
            path.add(0, currState);
            currState = currState.parent;
        }

        for (int i = 0; i < path.size(); i++) {
            State state = path.get(i);
            if (i == 0) {
                appendOutput("\nInitial State:");
            } else {
                appendOutput("\nMove " + i + ": " + state.move);
            }

            // Display board in text format (akan digantikan dengan frame GIF)
            appendOutput(boardToString(state.board));
        }

        // TODO: Implementasi pembuatan GIF dari rangkaian state
        // Contoh integrasi dengan GIF (asumsi ada file GIF solusi):
        // For placeholder visualization, use a simple colored rectangle
        // This avoids the null GIF problem
        try {
            appendOutput("\nCreating animation...");

            // Create frames
            animationFrames = new ArrayList<>();
            for (State state : path) {
                animationFrames.add(createImageFromState(state));
            }

            // Display the first frame
            if (!animationFrames.isEmpty()) {
                animationView.setImage(animationFrames.get(0));
                currentFrame = 0;

                // Stop any existing animation
                if (animationTimeline != null) {
                    animationTimeline.stop();
                }

                // Create new animation timeline
                animationTimeline = new Timeline(
                        new KeyFrame(Duration.millis(500), event -> {
                            currentFrame = (currentFrame + 1) % animationFrames.size();
                            animationView.setImage(animationFrames.get(currentFrame));
                        }));
                animationTimeline.setCycleCount(Timeline.INDEFINITE);
                animationTimeline.play();
                appendOutput("Animation playing");
            }
        } catch (Exception e) {
            e.printStackTrace();
            appendOutput("Failed to create animation: " + e.getMessage());
        }
    }

    private String boardToString(char[][] board) {
        StringBuilder sb = new StringBuilder();
        for (char[] row : board) {
            for (char c : row) {
                sb.append(c).append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    // Ketika GIF sudah dibuat, bisa dimuat dan ditampilkan:
    private void loadAndDisplayGif(String gifPath) {
        try {
            Image image = new Image(new FileInputStream(gifPath));
            animationView.setImage(image);
        } catch (Exception e) {
            e.printStackTrace();
            appendOutput("Failed to load animation: " + e.getMessage());
        }
    }

    private WritableImage createImageFromState(State state) {
        int cellSize = 30;
        int width = state.tot_cols * cellSize;
        int height = state.tot_rows * cellSize;

        // Create a JavaFX Canvas
        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Fill background
        gc.setFill(javafx.scene.paint.Color.WHITE);
        gc.fillRect(0, 0, width, height);

        // Draw grid
        gc.setStroke(javafx.scene.paint.Color.LIGHTGRAY);
        for (int i = 0; i <= state.tot_rows; i++) {
            gc.strokeLine(0, i * cellSize, width, i * cellSize);
        }
        for (int j = 0; j <= state.tot_cols; j++) {
            gc.strokeLine(j * cellSize, 0, j * cellSize, height);
        }

        // Draw cells
        char movedPiece = ' ';
        if (state.move != null && state.move.length() > 0) {
            movedPiece = state.move.charAt(0);
        }

        for (int i = 0; i < state.tot_rows; i++) {
            for (int j = 0; j < state.tot_cols; j++) {
                char c = state.board[i][j];

                if (c == '.') {
                    continue;
                }

                if (c == 'P') {
                    gc.setFill(javafx.scene.paint.Color.RED);
                } else if (c == 'K') {
                    gc.setFill(javafx.scene.paint.Color.GREEN);
                } else if (c == movedPiece) {
                    gc.setFill(javafx.scene.paint.Color.BLUE);
                } else {
                    gc.setFill(javafx.scene.paint.Color.GRAY);
                }

                gc.fillRect(j * cellSize + 1, i * cellSize + 1, cellSize - 2, cellSize - 2);

                gc.setFill(javafx.scene.paint.Color.BLACK);
                gc.setFont(new javafx.scene.text.Font("SansSerif", 16));
                gc.fillText(String.valueOf(c), j * cellSize + cellSize / 2 - 5, i * cellSize + cellSize / 2 + 5);
            }
        }

        // Take a snapshot of the canvas
        WritableImage writableImage = new WritableImage(width, height);
        canvas.snapshot(null, writableImage);

        return writableImage;
    }

    // Add this method to handle the stop button click
    @FXML
    private void stopAnimation() {
        if (animationTimeline != null) {
            animationTimeline.stop();
            appendOutput("Animation stopped");
        }
    }

    // Tambahkan metode handler:
    @FXML
    private void handlePause() {
        if (animationTimeline != null) {
            if (isPaused) {
                animationTimeline.play();
                appendOutput("Animation resumed");
                pauseButton.setText("Pause");
            } else {
                animationTimeline.pause();
                appendOutput("Animation paused");
                pauseButton.setText("Resume");
            }
            isPaused = !isPaused;
        }
    }

    @FXML
    private void handleNewTestCase() {
        // Hentikan animasi
        if (animationTimeline != null) {
            animationTimeline.stop();
            animationTimeline = null;
        }

        // Reset semua input dan state
        filePathField.clear();
        outputArea.clear();
        animationView.setImage(null);
        animationFrames = null;
        currentFrame = 0;

        // Reset pilihan algoritma
        ucsRadio.setSelected(true);
        gbfsRadio.setSelected(false);
        astarRadio.setSelected(false);
        manhattanRadio.setSelected(true);
        blockedRadio.setSelected(false);

        // Reset status tombol
        pauseButton.setText("Pause");
        isPaused = false;

        appendOutput("Ready for new test case");
    }

    private void showRuntime(long startTime, long endTime) {
        String runtimeStr = String.format("Runtime: %.4f detik", (endTime - startTime) / 1000.0);
        if (outputArea.getText().isEmpty()) {
            outputArea.setText(runtimeStr);
        } else {
            outputArea.appendText("\n" + runtimeStr);
        }
    }

    private void showVisitedNodeCount(int count) {
        String countStr = "Visited Nodes: " + count;
        if (outputArea.getText().isEmpty()) {
            outputArea.setText(countStr);
        } else {
            outputArea.appendText("\n" + countStr);
        }
    }

}
