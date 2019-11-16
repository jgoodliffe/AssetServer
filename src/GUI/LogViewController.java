package GUI;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.LogRecord;

public class LogViewController extends java.util.logging.Handler implements Initializable {
    @FXML
    TextArea consoleOutput;


    public void updateConsole(String text){
        consoleOutput.setText(text);
    }

    public TextArea getTextArea(){
        return consoleOutput;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //consoleOutput.setText("Hello world...");
    }

    @Override
    public void publish(LogRecord record) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                StringWriter text = new StringWriter();
                PrintWriter out = new PrintWriter(text);
                out.println(consoleOutput.getText());
                out.printf("[%s] [Thread-%d]: %s.%s -> %s", record.getLevel(),
                        record.getThreadID(), record.getSourceClassName(),
                        record.getSourceMethodName(), record.getMessage());
                consoleOutput.setText(text.toString());
            }
        });
    }

    @Override
    public void flush() {

    }

    @Override
    public void close() throws SecurityException {

    }
}
