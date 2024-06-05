package gui;

import javax.swing.*;

import models.BilleState;
import models.PlayerState;
import models.VirusState;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import controllers.Ball;

import com.google.gson.JsonSyntaxException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.awt.*;
import java.awt.event.*;

import network.GameClient;

//jeu en ligne
public class OnlineGamePanel extends JPanel implements MouseMotionListener, MouseListener {
    private GameClient client;
    private final int viewWidth = 900;
    private final int viewHeight = 600;
    private final int mapWidth = 3200;
    private final int mapHeight = 3200;
    private int offsetX = 0;
    private int offsetY = 0;
    private Point mousePosition = new Point(0, 0);
    private String color;

    private String playerName;

    private List<PlayerState> playerStates; // Liste de statut de tous les joueurs
    private PlayerState localPlayerState; // statut de joueur local
    private List<BilleState> billes = new ArrayList<>();
    private List<VirusState> viruses = new ArrayList<>();
    private PlayerState lastEatenPlayer = null;

    private Gson gson = new Gson();

    private final int miniMapWidth = 150;
    private final int miniMapHeight = 150;
    private final int miniMapMargin = 10;

    private boolean isAlive = true;

    private final int UPDATE_INTERVAL = 20;

    public OnlineGamePanel(JFrame frame, String playerName, String colorSelect, int port, String ip) {
        this.client = new GameClient(port, playerName, ip);
        this.playerName = playerName;
        this.playerStates = new ArrayList<>();
        if (colorSelect == null || colorSelect.endsWith(".png")) {
            this.color = colorToHex(Ball.getRandomColor());
        } else {
            this.color = colorSelect;
        }
        setFocusable(true);
        addMouseMotionListener(this);
        addMouseListener(this);

        Random rand = new Random();
        int randomX = rand.nextInt(mapWidth);
        int randomY = rand.nextInt(mapHeight);

        // Initialiser le statut du joueur local
        localPlayerState = new PlayerState(playerName, randomX, randomY, 50, 5, color);

        // Envoyer le statut initial au serveur
        client.sendAction(localPlayerState);

        initUI(frame);

        // Commence à écouter les messages du serveur
        new Thread(this::listenToServer).start();

        // Défini pour actualiser l'écran toutes les 16 millisecondes
        new Timer(UPDATE_INTERVAL, e -> {
            if (client.isConnected()) {
                updateGameState();
                repaint();
            }
        }).start();
    }

    //mise a jour de l'état du jeu et du joueur
    private void updateGameState() {
        movePlayerTowardsMouse();
        checkAndHandleCollisions();
        checkAndHandlePlayerCollisions();
        checkAndHandleVirusCollisions();
        repaint();
    }

    //les messages entre le serveur et le client
    private void listenToServer() {
        while (client.isConnected() && isAlive) {
            String message = client.receiveUpdate();
            if (message != null) {
                System.out.println("Raw message: " + message);
                try {
                    if (message.startsWith("PlayerStates:")) {
                        String json = message.substring("PlayerStates:".length());
                        Type listType = new TypeToken<List<PlayerState>>() {
                        }.getType();
                        List<PlayerState> updatedStates = gson.fromJson(json, listType);
                        this.playerStates.clear();
                        this.playerStates.addAll(updatedStates);

                        for (PlayerState state : updatedStates) {
                            if (state.getPlayerName().equals(this.playerName)) {
                                this.localPlayerState = state;
                                // targetPosition.setLocation(state.getX(), state.getY());
                                break;
                            }
                        }
                        System.out.println("Updated player states: " + updatedStates.size());
                        repaint();
                    }
                    if (message.startsWith("Billes:")) {
                        String json = message.substring(7);
                        Type listType = new TypeToken<List<BilleState>>() {
                        }.getType();
                        List<BilleState> updatedBilles = gson.fromJson(json, listType);
                        this.billes.clear();
                        this.billes.addAll(updatedBilles);
                        repaint();
                    }
                    if (message.startsWith("GAME_OVER:")) {
                        String playerName = message.split(":")[1];
                        if (this.playerName.equals(playerName)) {
                            isAlive = false;
                            SwingUtilities.invokeLater(() -> {
                                handleGameOver();
                            });
                            break;
                        }
                    }
                    if (message.startsWith("REMOVE_PLAYER:")) {
                        String json = message.substring("REMOVE_PLAYER:".length());
                        PlayerState removedPlayer = gson.fromJson(json, PlayerState.class);
                        playerStates.removeIf(player -> player.getPlayerName().equals(removedPlayer.getPlayerName()));
                        if (removedPlayer.getPlayerName().equals(this.playerName)) {
                            // quand le joueur en local est manger on met le gameovert
                            isAlive = false;
                            SwingUtilities.invokeLater(this::handleGameOver);
                        }
                        repaint();
                    }
                    if (message.startsWith("VirusesState:")) {
                        String json = message.substring("VirusesState:".length());
                        Type listType = new TypeToken<List<VirusState>>() {
                        }.getType();
                        List<VirusState> updatedViruses = gson.fromJson(json, listType);
                        this.viruses.clear();
                        this.viruses.addAll(updatedViruses);
                        repaint();
                    }
                } catch (JsonSyntaxException e) {
                    // On catch bcp trop d'erreur donc la lenteur doit venir d'ici
                    //
                    System.err.println("JSON parsing error: " + e.getMessage());
                }
            }
        }
    }

