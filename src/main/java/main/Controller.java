package main;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.io.FileInputStream;
import java.io.PrintWriter;

public class Controller {

    @FXML private TextField inputField;

    @FXML private Label outputLabel;

    @FXML private Button showButton;

    // Menambahkan elemen baru
    @FXML private TextField filePathField;
    @FXML private Button browseButton;
    @FXML private RadioButton ucsRadio, gbfsRadio, astarRadio;
    @FXML private RadioButton manhattanRadio, blockedRadio, gaRadio, evolvedRadio;
    @FXML private HBox heuristicBox;
    @FXML private Button runButton;
    @FXML private TextArea outputArea;
    @FXML private ImageView animationView;

    private List<WritableImage> animationFrames;
    private int currentFrame = 0;
    private Timeline animationTimeline;

    @FXML private Button pauseButton;
    private boolean isPaused = false;

    @FXML private RadioButton idaRadio;

    @FXML private RadioButton combinedMBRadio;
    @FXML private RadioButton chebysevRadio;
    @FXML private RadioButton fileInputRadio;
    @FXML private RadioButton directInputRadio;
    @FXML private HBox fileInputSection;
    @FXML private VBox directInputSection;
    @FXML private TextField rowsField;
    @FXML private TextField colsField;
    @FXML private RadioButton horizontalRadio;
    @FXML private RadioButton verticalRadio;
    @FXML private ComboBox<String> lengthCombo;
    @FXML private ComboBox<String> vehicleIdCombo;
    @FXML private Button placeVehicleButton;
    @FXML private Button placeExitButton;
    @FXML private Button clearBoardButton;
    @FXML private GridPane boardGrid;
    @FXML private Button saveToFileButton;
    @FXML private VBox filePreviewSection;

    @FXML private ImageView filePreviewImage;

    @FXML private Button generateBoardButton;
    @FXML private HBox algoSelectionBox;
    // Fields to store board state
    private char[][] directInputBoard;
    private int boardRows;
    private int boardCols;
    private int exitRow = -1;
    private int exitCol = -1;
    private boolean isPlacingVehicle = false;
    private boolean isPlacingExit = false;
    private Map<Character, Vehicle> directInputVehicles = new HashMap<>();

