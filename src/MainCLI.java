import javafx.scene.paint.Color;
import main.*;

import java.io.IOException;
import java.net.ServerSocket;

public class MainCLI {

    private static Server server;
    private static int port = 40000;
    private static Thread serverThread;

    /**
     * MainCli - Command Line Version without JFX Dependencies.
     * @param args
     */
    public static void main(String[] args){
        if(args.length<0){
            try{
                port = Integer.parseInt(args[0]);
                if(checkPortStatus(port)){
                    launchServer(port);
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid entry - Please ensure you have entered an integer.");
            }
        } else{
            //Default to Specified Port
            launchServer(port);
        }
    }

    /**
     * Checks the port status and updates the status orb...
     * @param port - desired port number..
     */
    private static boolean checkPortStatus(int port) {
        if(port>0 && port<=65535){
            System.out.println("--------------Testing port " + port);
            ServerSocket s = null;
            try {
                s = new ServerSocket(port);

                // If the code makes it this far without an exception it means
                // something is using the port and has responded.
                //System.out.println("--------------Port " + port + " is not available");
                //portStatusOrb.setFill(Color.LIMEGREEN);
                //portStatus.setText("Port "+port+ " is available!");
                return true;
            } catch (IOException | IllegalArgumentException e) {
                //System.out.println("--------------Port " + port + " is available");
                //portStatusOrb.setFill(Color.ORANGE);
                //portStatus.setText("Port "+port+" is unavailable: \n"+e.getMessage() );
                return false;
            } finally {
                if( s != null){
                    try {
                        s.close();
                    } catch (IOException e) {
                        //portStatusOrb.setFill(Color.CRIMSON);
                        throw new RuntimeException("You should handle this error." , e);
                    }
                }
                return false;
            }
        }
        else{
            System.out.println("Port out of range...");
            //portStatusOrb.setFill(Color.CRIMSON);
            //portStatus.setText("Port "+port+" is out of range!");
            return false;
        }
    }

    private static void launchServer(int port){
        server = new Server(port);
        serverThread = new Thread(server);
        serverThread.start();
    }
}
