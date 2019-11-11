package main;

import java.io.*;
import java.util.List;

public class SendMessage extends Thread {
    protected List<ClientHandler> clients;
    protected String userInput;
    protected DataInputStream console;
    boolean clientConnected = false;
    volatile boolean running = true;
    Server s;

    public SendMessage(List<ClientHandler> clients, Server s){
        this.clients = clients;
        this.userInput = null;
        this.start();
        this.s = s;

        console = new DataInputStream(System.in);
    }

    private boolean clientsExist(){
        if(clients.size()>0){
            return true;
        } else{
            return false;
        }
    }

    /**
     * Gets list of clients connected to the server
     */
    public void getClientList(){
        s.refreshClientList();
        System.out.println("Connected Clients: ");
        for(int i=0; i<clients.size(); i++){
            System.out.println(i+": "+clients.get(i).toString());
        }
    }

    public boolean isClientConnected(int clientNumber){
        try{
            if(clients.get(clientNumber).s.isConnected() && !clients.get(clientNumber).s.isClosed()){
                return true;
            } else{
                return false;
            }
        } catch (IndexOutOfBoundsException e){
            return false;
        }
    }

    /**
     * Attempts to take client number input from user
     * @return - the client number
     */
    private int getClientNumber(){
        try{
            BufferedReader d = new BufferedReader(new InputStreamReader(System.in));
            String line = d.readLine();
            return Integer.parseInt(String.valueOf(line.charAt(0)));
        } catch(IOException | NumberFormatException | StringIndexOutOfBoundsException | ArrayIndexOutOfBoundsException e){
            System.out.println("Error obtaining a client number. Make sure you have entered an integer.");
            return -1;
        }
    }

    /**
     * Attempts to start a client connection
     * @param clientNumber - the number of the client in the list
     */
    public DataOutputStream connectToClient(int clientNumber) throws IOException{
        if(clientNumber==-1){
            return null;
        }
        if(isClientConnected(clientNumber)){
            clientConnected = true;
            return new DataOutputStream(clients.get(clientNumber).s.getOutputStream());
        } else{
            clientConnected = false;
            s.refreshClientList();
            return null;
        }
    }

    /**
     * Sends a String message to a defined output client
     * @param message - String to send to client
     * @param outputToClient - Output stream of client
     */
    public void sendMessage(String message, DataOutputStream outputToClient, int clientNumber){
        if(isClientConnected(clientNumber)){
            try{
                outputToClient.writeUTF(message);
            } catch (IOException e){
                e.printStackTrace();
                System.out.println("Error sending message, Client may have disconnected.");
                clientConnected = false;
                s.refreshClientList();
            }
        }
    }

    public void run(){
        s.refreshClientList();
        while(running){
            if(clientsExist()){
                getClientList();
                System.out.println("Choose a client to message: ");
                String line = "";
                try{
                    int clientNumber = getClientNumber();
                    DataOutputStream output = connectToClient(clientNumber);
                    BufferedReader d = new BufferedReader(new InputStreamReader(System.in));
                    while(!line.equals("Done") && isClientConnected(clientNumber)){
                        System.out.println("Enter message: ");
                        line = d.readLine();
                        sendMessage(line, output, clientNumber);
                    }
                    output.flush();
                    output.close();
                } catch(IOException |NullPointerException e){
                    System.out.println("IO Exception occurred - socket must not exist.");
                    s.refreshClientList();
                }
            }
            else{
                try{
                    System.out.println("No clients currently exist - wait for someone to connect.");
                    Thread.sleep(1000);
                } catch (InterruptedException e){
                    run();
                }
            }
        }
    }
}
