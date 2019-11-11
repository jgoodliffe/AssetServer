package main;

import LoggingSystem.LoggingSystem;
import dbSystem.DataStore;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Main Server Class
 */
public class Server extends Thread {
    private ServerSocket serverSocket;
    private LoggingSystem log;
    volatile boolean ServerOn;
    DataStore mainStore;
    static protected List<ClientHandler> clients;
    private int port = 20000;
    static int i = 0;

    /**
     * Constructor - Initalises server & Starts thread...
     */
    public Server(){
        log = new LoggingSystem(this.getClass().getCanonicalName());
        log.infoMessage("New server instance.");
        try{
            this.serverSocket = new ServerSocket(port);
            log.infoMessage("main.Server successfully initialised.");
            clients = Collections.synchronizedList(new ArrayList<>());
            ServerOn = true;
            log.infoMessage("Connecting to datastore...");
            mainStore = new DataStore();
            //this.run();
        } catch (IOException e) {
            log.errorMessage(e.getMessage());
            System.out.println("Exception occurred. 1");
        }
    }

    /**
     * Main Server thread.
     */
    public void run(){
        while(ServerOn){
            try{
                Socket client = serverSocket.accept();
                log.infoMessage(client.getInetAddress().getHostName() + " Connected");
                DataInputStream dis = new DataInputStream(client.getInputStream());
                DataOutputStream dos = new DataOutputStream(client.getOutputStream());
                JSONParse jp = new JSONParse();
                ClientHandler newClient = new ClientHandler(client, "client "+i, dis, dos, this, mainStore ,jp);
                Thread t = new Thread(newClient);
                clients.add(newClient);
                t.start();
                i++;
                System.out.println("Added new client.");
                new SendMessage(clients, this);

            } catch (Exception e){
                e.printStackTrace();
                log.errorMessage(e.toString());
            }
        }
    }

    public void stopServer(){
        ServerOn = false;
    }

    public void refreshClientList(){
        for(int i=0; i<clients.size(); i++){
            if(clients.get(i).s.isClosed() | clients.get(i).s==null){
                clients.remove(i);
            }
        }
    }
}
