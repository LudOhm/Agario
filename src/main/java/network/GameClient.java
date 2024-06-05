package network;

import com.google.gson.Gson;
import models.PlayerState;

import java.net.Socket;
import java.net.UnknownHostException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

//le jeu pour client
public class GameClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    int port = 24935;

    private boolean isConnected = false;

    private Gson gson = new Gson();

    //pour se connecter on verifie l'adresse ip et si le joueur peut se connecter ou non
    public GameClient(int port, String playerName, String ip) {
        try {
            String serverAddress = ip;
            this.socket = new Socket(serverAddress, port);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);

            out.println(playerName);

            String serverResponse = in.readLine();
            if ("Welcome".equals(serverResponse)) {
                System.out.println("connexion réussie : " + serverAddress + " : " + port);
            } else {
                System.err.println("Expected 'Welcome' but received: " + serverResponse);
            }
            isConnected = true;
        } catch (UnknownHostException e) {
            System.err.println("Client error: Unable to determine the local host IP address.");
            e.printStackTrace();
            isConnected = false;
        } catch (IOException e) {
            System.err.println("Client error: Unable to connect to address ");
            e.printStackTrace();
            isConnected = false;
        }
        new Thread(() -> {
            listenForServerMessages();
        }).start();
    }

    //recherche de message du serveur
    private void listenForServerMessages() {
        String message;
        try {
            while ((message = in.readLine()) != null) {
                if (message.startsWith("New user joined:")) {
                    // System.out.println(message);
                } else {
                    // System.out.println(message);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading from server: " + e.getMessage());
            isConnected = false;
        }
    }

    public void sendAction(PlayerState state) {
        if (out != null && isConnected) {
            String message = gson.toJson(state);
            out.println(message);
        }
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    //envoie de l'action d'une bille mangé
    public void sendBilleEatenMessage(int billeId) {
        out.println("BILLE_EATEN:" + billeId);
    }

    //envoie de l'action du joueur qui a ete mangé
    public void sendPlayerEatenMessage(String eatenPlayerName) {
        out.println("PlayerEaten:" + eatenPlayerName);
    }
    
    //envoie de l'action split
    public void sendSplitAction(String playerName, PlayerState newBall) {
        if (out != null && isConnected) {
            String newBallJson = gson.toJson(newBall);
            out.println("SPLIT_ACTION:" + playerName + ":" + newBallJson);
            //System.out.println("Split action sent for " + playerName + " with new ball");
        }
    }  
     
    //messages reçu du serveur
    public String receiveUpdate() {
        try {
            return in.readLine();
        } catch (IOException e) {
            System.err.println("Error reading from server: " + e.getMessage());
            return null;
        }
    }

    public boolean isConnected() {
        return this.isConnected;
    }

    //quand on sort il faut fermer toute connexion lié au serveur
    public void closeConnection() {
        try {
            if (out != null) {
                out.close();
                out = null;
            }
            if (in != null) {
                in.close();
                in = null;
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
                socket = null;
            }
            isConnected = false;
            System.out.println("Connection to server closed successfully.");
        } catch (IOException e) {
            System.err.println("Error closing connection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendExitMessage(String playerName) {
        String exitMessage = "EXIT:" + playerName;
        sendMessage(exitMessage);
    }

}
