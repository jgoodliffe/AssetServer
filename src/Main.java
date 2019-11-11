import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import main.Server;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


public class Main extends Application {
    private Server server;
    private Thread serverThread;
    private Boolean serverRunning = false;

    public static void main(String[] args) {
        launch(args);
    }

    public HBox addHBox(){
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

        return hbox;
    }

    public HBox titleHBox(){
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15,12,15,12));
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

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Asset Manager - Server");
        stage.setMinHeight(600);
        stage.setMinWidth(400);
        stage.setMaxHeight(600);
        stage.setMaxWidth(400);

        BorderPane border = new BorderPane();
        border.setPrefSize(600,400);
        border.setStyle("-fx-padding: 10;"+
                        "-fx-boarder-color: dcdcdc;" +
                "-fx-border-style: solid inside;");
        HBox hbox = addHBox();
        HBox hboxTop = titleHBox();
        border.setCenter(hbox);
        border.setTop(hboxTop);
        //border.setLeft(addVBox());


        Scene scene = new Scene(border);
        stage.setScene(scene);
        stage.show();
    }
}
