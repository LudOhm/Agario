package network;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;

import models.BilleState;
import models.PlayerState;
import models.VirusState;

public class GameServer {
    private ServerSocket serverSocket;
    private ExecutorService pool;
    private ArrayList<ClientHandler> clients;
    private List<PlayerState> playerStates = new ArrayList<>();
    private List<BilleState> billes = new ArrayList<>();
    private List<VirusState> viruses = new ArrayList<>();
    private Gson gson = new Gson();
    private Random random = new Random();
    private ScheduledExecutorService gameLoopExecutor;

    //quand on lance le serveur on essaie sur un port spécifique
    public GameServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Le serveur démarre, port : " + port);
            pool = Executors.newCachedThreadPool();
            clients = new ArrayList<>();
            gameLoopExecutor = Executors.newSingleThreadScheduledExecutor();
            initializeBilles();
            initializeViruses();
        } catch (IOException e) {
            System.err.println("Server failed to start: " + e.getMessage());
        }
    }

    //lancement du serveur qui va gérer les clients
    public void startServer() {
        System.out.println("Server started on port: " + serverSocket.getLocalPort());
        try {
            gameLoopExecutor.scheduleAtFixedRate(() -> {
                broadcastState();
                broadcastVirusesState();
            }, 0, 100, TimeUnit.MILLISECONDS);

            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                clients.add(clientHandler);
                pool.execute(clientHandler);
                // broadcastMessage("Welcome");
            }
        } catch (IOException e) {
            System.err.println("Server accept error: " + e.getMessage());
        }
    }

    //on ajoute les billes 
    private void initializeBilles() {
        int initialBilleCount = 100;
        for (int i = 0; i < initialBilleCount; i++) {
            int randomX = random.nextInt(3190);
            int randomY = random.nextInt(3190);
            int diameter =15;
            BilleState bille = new BilleState(randomX, randomY, i, diameter);
            billes.add(bille);
        }
    }

    //on ajoute les virus
    private void initializeViruses() {
        Random rand = new Random();
        for (int i = 0; i < 10; i++) {
            int x = rand.nextInt(3200 - 100) + 50;
            int y = rand.nextInt(3200 - 100) + 50;
            viruses.add(new VirusState(x, y));
        }
    }

    //pour chaque clients on envoie le message
    public void broadcastMessage(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public void broadcastVirusesState() {
        String virusesJson = gson.toJson(viruses);
        for (ClientHandler client : clients) {
            client.sendMessage("VirusesState:" + virusesJson);
        }
    }

    //mise a jour de tous les états du joueur
    public synchronized void updatePlayerState(PlayerState newState) {
        boolean found = false;
        for (int i = 0; i < playerStates.size(); i++) {
            PlayerState existingState = playerStates.get(i);
            if (existingState.getPlayerName().equals(newState.getPlayerName())) {
                // depuis les nouvelles données
                existingState.setX(newState.getX());
                existingState.setY(newState.getY());
                existingState.setDiameter(newState.getDiameter());
                found = true;
            }
            // pour aussi update le possible enfant de la balle
            if (existingState.isFollowing() && existingState.getParentName().equals(newState.getPlayerName())) {
                // calcul de la position de l'enfant de la balle
                updateFollowingBallPosition(existingState, newState);
            }
        }
        if (!found) {
            playerStates.add(newState);
        }
        broadcastState();
    }
    
    //mise a jour de la balle split
    private void updateFollowingBallPosition(PlayerState child, PlayerState parent) {
        double distance = 100;  // set the distance you want the child to follow from parent
        double angle = Math.atan2(child.getY() - parent.getY(), child.getX() - parent.getX());
        child.setX((int) (parent.getX() + distance * Math.cos(angle)));
        child.setY((int) (parent.getY() + distance * Math.sin(angle)));
    }
    

    //envoie au client du message sur son état et celles des billes sur la map
    public synchronized void broadcastState() {
        String allPlayerStatesJson = gson.toJson(playerStates);
        String billesJson = gson.toJson(billes);
        for (ClientHandler client : clients) {
            client.sendMessage("PlayerStates:" + allPlayerStatesJson);
            client.sendMessage("Billes:" + billesJson);
        }
    }

    //quand on enlève le client du jeu on le retire du serveur
    public synchronized void removePlayer(String playerName, ClientHandler exitingClient) {
        clients.remove(exitingClient);
        playerStates.removeIf(state -> state.getPlayerName().equals(playerName));
        broadcastState();
    }

    //on rajoute des billes dès qu'une est mangé
    public synchronized void handleBilleEaten(int billeId) {
        billes.removeIf(bille -> bille.getId() == billeId);

        int newBilleId = billes.size();
        int newBilleX = random.nextInt(3200);
        int newBilleY = random.nextInt(3200);
        int newBilleDiameter = 15;
        BilleState newBille = new BilleState(newBilleX, newBilleY, newBilleId, newBilleDiameter);
        billes.add(newBille);

        broadcastState();
    }

    //gère quand un joueur est mangé
    public synchronized void handlePlayerEaten(String eatenPlayerName) {
        PlayerState eatenPlayer = playerStates.stream()
            .filter(p -> p.getPlayerName().equals(eatenPlayerName))
            .findFirst()
            .orElse(null);
    
        if (eatenPlayer != null) {
            PlayerState eaterPlayer = findEaterPlayer(eatenPlayer);
            if (eaterPlayer != null && !eaterPlayer.equals(eatenPlayer)) {
                eaterPlayer.setDiameter(eaterPlayer.getDiameter() + eatenPlayer.getDiameter() / 4);
    
                // on met la balle split en première dans le cas ou la première est celle mangé
                playerStates.stream()
                    .filter(p -> p.getParentName() != null && p.getParentName().equals(eatenPlayer.getPlayerName()))
                    .forEach(child -> {
                        child.setFollowing(false);
                        child.setParentName(null);
                    });
                
                playerStates.remove(eatenPlayer);
                broadcastState();
                broadcastMessage("GAME_OVER:" + eatenPlayerName);
                broadcastMessage("REMOVE_PLAYER:" + gson.toJson(eatenPlayer));
            }
        }
    }    
    
    //trouve parmi les joueur celui qui a mangé l'autre joueur
    private PlayerState findEaterPlayer(PlayerState eatenPlayer) {
        return playerStates.stream()
            .filter(p -> !p.getPlayerName().equals(eatenPlayer.getPlayerName()) && p.getDiameter() > eatenPlayer.getDiameter())
            .min(Comparator.comparingDouble(p -> Math.hypot(p.getX() - eatenPlayer.getX(), p.getY() - eatenPlayer.getY())))
            .orElse(null);
    }
     
    //gère l'action su split
    public void handleSplitAction(String username, PlayerState originalPlayer) {
        PlayerState player = playerStates.stream()
                .filter(p -> username.equals(p.getPlayerName()))
                .findFirst()
                .orElse(null);
    
        if (player != null && player.getDiameter() > 50) {
            int splitDiameter = player.getDiameter() / 2;
            player.setDiameter(splitDiameter);
    
            double angle = Math.random() * 2 * Math.PI;
            int distance = splitDiameter + 10;
            int newX = (int) (player.getX() + distance * Math.cos(angle));
            int newY = (int) (player.getY() + distance * Math.sin(angle));
    
            String newPlayerName = PlayerState.getNextSplitName(username);
            PlayerState newBall = new PlayerState(newPlayerName, newX, newY, splitDiameter, player.getSpeed(), player.getColor(), true);
            newBall.setFollowing(true);
            newBall.setParentName(username);
            playerStates.add(newBall);

            System.out.println("Split performed: New ball " + newBall.getPlayerName() + " at (" + newX + ", " + newY + ") with diameter " + newBall.getDiameter());

            broadcastState();
        }
        else {
            System.out.println("Split failed: Player not found or diameter too small");
        }
    }
       
    //lance le serveur quand appelé
    public static void main(String[] args) {
        GameServer server = new GameServer(24935);
        server.startServer();
    }
}