    //gère quand le joueur a perdu donc déconnexion et écran de gameover
    private void handleGameOver() {
        client.sendExitMessage(playerName);
        client.closeConnection();

        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.setContentPane(new GameOver(frame, playerName, color));
        frame.invalidate();
        frame.validate();
    }

    //le bouton pour sortir du jeu et revenir à l'écran de selection
    private void initUI(JFrame frame) {
        JButton returnButton = new JButton("Back");
        returnButton.addActionListener(e -> {
            client.sendExitMessage(playerName);
            client.closeConnection();

            frame.setContentPane(new ModeSelectPanel(frame, playerName, color));
            frame.invalidate();
            frame.validate();
        });
        this.add(returnButton);
    }

    //fonctionnalité de split
    private void performSplit() {
        int currentDiameter = localPlayerState.getDiameter();
        if (currentDiameter > 60) { // un minimum pour split
            int splitDiameter = currentDiameter / 2;
    
            localPlayerState.setDiameter(splitDiameter);
    
            // calculer la position de la nouvelle balle
            double angle = Math.random() * 2 * Math.PI;
            int distance = splitDiameter + 10; // pour pas qu'elle se superpose tout de suite
            int newX = (int) (localPlayerState.getX() + distance * Math.cos(angle));
            int newY = (int) (localPlayerState.getY() + distance * Math.sin(angle));
    
            // ajouter la nouvelle balle
            String newPlayerName = PlayerState.getNextSplitName(localPlayerState.getPlayerName());
            PlayerState newBall = new PlayerState(newPlayerName, newX, newY, splitDiameter, localPlayerState.getSpeed(), localPlayerState.getColor(), true);
    
            playerStates.add(newBall); // ajout en local
    
            // ajout de l'action su split et de l'état de la nouvelle balle au serveur
            client.sendSplitAction(localPlayerState.getPlayerName(), newBall);
            client.sendAction(localPlayerState); // update l'état du joueur au serveur
            client.sendAction(newBall); // envoie l'état de la nouvelle balle
            System.out.println("Split performed: " + newBall.getPlayerName() + " at (" + newX + ", " + newY + ")");
        } else {
            System.out.println("Split failed: Current diameter (" + currentDiameter + ") is too small.");
        }
    }  