    // Menambahkan metode baru
    @FXML
    private void initialize() {
        // Initialize length options
        lengthCombo.getItems().addAll("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
        lengthCombo.setValue("2");

        // Initialize vehicle ID options
        vehicleIdCombo.getItems().addAll(
                "P (Primary)", "A", "B", "C", "D",
                "E", "F", "G", "H", "I", "J", "L", "M", "N", "O", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z");
        vehicleIdCombo.setValue("P (Primary)");

        // Add listener to input method radio buttons
        fileInputRadio.selectedProperty().addListener((obs, oldVal, newVal) -> {
            fileInputSection.setVisible(newVal);
            fileInputSection.setManaged(newVal);
            filePreviewSection.setVisible(newVal);
            filePreviewSection.setManaged(newVal);
            directInputSection.setVisible(!newVal);
            directInputSection.setManaged(!newVal);

                    // Show algorithm selection and run button for file input
            algoSelectionBox.setVisible(newVal);
            algoSelectionBox.setManaged(newVal);
            updateHeuristicBoxVisibility();
            runButton.setVisible(newVal);
            runButton.setManaged(newVal);
            pauseButton.setVisible(newVal);
            pauseButton.setManaged(newVal);
            animationView.setVisible(newVal);
            animationView.setManaged(newVal);
            outputArea.setVisible(newVal);
            outputArea.setManaged(newVal);
        });

        directInputRadio.selectedProperty().addListener((obs, oldVal, newVal) -> {
            fileInputSection.setVisible(!newVal);
            fileInputSection.setManaged(!newVal);
            filePreviewSection.setVisible(!newVal);
            filePreviewSection.setManaged(!newVal);
            directInputSection.setVisible(newVal);
            directInputSection.setManaged(newVal);
            
            // Hide algorithm selection and run button for direct input
            algoSelectionBox.setVisible(!newVal);
            algoSelectionBox.setManaged(!newVal);
            updateHeuristicBoxVisibility();
            runButton.setVisible(!newVal);
            runButton.setManaged(!newVal);
            pauseButton.setVisible(!newVal);
            pauseButton.setManaged(!newVal);
            outputArea.setVisible(!newVal);
            outputArea.setManaged(!newVal);
            animationView.setVisible(!newVal);
            animationView.setManaged(!newVal);

            if (newVal && directInputBoard == null) {
                // Auto-create a board when switching to direct input
                createBoard();
            }
        });
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

        gaRadio.selectedProperty().addListener((obs, oldVal, newVal) -> {
            heuristicBox.setVisible(newVal || astarRadio.isSelected() || gbfsRadio.isSelected());
        });
    }

    private void updateHeuristicBoxVisibility() {
        boolean algorithmsRequiringHeuristic = astarRadio.isSelected() || gbfsRadio.isSelected() || 
                                            idaRadio.isSelected() || gaRadio.isSelected();
        boolean isFileInputSelected = fileInputRadio.isSelected();
        
        heuristicBox.setVisible(isFileInputSelected && algorithmsRequiringHeuristic);
        heuristicBox.setManaged(isFileInputSelected && algorithmsRequiringHeuristic);
    }

    @FXML
    private void createBoard() {
        try {
            boardRows = Integer.parseInt(rowsField.getText());
            boardCols = Integer.parseInt(colsField.getText());
            if (boardRows < 3 || boardCols < 3) {
                showAlert("Error", "Board size must be at least 3x3");
                return;
            }

            // Clear existing board
            boardGrid.getChildren().clear();
            boardGrid.getRowConstraints().clear();
            boardGrid.getColumnConstraints().clear();

            // Reset board state - includes border cells
            directInputBoard = new char[boardRows + 2][boardCols + 2];
            directInputVehicles.clear();
            exitRow = -1;
            exitCol = -1;
            isPlacingVehicle = false;
            isPlacingExit = false;

            // Fill board with empty cells and border cells
            for (int i = 0; i < boardRows + 2; i++) {
                for (int j = 0; j < boardCols + 2; j++) {
                    Button cell = new Button();
                    cell.setPrefSize(30, 30);

                    // Check if cell is in the border
                    boolean isBorder = (i == 0 || i == boardRows + 1 || j == 0 || j == boardCols + 1);

                    if (isBorder) {
                        // Border cell - can only place exit (K)
                        directInputBoard[i][j] = ' ';
                        cell.setStyle("-fx-background-color: lightgray; -fx-border-color: gray;");
                        cell.setText("");
                    } else {
                        // Regular cell - can place vehicles
                        directInputBoard[i][j] = '.';
                        cell.setStyle("-fx-background-color: white; -fx-border-color: lightgray;");
                        cell.setText(".");
                    }

                    final int row = i;
                    final int col = j;

                    // Set up click handler
                    cell.setOnMouseClicked(event -> handleCellClick(cell, row, col));
                    boardGrid.add(cell, j, i);
                }
            }

            // Set up grid constraints for the board plus border
            for (int i = 0; i < boardCols + 2; i++) {
                ColumnConstraints colConstraint = new ColumnConstraints();
                colConstraint.setPrefWidth(30);
                boardGrid.getColumnConstraints().add(colConstraint);
            }

            for (int i = 0; i < boardRows + 2; i++) {
                RowConstraints rowConstraint = new RowConstraints();
                rowConstraint.setPrefHeight(30);
                boardGrid.getRowConstraints().add(rowConstraint);
            }

        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter valid numbers for rows and columns");
        }
    }

    private void handleCellClick(Button cell, int row, int col) {
        boolean isBorder = (row == 0 || row == boardRows + 1 || col == 0 || col == boardCols + 1);

        if (isPlacingVehicle) {
            if (isBorder) {
                showAlert("Error", "Vehicles cannot be placed on the border");
                return;
            }
            placeVehicleAtPosition(row, col);
        } else if (isPlacingExit) {
            if (!isBorder) {
                showAlert("Error", "Exit must be placed on the border");
                return;
            }
            placeExitAtPosition(row, col);
        }
    }

    @FXML
    private void toggleVehiclePlacement() {
        isPlacingVehicle = !isPlacingVehicle;
        isPlacingExit = false;

        placeVehicleButton.setText(isPlacingVehicle ? "Cancel Vehicle Placement" : "Place Vehicle");
        placeExitButton.setText("Place Exit");
    }

    @FXML
    private void toggleExitPlacement() {
        isPlacingExit = !isPlacingExit;
        isPlacingVehicle = false;

        placeExitButton.setText(isPlacingExit ? "Cancel Exit Placement" : "Place Exit");
        placeVehicleButton.setText("Place Vehicle");
    }

    private void placeVehicleAtPosition(int startRow, int startCol) {
        // Get vehicle details
        String selectedLength = lengthCombo.getValue();
        String selectedVehicle = vehicleIdCombo.getValue();
        boolean isHorizontal = horizontalRadio.isSelected();

        if (selectedLength == null || selectedVehicle == null) {
            showAlert("Error", "Please select vehicle length and ID");
            return;
        }

        int length = Integer.parseInt(selectedLength);
        char vehicleId = selectedVehicle.charAt(0);

        // Check if primary vehicle already exists
        if (vehicleId == 'P' && directInputVehicles.containsKey('P')) {
            showAlert("Error", "Primary vehicle already exists");
            return;
        }

        // Check if the vehicle fits
        if (isHorizontal) {
            if (startCol + length - 1> boardCols) {
                showAlert("Error", "Vehicle doesn't fit horizontally");
                // Debug info
                System.out.println("Debug: startCol=" + startCol + ", length=" + length + ", boardCols=" + boardCols);
                return;
            }

            // Check if path is clear
            for (int i = 0; i < length; i++) {
                if (directInputBoard[startRow][startCol + i] != '.') {
                    showAlert("Error", "Path is not clear");
                    return;
                }
            }

            // Place vehicle
            Vehicle vehicle = new Vehicle(vehicleId, startRow, startCol, Orientation.HORIZONTAL);
            vehicle.length = length;
            vehicle.isPrimary = (vehicleId == 'P');
            directInputVehicles.put(vehicleId, vehicle);

            // Update board
            for (int i = 0; i < length; i++) {
                directInputBoard[startRow][startCol + i] = vehicleId;
                Button cell = getButtonAt(startRow, startCol + i);
                if (cell != null) {
                    cell.setText(String.valueOf(vehicleId));
                    cell.setStyle("-fx-background-color: " + getColorForVehicle(vehicleId) + "; -fx-text-fill: white;");
                }
            }
        } else { // Vertical
            if (startRow + length - 1 > boardRows) {
                showAlert("Error", "Vehicle doesn't fit vertically");
                System.out.println("Debug: startRow=" + startRow + ", length=" + length + ", boardRows=" + boardRows);
                return;
            }

            // Check if path is clear
            for (int i = 0; i < length; i++) {
                if (directInputBoard[startRow + i][startCol] != '.') {
                    showAlert("Error", "Path is not clear");
                    return;
                }
            }

            // Place vehicle
            Vehicle vehicle = new Vehicle(vehicleId, startRow, startCol, Orientation.VERTICAL);
            vehicle.length = length;
            vehicle.isPrimary = (vehicleId == 'P');
            directInputVehicles.put(vehicleId, vehicle);

            // Update board
            for (int i = 0; i < length; i++) {
                directInputBoard[startRow + i][startCol] = vehicleId;
                Button cell = getButtonAt(startRow + i, startCol);
                if (cell != null) {
                    cell.setText(String.valueOf(vehicleId));
                    cell.setStyle("-fx-background-color: " + getColorForVehicle(vehicleId) + "; -fx-text-fill: white;");
                }
            }
        }

        // Reset placement mode
        isPlacingVehicle = false;
        placeVehicleButton.setText("Place Vehicle");

        // Remove the used vehicle ID from combo box if not primary
        vehicleIdCombo.getItems().remove(selectedVehicle);
        if (!vehicleIdCombo.getItems().isEmpty()) {
            vehicleIdCombo.setValue(vehicleIdCombo.getItems().get(0));
        }
    }

    private void placeExitAtPosition(int row, int col) {
        // Remove old exit if exists
        if (exitRow != -1 && exitCol != -1) {
            directInputBoard[exitRow][exitCol] = ' ';
            Button oldExit = getButtonAt(exitRow, exitCol);
            if (oldExit != null) {
                oldExit.setText("");
                oldExit.setStyle("-fx-background-color: lightgray; -fx-border-color: gray;");
            }
        }

        // Place new exit
        directInputBoard[row][col] = 'K';
        Button cell = getButtonAt(row, col);
        if (cell != null) {
            cell.setText("K");
            cell.setStyle("-fx-background-color: green; -fx-text-fill: white;");
        }

        exitRow = row;
        exitCol = col;

        // Reset placement mode
        isPlacingExit = false;
        placeExitButton.setText("Place Exit");
    }

    private Button getButtonAt(int row, int col) {
        for (Node node : boardGrid.getChildren()) {
            if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == col) {
                return (Button) node;
            }
        }
        return null;
    }

    @FXML
    private void clearBoard() {
        createBoard(); // Recreate the board
    }

    @FXML
    private void saveToFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Board");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = fileChooser.showSaveDialog(boardGrid.getScene().getWindow());

        if (file != null) {
            saveBoard(file);
            filePathField.setText(file.getAbsolutePath()); // Set the file path for running the algorithm
        }
    }

