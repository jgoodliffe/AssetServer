package GUI;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class LogViewController {
    @FXML
    TextArea consoleOutput;

public void updateConsole(String text){
    consoleOutput.setText(text);
}
}