    //déplecment du joueur
    private void movePlayerTowardsMouse() {
        if (localPlayerState == null)
            return;

        double dx = mousePosition.x - (viewWidth / 2);
        double dy = mousePosition.y - (viewHeight / 2);
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance > 3) {
            dx /= distance;
            dy /= distance;

            // Mettre à jour la position du ball
            int newX = localPlayerState.getX() + (int) (dx * localPlayerState.getSpeed());
            int newY = localPlayerState.getY() + (int) (dy * localPlayerState.getSpeed());

            newX = Math.max(50, Math.min(newX, mapWidth - 50));
            newY = Math.max(50, Math.min(newY, mapHeight - 50));

            localPlayerState.setX(newX);
            localPlayerState.setY(newY);

            client.sendAction(localPlayerState);
        }
    }

    //fonctions de collision 
    private void checkAndHandleCollisions() {
        List<BilleState> eatenBilles = new ArrayList<>();
        for (BilleState bille : billes) {
            if (isColliding(localPlayerState, bille)) {
                eatenBilles.add(bille);
                localPlayerState.setDiameter(localPlayerState.getDiameter() + 5);
                client.sendAction(localPlayerState);
                client.sendBilleEatenMessage(bille.getId());
            }
        }
        billes.removeAll(eatenBilles);
    }

    //collisions des joueurs entre eux
    private void checkAndHandlePlayerCollisions() {
        List<PlayerState> toRemove = new ArrayList<>();
        PlayerState currentPlayer = localPlayerState;
        for (PlayerState otherPlayer : playerStates) {
            if (!otherPlayer.equals(currentPlayer) && isColliding(currentPlayer, otherPlayer)) {
                if (currentPlayer.getDiameter() > otherPlayer.getDiameter()) {
                    if (lastEatenPlayer == null || !lastEatenPlayer.getPlayerName().equals(otherPlayer.getPlayerName())) {
                        currentPlayer.setDiameter(currentPlayer.getDiameter() + otherPlayer.getDiameter() / 4);
                        System.out.println(currentPlayer + " in onlinepanel deviens big");
                        lastEatenPlayer = otherPlayer;
                    }
                    toRemove.add(otherPlayer);
                    client.sendAction(currentPlayer);
                    client.sendPlayerEatenMessage(otherPlayer.getPlayerName());
                }
            } 
        }
        playerStates.removeAll(toRemove);
    }    

    //colisions alec les virus
    private void checkAndHandleVirusCollisions() {
        for (VirusState virus : viruses) {
            if (isColliding(localPlayerState, virus)&& virus.getDiameter()< localPlayerState.getDiameter()) {
                performSplit();
                break;
            }
        }
    }

    //différentes variable de la fonction qui vérifie si une balle et un virus se touchent bien
    private boolean isColliding(PlayerState player, VirusState virus) {
        double dx = player.getX() - virus.getX();
        double dy = player.getY() - virus.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance < (player.getDiameter() / 2 + virus.getDiameter() / 2);
    }

    private boolean isColliding(PlayerState player, BilleState bille) {
        double dx = player.getX() - bille.getX();
        double dy = player.getY() - bille.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance < (player.getDiameter() / 2 + bille.getDiameter() / 2);
    }

    private boolean isColliding(PlayerState player1, PlayerState player2) {
        double dx = player1.getX() - player2.getX();
        double dy = player1.getY() - player2.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance < (player1.getDiameter() / 2 + player2.getDiameter() / 2);
    }

    //affichage
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, viewWidth, viewHeight);

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.translate(-offsetX, -offsetY);

        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, mapWidth, mapHeight);

        g2d.setColor(Color.BLACK);
        final int gridSize = 50;
        for (int x = 0; x <= mapWidth; x += gridSize) {
            g2d.drawLine(x, 0, x, mapHeight);
        }
        for (int y = 0; y <= mapHeight; y += gridSize) {
            g2d.drawLine(0, y, mapWidth, y);
        }

        g2d.drawRect(0, 0, mapWidth - 1, mapHeight - 1);

        int halfViewWidth = viewWidth / 2;
        int halfViewHeight = viewHeight / 2;

        int mapEdgeToCenterX = mapWidth - halfViewWidth;
        int mapEdgeToCenterY = mapHeight - halfViewHeight;

        offsetX = Math.min(Math.max(localPlayerState.getX() - halfViewWidth, 0), mapEdgeToCenterX);
        offsetY = Math.min(Math.max(localPlayerState.getY() - halfViewHeight, 0), mapEdgeToCenterY);

        for (PlayerState state : playerStates) {
            if (!state.getPlayerName().equals(playerName)) {
                System.out.println(
                        "Rendering player " + state.getPlayerName() + " with diameter: " + state.getDiameter());
                drawPlayer(g, state, offsetX, offsetY, state.getColor());
            }
        }
        drawPlayer(g, localPlayerState, offsetX, offsetY, color);

        for (BilleState bille : billes) {
            int screenX = bille.getX() - offsetX;
            int screenY = bille.getY() - offsetY;
            g.setColor(Color.decode(bille.getColor()));
            g.fillOval(screenX, screenY, bille.getDiameter(), bille.getDiameter());
        }

        for (VirusState virus : viruses) {
            drawVirus(g, virus.getX() - offsetX, virus.getY() - offsetY, virus.getDiameter());
        }

        drawMiniMap(g);
        drawLeaderboard(g);
    }

    //affiche le classement en ligne
    private void drawLeaderboard(Graphics g) {
        List<PlayerState> sortedPlayers = playerStates.stream()
            .filter(p -> !p.isSplitBall())  // 只包括非分裂产生的球体 /??
            .sorted((p1, p2) -> Integer.compare(p2.getDiameter(), p1.getDiameter()))
            .collect(Collectors.toList());

        int leaderboardWidth = 200;
        int leaderboardHeight = 300;
        int x = viewWidth - leaderboardWidth - 20;
        int y = 20;
        g.setColor(new Color(0, 0, 0, 128));
        g.fillRect(x, y, leaderboardWidth, leaderboardHeight);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Leaderboard", x + 20, y + 25);

        int rankY = y + 50;
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        for (int i = 0; i < Math.min(sortedPlayers.size(), 10); i++) {
            PlayerState player = sortedPlayers.get(i);
            String playerInfo = String.format("%d. %s - %d", i + 1, player.getPlayerName(), player.getDiameter());
            g.drawString(playerInfo, x + 10, rankY);
            rankY += 20;
        }
    }

    //mettre les virus 
    private void drawVirus(Graphics g, int x, int y, int diameter) {
        g.setColor(new Color(31, 173, 53));

        int radius = diameter / 2;
        int[] xPoints = new int[8];
        int[] yPoints = new int[8];
        for (int i = 0; i < 8; i++) {
            double angle = 2 * Math.PI * i / 8;
            xPoints[i] = (int) (x + radius + radius * Math.cos(angle));
            yPoints[i] = (int) (y + radius + radius * Math.sin(angle));
        }
        g.fillPolygon(xPoints, yPoints, 8);
    }

    //afficher la minimap
    private void drawMiniMap(Graphics g) {
        int miniMapX = viewWidth - miniMapWidth - miniMapMargin;
        int miniMapY = viewHeight - miniMapHeight - miniMapMargin;

        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(miniMapX, miniMapY, miniMapWidth, miniMapHeight);

        for (PlayerState state : playerStates) {
            if (state.getPlayerName().equals(this.playerName)) {
                int posX = miniMapX + (int) ((double) state.getX() / mapWidth * miniMapWidth);
                int posY = miniMapY + (int) ((double) state.getY() / mapHeight * miniMapHeight);

                g.setColor(Color.decode(state.getColor()));
                g.fillOval(posX - 2, posY - 2, 4, 4);
                break;
            }
        }
    }

    //afficher le joueur en local
    private void drawPlayer(Graphics g, PlayerState state, int offsetX, int offsetY, String color) {
        int playerX = state.getX() - offsetX;
        int playerY = state.getY() - offsetY;
        int diameter = state.getDiameter();
        int x = state.getX() - offsetX;
        int y = state.getY() - offsetY;

        g.setColor(Color.decode(state.getColor()));
        g.fillOval(playerX - diameter / 2, playerY - diameter / 2, diameter, diameter);

        g.setColor(Color.BLACK);
        g.drawOval(playerX - diameter / 2, playerY - diameter / 2, diameter, diameter);

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString(state.getPlayerName(), x - g.getFontMetrics().stringWidth(state.getPlayerName()) / 2, y);
    }

    //fonctions lié à la souris
    @Override
    public void mouseMoved(MouseEvent e) {
        mousePosition.setLocation(e.getPoint());
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e) && isAlive) {
            performSplit();
        }
    }    
    
    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    //pour avoir la couleur des balles présentent
    public static String colorToHex(Color color) {
        String hex = String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
        return hex;
    }

}