    private void saveBoard(File file) {
        try (PrintWriter writer = new PrintWriter(file)) {
            // Ensure K exists
            if (exitRow == -1 || exitCol == -1) {
                showAlert("Error", "Exit (K) must be placed before saving");
                return;
            }

            // Write board dimensions (exclude border)
            writer.println(boardRows + " " + boardCols);

            // Write vehicle count (excluding exit)
            int vehicleCount = 0;
            for (char vehicleId : directInputVehicles.keySet()) {
                // bukan K dan bukan Primary
                if (vehicleId != 'K' && !directInputVehicles.get(vehicleId).isPrimary) {
                    vehicleCount++;
                }
            }
            writer.println(vehicleCount);

            // Determine exit position
            boolean exitOnTop = (exitRow == 0);
            boolean exitOnBottom = (exitRow == boardRows + 1);
            boolean exitOnLeft = (exitCol == 0);
            boolean exitOnRight = (exitCol == boardCols + 1);

            // Write board content with special handling for rows/cols with K
            if (exitOnTop) {
                // Write the top line with K
                StringBuilder line = new StringBuilder();
                for (int j = 1; j <= boardCols; j++) {
                    line.append(j == exitCol ? 'K' : ' ');
                }
                writer.println(line.toString());

                // Write the normal board content
                for (int i = 1; i <= boardRows; i++) {
                    for (int j = 1; j <= boardCols; j++) {
                        writer.print(directInputBoard[i][j]);
                    }
                    writer.println();
                }
            } else if (exitOnBottom) {
                // Write the normal board content
                for (int i = 1; i <= boardRows; i++) {
                    for (int j = 1; j <= boardCols; j++) {
                        writer.print(directInputBoard[i][j]);
                    }
                    writer.println();
                }

                // Write the bottom line with K
                StringBuilder line = new StringBuilder();
                for (int j = 1; j <= boardCols; j++) {
                    line.append(j == exitCol ? 'K' : ' ');
                }
                writer.println(line.toString());
            } else if (exitOnLeft) {
                // Write the normal board content with K and spaces
                for (int i = 1; i <= boardRows; i++) {
                    if (i == exitRow) {
                        writer.print('K');
                    } else {
                        writer.print(' ');
                    }

                    for (int j = 1; j <= boardCols; j++) {
                        writer.print(directInputBoard[i][j]);
                    }
                    writer.println();
                }
            } else if (exitOnRight) {
                // Write the normal board content with K at the end
                for (int i = 1; i <= boardRows; i++) {
                    for (int j = 1; j <= boardCols; j++) {
                        writer.print(directInputBoard[i][j]);
                        if (j == boardCols && i != exitRow) {
                            writer.print(' ');
                        }
                    }
                    if (i == exitRow) {
                        writer.print('K');
                    }

                    writer.println();
                }
            }

            appendOutput("Board saved to: " + file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to save board: " + e.getMessage());
        }
    }

    private static final Map<Character, String> VEHICLE_COLOR_MAP = Map.ofEntries(
            Map.entry('P', "red"),
            Map.entry('A', "blue"),
            Map.entry('B', "green"),
            Map.entry('C', "yellow"),
            Map.entry('D', "purple"),
            Map.entry('E', "orange"),
            Map.entry('F', "brown"),
            Map.entry('G', "pink"),
            Map.entry('H', "cyan"),
            Map.entry('I', "magenta"),
            Map.entry('J', "teal"),
            Map.entry('L', "maroon"),
            Map.entry('M', "olive"),
            Map.entry('N', "navy"),
            Map.entry('O', "violet"),
            Map.entry('Q', "fuchsia"),
            Map.entry('R', "indigo"),
            Map.entry('S', "sienna"),
            Map.entry('T', "khaki"),
            Map.entry('U', "wheat"),
            Map.entry('V', "tan"),
            Map.entry('W', "peachpuff"),
            Map.entry('X', "papayawhip"),
            Map.entry('Y', "blanchedalmond"),
            Map.entry('Z', "linen"));

    private String getColorForVehicle(char id) {
        return VEHICLE_COLOR_MAP.getOrDefault(id, "black");
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
            updateFilePreview(selectedFile.getAbsolutePath());
        }
    }

