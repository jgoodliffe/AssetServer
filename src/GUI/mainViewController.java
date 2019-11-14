package GUI;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.PopupWindow;
import main.*;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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

    Tooltip portStatus;
    Tooltip status;

    private static final String SQUARE_BUBBLE =
            "M24 1h-24v16.981h4v5.019l7-5.019h13z";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Server server = new Server();
        port = Integer.parseInt(portNumber.getPromptText());
        statusOrb.setFill(Color.CRIMSON);
        portStatusOrb.setFill(Color.CRIMSON);
        btn_start.setDisable(false);
        btn_stop.setDisable(true);
        status = statusTooltip(new Tooltip("Status of Port"));
        portStatus = statusTooltip(new Tooltip("Status of Server"));
        Tooltip.install(statusOrb, status);
        Tooltip.install(portStatusOrb, portStatus);
        checkPortStatus(port);
    }

    /**
     * Checks the port status and updates the status orb...
     * @param port - desired port number..
     */
    private void checkPortStatus(int port) {
        if(port>0 && port<=65535){
            System.out.println("--------------Testing port " + port);
            ServerSocket s = null;
            try {
                s = new ServerSocket(port);

                // If the code makes it this far without an exception it means
                // something is using the port and has responded.
                //System.out.println("--------------Port " + port + " is not available");
                portStatusOrb.setFill(Color.LIMEGREEN);
                portStatus.setText("Port "+port+ " is available!");
            } catch (IOException | IllegalArgumentException e) {
                //System.out.println("--------------Port " + port + " is available");
                portStatusOrb.setFill(Color.ORANGE);
                portStatus.setText("Port "+port+" is unavailable: \n"+e.getMessage() );
            } finally {
                if( s != null){
                    try {
                        s.close();
                    } catch (IOException e) {
                        portStatusOrb.setFill(Color.CRIMSON);
                        throw new RuntimeException("You should handle this error." , e);
                    }
                }
            }
        }
        else{
            portStatusOrb.setFill(Color.CRIMSON);
            portStatus.setText("Port "+port+" is out of range!");
        }
    }

    private Tooltip statusTooltip(Tooltip tooltip){
        //tooltip.setStyle("-fx-font-size: 11px; -fx-shape: \"" + SQUARE_BUBBLE + "\";");
        tooltip.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_BOTTOM_LEFT);
        return tooltip;
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
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                statusLabel.setText(status);
            }
        });
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

    public void showAlert(String message1, String message2){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(message1);
        alert.setContentText(message2);
        alert.showAndWait().ifPresent(rs -> {
            if (rs == ButtonType.OK) {
                alert.close();
                return;
            }
        });
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
            //logViewController.updateConsole("hello");
            System.out.println(portNumber.getText());
            if(portNumber.getText().equals("")){
                server = new Server(port,this);
                serverThread = new Thread(server);
                serverThread.start();
                return;
            } else{
                port = Integer.parseInt(portNumber.getText());
                server = new Server(port,this);
                serverThread = new Thread(server);
                serverThread.start();
            }
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

    public void onEnter(javafx.event.ActionEvent actionEvent) {
        int pNum;
        try{
            pNum = Integer.parseInt(portNumber.getText());
            checkPortStatus(pNum);
        } catch(NumberFormatException e){
            showAlert("Error - Port Number", e.getMessage());
        }
    }

    //Check port
    public void handleOnKeyPressed(KeyEvent keyEvent) {
        if(keyEvent.getCode().isDigitKey() && portNumber.isFocused()){
            int pNum;
            try{
                pNum = Integer.parseInt(portNumber.getText());
                checkPortStatus(pNum);
            } catch(NumberFormatException e){
            }
        }
    }
}
