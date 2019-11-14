package GUI;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import main.*;
import java.net.URL;
import java.util.ResourceBundle;

public class mainViewController implements Initializable {
    private Server server;
    private Thread serverThread;
    private int port;
    @FXML
    Button btn_start;
    @FXML
    TextField portNumber;
    @FXML
    Button btn_stop;
    @FXML
    Circle statusOrb;
    @FXML
    Circle portStatusOrb;
    @FXML
    Label statusLabel;
    @FXML
    LogViewController logViewController;
    @FXML
    TextArea logView;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Server server = new Server();
        logViewController = new LogViewController();
        port = Integer.parseInt(portNumber.getPromptText());
        statusOrb.setFill(Color.CRIMSON);
        portStatusOrb.setFill(Color.CRIMSON);
        btn_start.setDisable(false);
        btn_stop.setDisable(true);
    }


    

    public void updateColour(Color color){
        statusOrb.setFill(color);
        if(statusOrb.getFill().equals(Color.CRIMSON)){
            statusLabel.setText("Starting Server");
        } else if(statusOrb.getFill().equals(Color.GOLD)){
            statusLabel.setText("Server Stopped");
        } else if(statusOrb.getFill().equals(Color.LIMEGREEN)){
            statusLabel.setText("Server Running");
        }
    }

    public void updateStatus(String status){
        statusLabel.setText(status);
    }

    public void serverStarting(){
        updateColour(Color.GOLD);
    }

    public void serverStopping(){
        updateColour(Color.GOLD);
    }

    public void serverStarted(){
        updateColour(Color.LIMEGREEN);
    }

    public void serverStopped(){
        updateColour(Color.CRIMSON);
    }

    public void disableStart(){
        btn_start.setDisable(true);
    }

    public void disableStop(){
        btn_stop.setDisable(true);
    }

    public void enableStart(){
        btn_start.setDisable(false);
    }

    public void enableStop(){
        btn_stop.setDisable(false);
    }

    private Boolean serverRunning(){
        return false;
    }

    @FXML
    public void stopClicked(MouseEvent mouseEvent) {
        if(server.isServerOn()){
            server.stopServer();
        }
    }

    @FXML
    public void startClicked(MouseEvent mouseEvent) {
        try{
            logViewController.updateConsole("hello");
            port = Integer.parseInt(portNumber.getText());
            server = new Server(port,this);
            serverThread = new Thread(server);
            serverThread.start();
        } catch(NumberFormatException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Port Number Invalid");
            alert.setHeaderText("Invalid Port Number Entered!");
            alert.setContentText("Please enter a valid port number (integer)");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    alert.close();
                    return;
                }
            });
        }
    }
}
