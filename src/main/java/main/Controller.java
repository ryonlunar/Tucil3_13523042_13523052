package main;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class Controller {

    @FXML
    private TextField inputField;

    @FXML
    private Label outputLabel;

    @FXML
    private Button showButton;

    @FXML
    private void showText() {
        String text = inputField.getText();
        outputLabel.setText("Hello, " + text + "!");
    }
}
