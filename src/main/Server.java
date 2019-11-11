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

public class Server extends Thread {
    private ServerSocket serverSocket;
    private LoggingSystem log;
    boolean ServerOn;
    DataStore mainStore;
    static protected List<ClientHandler> clients;
    private int port = 10000;
    static int i = 0;

    public Server(){
        log = new LoggingSystem("main.Server");
        System.out.println("New server instance");
        try{
            this.serverSocket = new ServerSocket(port);
            log.infoMessage("main.Server successfully initialised.");
            clients = Collections.synchronizedList(new ArrayList<ClientHandler>());
            ServerOn = true;
            System.out.println("Connecting to datastore...");
            mainStore = new DataStore();
            mainStore.init();
            this.run();
        } catch (IOException e) {
            log.errorMessage(e.getMessage());
            System.out.println("Exception occurred. 1");
        }
    }

    public void run(){
        while(ServerOn){
            try{
                Socket client = serverSocket.accept();
                log.infoMessage(client.getInetAddress().getHostName() + " Connected");
                DataInputStream dis = new DataInputStream(client.getInputStream());
                DataOutputStream dos = new DataOutputStream(client.getOutputStream());
                ClientHandler newClient = new ClientHandler(client, "client "+i, dis, dos, this);
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

    public void refreshClientList(){
        for(int i=0; i<clients.size(); i++){
            if(clients.get(i).s.isClosed() | clients.get(i).s==null){
                clients.remove(i);
            }
        }
    }
}
