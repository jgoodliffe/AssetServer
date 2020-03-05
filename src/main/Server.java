package main;

import GUI.LogViewController;
import GUI.mainViewController;
import LoggingSystem.LoggingSystem;
import org.eclipse.jetty.servlet.ServletContextHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Main Server Class
 */
public class Server extends Thread {
    private ServerSocket serverSocket;
    private mainViewController viewController;
    private LogViewController consoleViewController;
    private LoggingSystem log;
    private org.eclipse.jetty.server.Server jettyServer;
    private ServletContextHandler jettyContextHandler;
    volatile boolean ServerOn;
    //DataStore mainStore;
    static protected List<ClientHandler> clients;
    private int port;
    private boolean isRunningOnCLI;
    static int i = 0;

    /**
     * Constructor for GUI- Initalises server & Starts thread..
     *
     */
    public Server(int port, mainViewController viewController, LogViewController consoleViewController){
        isRunningOnCLI = false;
        this.viewController = viewController;
        this.port = port;
        this.consoleViewController = consoleViewController;
        viewController.serverStarting();
        log = new LoggingSystem(this.getClass().getCanonicalName(),consoleViewController);
        log.infoMessage("New server instance.");
        try{
            this.serverSocket = new ServerSocket(port);
            //this.http = new PersonServlet();
            log.infoMessage("main.Server successfully initialised.");
            clients = Collections.synchronizedList(new ArrayList<>());
            ServerOn = true;
            log.infoMessage("Connecting to datastore...");
            //mainStore = new DataStore();
            jettyServer = new org.eclipse.jetty.server.Server(8080);
            jettyContextHandler = new ServletContextHandler(jettyServer, "/");
            jettyContextHandler.addServlet(servlets.PersonServlet.class, "/person/*");
            jettyContextHandler.addServlet(servlets.LoginServlet.class, "/login/*");
            jettyContextHandler.addServlet(servlets.HomeServlet.class, "/home/*");
            jettyContextHandler.addServlet(servlets.DashboardServlet.class, "/dashboard/*");
            jettyContextHandler.addServlet(servlets.UserServlet.class, "/user/*");
            jettyContextHandler.addServlet(servlets.JobServlet.class, "/job/*");
            jettyContextHandler.addServlet(servlets.AssetServlet.class, "/asset/*");
            jettyContextHandler.addServlet(servlets.UtilityServlet.class, "/utilities/*");
            jettyContextHandler.addServlet(servlets.EventServlet.class, "/events/*");
            //this.run();
        } catch (IOException e) {
            viewController.showAlert("Error initialising server", e.getMessage());
            viewController.serverStopped();
            log.errorMessage(e.getMessage());
        }
    }


    /**
     * Constructor for no-GUI
     * @param port - Port number
     */
    public Server(int port){
        this.port = port;
        isRunningOnCLI = true;
        if(isRunningOnCLI){
            log = new LoggingSystem(this.getClass().getCanonicalName());
        } else{
            viewController.serverStarting();
            log = new LoggingSystem(this.getClass().getCanonicalName(),consoleViewController);
        }
        log.infoMessage("New server instance.");
        try{
            this.serverSocket = new ServerSocket(port);
            //this.http = new PersonServlet();
            log.infoMessage("main.Server successfully initialised.");
            clients = Collections.synchronizedList(new ArrayList<>());
            ServerOn = true;
            log.infoMessage("Connecting to datastore...");
            jettyServer = new org.eclipse.jetty.server.Server(8080);
            jettyContextHandler = new ServletContextHandler(jettyServer, "/");
            jettyContextHandler.addServlet(servlets.PersonServlet.class, "/person/*");
            jettyContextHandler.addServlet(servlets.LoginServlet.class, "/login/*");
            jettyContextHandler.addServlet(servlets.HomeServlet.class, "/home/*");
            jettyContextHandler.addServlet(servlets.DashboardServlet.class, "/dashboard/*");
            jettyContextHandler.addServlet(servlets.UserServlet.class, "/user/*");
            jettyContextHandler.addServlet(servlets.JobServlet.class, "/job/*");
            jettyContextHandler.addServlet(servlets.AssetServlet.class, "/asset/*");
            jettyContextHandler.addServlet(servlets.UtilityServlet.class, "/utilities/*");
            jettyContextHandler.addServlet(servlets.EventServlet.class, "/events/*");

            //this.run();
        } catch (IOException e) {
            if(!isRunningOnCLI){
                viewController.showAlert("Error initialising server", e.getMessage());
                viewController.serverStopped();
            }
            log.errorMessage(e.getMessage());
        }
    }

    /**
     * Main Server thread.
     */
    public void run(){
        while(ServerOn){
            if(!isRunningOnCLI){
                viewController.disableStart();
                viewController.enableStop();
                viewController.updateStatus("Waiting for clients...");
            }
            try{
                jettyServer.start();
                Socket client = serverSocket.accept();
                log.infoMessage(client.getInetAddress().getHostName() + " Connected");
                DataInputStream dis = new DataInputStream(client.getInputStream());
                DataOutputStream dos = new DataOutputStream(client.getOutputStream());
                JSONParse jp = new JSONParse();
                ClientHandler newClient = new ClientHandler(client, "client "+i, dis, dos, this,null ,jp);
                Thread t = new Thread(newClient);
                clients.add(newClient);
                t.start();
                i++;
                System.out.println("Added new client.");
                new SendMessage(clients, this);
                if(!isRunningOnCLI){
                    viewController.serverStarted();
                }
            } catch (Exception e){
                if(e instanceof SocketException){
                    log.infoMessage("Server socket closed: \n"+e.getMessage());
                } else{
                    log.errorMessage("Exception in server occurred: \n"+e.getMessage());
                }
            }
        }
    }

    public boolean isServerOn(){
        return ServerOn;
    }

    public void stopServer(){
        log.infoMessage("SERVER STOPPING....");
        ServerOn = false;
        if(!isRunningOnCLI){
            viewController.serverStopping();
            viewController.updateStatus("Stopping server..");
            viewController.disableStop();
        }
        for(int i=0; i< clients.size(); i++){
            if(clients.get(i)==null){
                System.out.println("got a null client..");
            } else{
                clients.get(i).stop();
                clients.remove(i);
            }
        }
        try{
            log.infoMessage("Closing Server Socket...");
            serverSocket.close();
            if(!isRunningOnCLI){
                viewController.enableStart();
                viewController.serverStopped();
            }
            log.infoMessage("Stopped server.");
        } catch (IOException e){
            log.errorMessage(e.toString());
            if(!isRunningOnCLI){
                viewController.enableStart();
                viewController.serverStopped();
            }
            log.errorMessage("Error stopping server!");
        }
    }

    public void refreshClientList(){
        for(int i=0; i<clients.size(); i++){
            if(clients.get(i).s.isClosed() | clients.get(i).s==null){
                clients.remove(i);
            }
        }
    }
}
