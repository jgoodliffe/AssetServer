import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import main.Server;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


public class Main extends Application {
    private Server server;
    private Thread serverThread;
    private Circle statusOrb;
    private Label statusLabel;
    private TextArea consoleOutput;
    private Boolean serverRunning = false;

    /**
     * Main Method
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }

    public void updateColour(Color color){
        statusOrb.setFill(color);
        if(statusOrb.getFill().equals(Color.ORANGE)){
            statusLabel.setText("Starting Server");
        } else if(statusOrb.getFill().equals(Color.GOLD)){
            statusLabel.setText("Server Stopped");
        } else if(statusOrb.getFill().equals(Color.LIMEGREEN)){
            statusLabel.setText("Server Running");
        }
    }

    public void updateConsole(String ud){
        consoleOutput.setText(consoleOutput.getText()+"\n"+ud);
    }

    public HBox buttonBox(){
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15,12,15,12));
        hbox.setSpacing(10);
        hbox.setStyle("-fx-background-color:dcdcdc");

        Button btnStartServer = new Button("Start Server");
        btnStartServer.setPrefSize(100,20);
        btnStartServer.setDisable(false);
        btnStartServer.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(!serverRunning){
                    server = new Server();
                    serverThread = new Thread(server);
                    serverThread.start();
                    serverRunning = true;
                }
            }
        });

        Button btnStopServer = new Button("Stop Server");
        btnStopServer.setPrefSize(100,20);
        btnStopServer.setDisable(false);
        btnStopServer.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(serverRunning){
                    server.stopServer();
                }
            }
        });

        hbox.getChildren().addAll(btnStartServer,btnStopServer);
        hbox.setAlignment(Pos.CENTER);

        return hbox;
    }

    private VBox bottomPortion(){
        VBox vbox = new VBox();
        HBox hb1 = buttonBox();
        HBox hb2 = statusBox();
        vbox.getChildren().addAll(hb1,hb2);
        return vbox;
    }

    private VBox consoleBox(){
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(15,12,15,12));
        vbox.setSpacing(10);
        vbox.setStyle("-fx-background-color:dcdcdc");


        Label cO = new Label();
        cO.setText("Console Output");
        cO.setFont(Font.font("Helvetica",12));
        cO.setAlignment(Pos.BOTTOM_LEFT);

        consoleOutput = new TextArea();
        consoleOutput.setEditable(false);
        consoleOutput.setFont(Font.font("Consolas", 10));

        vbox.getChildren().addAll(cO,consoleOutput);
        return vbox;
    }

    private HBox titleHBox(){
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(10,12,15,12));
        hbox.setSpacing(10);
        hbox.setStyle("-fx-background-color:dcdcdc");

        Label title = new Label();
        title.setText("Asset Management Server");
        title.setFont(Font.font("Helvetica", 30));
        title.setWrapText(true);
        title.setContentDisplay(ContentDisplay.TOP);
        title.setAlignment(Pos.CENTER);
        hbox.getChildren().addAll(title);
        return hbox;
    }

    public HBox statusBox(){
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15,12,15,12));
        hbox.setSpacing(10);
        hbox.setStyle("-fx-background-color:dcdcdc");

        statusLabel = new Label();
        statusLabel.setText("Server Stopped");
        statusLabel.setFont(Font.font("Helvetica", 12));
        statusLabel.setContentDisplay(ContentDisplay.LEFT);
        statusLabel.setAlignment(Pos.BASELINE_LEFT);

        statusOrb = new Circle(7,7,7);
        statusOrb.setFill(Color.CRIMSON);

        hbox.getChildren().addAll(statusLabel,statusOrb);
        return hbox;
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Asset Manager - Server");
        stage.setMinHeight(600);
        stage.setMinWidth(400);
        stage.setMaxHeight(600);
        stage.setMaxWidth(400);

        BorderPane border = new BorderPane();
        border.setPrefSize(600,400);
        VBox bottom = bottomPortion();
        HBox hboxTop = titleHBox();
        border.setBottom(bottom);
        border.setCenter(consoleBox());
        border.setTop(hboxTop);
        //border.setLeft(addVBox());


        Scene scene = new Scene(border);
        stage.setScene(scene);
        stage.show();
    }
}
