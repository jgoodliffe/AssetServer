package GUI;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.ResourceBundle;

public class LogViewController implements Initializable {
    @FXML
    TextArea consoleOutput;


    public void updateConsole(String text){
        consoleOutput.setText(text);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