    @FXML
    private void runAlgorithm() {
        String filePath = fileInputRadio.isSelected() ? filePathField.getText() : "";

        if (filePath == "") {
            showAlert("Error", "Please select an input file.");
            return;
        }
        if (!fileInputRadio.isSelected()) {
            // Direct input is selected
            if (directInputBoard == null) {
                showAlert("Error", "Please create a board first.");
                return;
            }

            // Check if primary vehicle exists
            if (!directInputVehicles.containsKey('P')) {
                showAlert("Error", "Primary vehicle is required.");
                return;
            }

            // Check if exit is placed
            if (exitRow == -1 || exitCol == -1) {
                showAlert("Error", "Exit position is required.");
                return;
            }

            // Save to temporary file
            try {
                File tempDir = getTempDirectory();
                if (tempDir == null) {
                    showAlert("Error", "Failed to create temporary directory.");
                    return;
                }
                
                File tempFile = new File(tempDir, "direct_input_" + System.currentTimeMillis() + ".txt");
                saveBoard(tempFile);
                filePathField.setText(tempFile.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Failed to create temporary file: " + e.getMessage());
                return;
            }
        }

        // Mengosongkan hasil sebelumnya
        outputArea.clear();
        String heuristiCh = blockedRadio.isSelected() ? "BLOCKED"
                : combinedMBRadio.isSelected() ? "combinedMB" : chebysevRadio.isSelected() ? "CHEBYSHEV" : evolvedRadio.isSelected() ? "EVOLVED" : "MANHATTAN";
        // Menjalankan algoritma di background agar UI tetap responsif
        CompletableFuture.runAsync(() -> {
            try {
                // Parse input file
                InputParser.Result result = InputParser.parse(filePath,
                        ucsRadio.isSelected() ? "UCS"
                                : astarRadio.isSelected() ? "A*" : gbfsRadio.isSelected() ? "GBFS" : idaRadio.isSelected() ? "IDA*" : "GA",
                        heuristiCh);

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
                    case "GA":
                        result.initState.methode = result.heuristic;
                        GA ga = new GA(result.initState);
                        GA.HeuristicSolution solution = ga.evolveHeuristic();
                        goalState = solution.solutionState;
                        visitedNodesCount = solution.nodesVisited;
                        // print runtime
                        long endTime = System.currentTimeMillis();
                        System.out.println("Execution time: " + (endTime - startTime) / 1000.0 + " seconds");
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
        // Keep your existing code

        // Reset direct input as well
        boardGrid.getChildren().clear();
        directInputBoard = null;
        directInputVehicles.clear();
        exitRow = -1;
        exitCol = -1;
        isPlacingVehicle = false;
        isPlacingExit = false;
        rowsField.clear();
        colsField.clear();
        // Reset vehicle IDs
        vehicleIdCombo.getItems().clear();
        vehicleIdCombo.getItems().addAll(
                "P (Primary)", "A", "B", "C", "D",
                "E", "F", "G", "H", "I", "J", "L", "M", "N", "O", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z");
        vehicleIdCombo.setValue("P (Primary)");
        horizontalRadio.setSelected(true);
        lengthCombo.setValue("2");
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

    private File getTempDirectory() {
        try {
            // Create directory in the current working directory
            String currentDir = System.getProperty("user.dir");
            File tempDir = new File(currentDir, "temp_boards");
            if (!tempDir.exists()) {
                tempDir.mkdir();
            }
            return tempDir;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    @FXML
    private void generateAndSaveBoard() {
        // Validate board exists
        if (directInputBoard == null) {
            showAlert("Error", "Please create a board first");
            return;
        }
        
        // Check if primary vehicle exists
        if (!directInputVehicles.containsKey('P')) {
            showAlert("Error", "Primary vehicle is required.");
            return;
        }
        
        // Check if exit is placed
        if (exitRow == -1 || exitCol == -1) {
            showAlert("Error", "Exit position is required.");
            return;
        }
        
        // Create and save to temp file
        try {
            File tempDir = getTempDirectory();
            if (tempDir == null) {
                showAlert("Error", "Failed to create temporary directory.");
                return;
            }
            
            File tempFile = new File(tempDir, "direct_input_" + System.currentTimeMillis() + ".txt");
            saveBoard(tempFile);
            filePathField.setText(tempFile.getAbsolutePath());
            // Switch ke File Input
            fileInputRadio.setSelected(true);
            updateFilePreview(tempFile.getAbsolutePath());
            appendOutput("Board generated and ready to run.");
            runAlgorithm();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to generate board file: " + e.getMessage());
        }
    }

    // Tambahkan method baru untuk update preview
    private void updateFilePreview(String filePath) {
        try {
            // Parse the board file menggunakan InputParser yang sudah ada
            InputParser.Result result = InputParser.parse(filePath,
                ucsRadio.isSelected() ? "UCS"
                : astarRadio.isSelected() ? "A*" 
                : gbfsRadio.isSelected() ? "GBFS" 
                : idaRadio.isSelected() ? "IDA*" 
                : "GA",
                blockedRadio.isSelected() ? "BLOCKED"
                : combinedMBRadio.isSelected() ? "combinedMB" 
                : chebysevRadio.isSelected() ? "CHEBYSHEV" 
                : evolvedRadio.isSelected() ? "EVOLVED" 
                : "MANHATTAN");
            
            // Buat preview image menggunakan method createImageFromState yang sudah ada
            // Sesuaikan ukuran ImageView
            int cellSize = 30; // sama seperti di createImageFromState
            int width = result.initState.tot_cols * cellSize;
            int height = result.initState.tot_rows * cellSize;
            filePreviewImage.setFitWidth(width);
            filePreviewImage.setFitHeight(height);
            filePreviewImage.setPreserveRatio(false);
            WritableImage previewImage = createImageFromState(result.initState);
            filePreviewImage.setImage(previewImage);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load preview: " + e.getMessage());
        }
    }
}
