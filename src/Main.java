import GUI.mainViewController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {
    private Stage stage;
    private FXMLLoader loader  = new FXMLLoader();
    private mainViewController controller;

    /**
     * Main Method
     * @param args
     */
    public static void main(String[] args) {
        //System.setProperty("apple.laf.useScreenMenuBar", "true");
        //System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Asset Manager - Server");
        launch(args);
    }

    private void handleExit(){
        //Close nicely
        if(!(controller ==null)){
            controller.dispose();
        }
        Platform.exit();
        System.exit(0);
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;

        //Handle closing
        stage.setOnCloseRequest(e->{handleExit();});

        // instructs the javafx system not to exit implicitly when the last application window is shut.
        Platform.setImplicitExit(false);


        try{
            Parent root = loader.load(getClass().getResource("GUI/mainView.fxml"));
            controller = loader.getController();
            Scene scene = new Scene(root, 400,600);
            stage.setScene(scene);
            stage.setTitle("Asset Manager Server");
            stage.setResizable(false);
            stage.show();
        } catch (Exception e){
            System.out.println("Error Loading FXML.");
            e.printStackTrace();
        }
    }


    /**
     * Shows the application stage and ensures that it is brought ot the front of all stages.
     */
    private void showStage() {
        if (stage != null) {
            stage.show();
            stage.toFront();
        }
    }
}
