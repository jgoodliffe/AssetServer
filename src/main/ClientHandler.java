package main;

import LoggingSystem.LoggingSystem;
import dbSystem.DataStore;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable{
    protected Socket s;
    volatile boolean runThread = true;
    private String name;
    private DataStore dataStore;
    private JSONParse jParse;
    private LoggingSystem log;
    boolean isloggedin;
    final DataOutputStream dos;
    final DataInputStream dis;
    final Server svr;

    public ClientHandler(Socket s, String name, DataInputStream dis, DataOutputStream dos, Server svr, DataStore dataStore, JSONParse jParse){
        this.s = s;
        this.dis = dis;
        this.dos = dos;
        this.name = name;
        this.dataStore = dataStore;
        this.jParse = jParse;
        this.isloggedin = true;
        this.svr = svr;
        log = new LoggingSystem("main.ClientHandler-"+Thread.currentThread().toString()+" - "+s.getInetAddress().getHostName());
    }

    public void run(){
        while(runThread){
            try{
                String clientMessage = dis.readUTF();
                System.out.println(s.getInetAddress().getHostName()+" says: "+clientMessage);

                if(!svr.ServerOn) {
                    log.infoMessage("main.Server already stopped");
                    System.out.println("main.Server already stopped");
                }
            } catch (IOException e){
                log.errorMessage("Client disconnected.");
                stop();
                svr.refreshClientList();
            }
        }
        try{
            this.dos.close();
            this.dis.close();
        } catch (IOException e){
            log.errorMessage(e.toString());
        }
    }

    public void stop(){
        runThread = false;
        System.out.println("Stopping communication thread.");
    }
}
