package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import models.PlayerState;

//connexion du client au serveur
public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private GameServer server;
    private BufferedReader in;
    private PrintWriter out;
    private String username;
    private Gson gson = new Gson();
    private PlayerState playerState;

    public ClientHandler(Socket clientSocket, GameServer server) {
        this.clientSocket = clientSocket;
        this.server = server;
        System.out.println("Nouvelle connexion utilisateur : " + clientSocket.getRemoteSocketAddress());
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (Exception e) {
            System.err.println("Error in ClientHandler: " + e.getMessage());
        }
    }

    //messages entre le client joueur et le serveur
    @Override
    public void run() {
        try {
            this.username = in.readLine();
            if (this.username == null) {
                throw new IOException("Failed to read username from client.");
            }

            out.println("Welcome");
            server.broadcastMessage("New user joined: " + this.username);

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                try {
                    if (inputLine.startsWith("EXIT:")) {
                        String exitPlayerName = inputLine.split(":")[1];
                        server.removePlayer(exitPlayerName, this);
                        break;
                    } else if (inputLine.startsWith("SPLIT_ACTION:")) {
                        String[] parts = inputLine.split(":", 3);
                        if (parts.length == 3) {
                            String username = parts[1];
                            PlayerState newSplitPart = gson.fromJson(parts[2], PlayerState.class);
                            if (newSplitPart != null) {
                                server.handleSplitAction(username, newSplitPart);
                            } else {
                                System.err.println("Invalid SPLIT_ACTION data.");
                            }
                        } else {
                            System.err.println("SPLIT_ACTION command received without sufficient data.");
                        }
                    } else if (inputLine.startsWith("BILLE_EATEN:")) {
                        int billeId = Integer.parseInt(inputLine.split(":")[1]);
                        server.handleBilleEaten(billeId);
                    } else if (inputLine.startsWith("PlayerEaten:")) {
                        String eatenPlayerName = inputLine.substring("PlayerEaten:".length());
                        server.handlePlayerEaten(eatenPlayerName);
                    }else {
                        PlayerState state = gson.fromJson(inputLine, PlayerState.class);
                        if (state != null && state.getPlayerName() != null) {
                            this.playerState = state;
                            server.updatePlayerState(state);
                            server.broadcastState();
                        } else {
                            System.err.println("Received malformed JSON or incomplete player state.");
                        }
                    }
                } catch (JsonSyntaxException e) {
                    System.err.println("JSON parsing error: " + e.getMessage() + " | Data: " + inputLine);
                } catch (Exception e) {
                    System.err.println("Error processing command: " + e.getMessage() + " | Command: " + inputLine);
                }
            }
            System.out.println("User disconnected: " + clientSocket.getRemoteSocketAddress());
        } catch (IOException e) {
            System.out.println("A connection error occurred: " + e.getMessage() + " | User: " + this.username);
        } finally {
            cleanUpResources();
        }
    }

    private void cleanUpResources() {
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing resources: " + e.getMessage());
        }
    }


    public void sendMessage(String message) {
        out.println(message);
    }

    public PlayerState getPlayerState() {
        return playerState;
    }
}
