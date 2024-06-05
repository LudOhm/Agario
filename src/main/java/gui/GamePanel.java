package gui;

import javax.swing.*;
import java.util.Iterator;

import controllers.Ball;
import models.BilleState;
import models.VirusState;
import managers.*;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

//le fichier du jeu solo
public class GamePanel extends JPanel implements MouseMotionListener, MouseListener{
    private Ball ball;
    private int mouseX, mouseY;
    private Timer timer;
    private final int viewWidth = 900; // largeur du fenetre
    private final int viewHeight = 600; // hauteur du fenetre
    private final int mapWidth = 3200; // largeur du map
    private final int mapHeight = 3200; // hauteur du map
    private BillesGestion billesGestion;
    private VirusGestion virusGestion;
    private JButton returnButton;
    private String colorSelect;

    // décalage de la fenêtre
    private int offsetX = 0;
    private int offsetY = 0;
    private ArrayList<Ball> balls;
    private ArrayList<Bot> bots;
    private ArrayList<Ball> totalRank = new ArrayList<>();
    private final int miniMapWidth = 150;
    private final int miniMapHeight = 150;
    private final int miniMapMargin = 10;
    private PlayerRank classement;

    //une liste car on peu avoir plusieurs balle par joueur
    private Ball[] arrayRank = new Ball[10];

    //constructeur pour initialisé toutes les fonctionnalités du jeu
    public GamePanel(JFrame frame, String playerName, String colorSelect) {
        //joueur
        this.colorSelect = colorSelect;
        ball = new Ball(50, playerName, mapWidth, mapHeight, colorSelect);
        balls = new ArrayList<>();
        balls.add(ball);
        // bots
        initBots();
        // billes
        billesGestion = new BillesGestion(this);
        //virus
        virusGestion = new VirusGestion(this);
        //classement
        classement = new PlayerRank(this);
        setFocusable(true);
        addMouseMotionListener(this);
        addMouseListener(this);
        mouseX = 100;
        mouseY = 100;

        //timer du jeu 
        timer = new Timer(16, e -> {
            update();
            repaint();
            checkCollisionULTIMATE();
            virusBehavior();
            rankBalls();
        });
        timer.start();

        // retour à l'écran de selection du mode de jeu
        returnButton = new JButton("Back");
        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopTimer();
                frame.setContentPane(new ModeSelectPanel(frame, playerName, colorSelect));
                frame.invalidate();
                frame.validate();
            }
        });
        add(returnButton);

    }

    //ajouter les bots dans le jeu
    private void initBots() {
        bots = new ArrayList<>();
        for (int i = 1; i < 31; i++) {
            bots.add(new Bot(mapWidth, mapHeight));
        }
    }

    //mise a jour du jeu avec le timer
    private void update() {

        for (Ball currentBall : balls) {
            currentBall.moveTowards(mouseX + offsetX, mouseY + offsetY, mapWidth, mapHeight);

            // Met à jour le décalage de la fenêtre pour conserver la Ball au centre de la
            // fenêtre et permettre la vue au-delà des limites de la carte
            offsetX = (int) Math.round(currentBall.getX() - viewWidth / 2 + currentBall.getDiameter() / 2);
            offsetY = (int) Math.round(currentBall.getY() - viewHeight / 2 + currentBall.getDiameter() / 2);
        }

        // Autorisez le décalage de la fenêtre à dépasser les limites de map
        ball.setX((int) Math.max(0, Math.min(ball.getX(), mapWidth - ball.getDiameter())));
        ball.setY((int) Math.max(0, Math.min(ball.getY(), mapHeight - ball.getDiameter())));

        // Mouvements bots
        ArrayList<BilleState> listeBilles = billesGestion.getListeBilles();
        for (Bot b : bots) {
            b.botMove(mapWidth, mapHeight, ball, listeBilles, bots);
            b.botUpdate();
        }
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

        // DESSIN DES BILLES
        billesGestion.dessinerBilles(g, this);

        // DESSIN DES BOTS
        for (Bot b : bots)
            b.botDraw(g2d);

        // DESSIN DES BALLE DU JOUEUR
        for (Ball currentBall : balls) {
            currentBall.draw(g2d);
        }

        // VIRUS/BUISSONS
        virusGestion.drawVirus(g, this);

        g2d.dispose();

        // MINI MAP
        int miniMapX = viewWidth - miniMapWidth - miniMapMargin;
        int miniMapY = viewHeight - miniMapHeight - miniMapMargin;
        g.setColor(Color.GRAY);
        g.fillRect(miniMapX, miniMapY, miniMapWidth, miniMapHeight);

        for (Ball ball : balls) {
            Point miniMapPos = calculateMiniMapPosition(ball);
            int ballDiameterOnMiniMap = 5;
            g.setColor(ball.getColor());
            g.fillOval(miniMapX + miniMapPos.x - ballDiameterOnMiniMap / 2,
                    miniMapY + miniMapPos.y - ballDiameterOnMiniMap / 2,
                    ballDiameterOnMiniMap,
                    ballDiameterOnMiniMap);
        }
        // CLASSEMENT
        classement.draw(g);
    }

    public void checkCollisionULTIMATE(){
        //Le but est d'éviter les répétitions
        // Tous les bots et ttes les balles peuvent manger des billes
        ArrayList<Ball> allBallsandBots = new ArrayList<>();
        allBallsandBots.addAll(balls); allBallsandBots.addAll(bots);
        Iterator<Ball> allIterator = allBallsandBots.iterator();
        while (allIterator.hasNext()){
            Ball botOuBalle = allIterator.next();
            Iterator<BilleState> bi = billesGestion.getListeBilles().iterator();
            while(bi.hasNext()) {
                BilleState b = bi.next();
                if(b.intersects(botOuBalle)) {
                    botOuBalle.mange(b);
                    if(b.isProjectile()) bi.remove();
                    else billesGestion.billeMangeeUpdate(b);
                }
            }
            //____JUSQU'ICI ON  EST BONS________
            //Les cas ou bot se mangent entre eux et balle mange bot
            Iterator<Bot> botIterator = bots.iterator();
            while (botIterator.hasNext()){
                Bot bot = botIterator.next();
                // on s'assure qu'un bot ne se mange pas lui-même
                if((!(botOuBalle instanceof Bot)) || (botOuBalle instanceof Bot && bot.getID()!=((Bot) botOuBalle).getID())){
                    if (botOuBalle.intersects(bot) && bot.getDiameter() < botOuBalle.getDiameter()) {
                        botOuBalle.mange(bot);
                        bot.botMangeUpdate(mapWidth, mapHeight);
                    }
                }
            }
        } ///SUITE CAS UNE BALLE PEUT ETRE MANGEE PAR UN BOT
        Iterator<Ball> ballIterator = balls.iterator();
        // parcours toutes les balles
        while (ballIterator.hasNext()) {
            Ball balle = ballIterator.next();
            for (Bot bot: bots) {
                if(balle.intersects(bot) && bot.getDiameter() > ball.getDiameter()){
                    bot.mange(balle);
                    if (balls.size() > 1) {
                        ballIterator.remove();
                    }else{
                        //perd que si le joueur n'a plus aucune balle sur la map
                        defaite();
                    }
                }
            }
        }
    }

    //si le joueur perd alors il va vers un ecran de défaite
    public void defaite(){
        stopTimer();
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.setContentPane(new GameOver(frame, ball.getName(), colorSelect));
        frame.invalidate();
        frame.validate();
    }

    //la fonctionnalité des virus
    public void virusBehavior() {
        ArrayList<VirusState> virus = this.virusGestion.getListeVirus();
        for (Ball b : totalRank) {
            for (VirusState v : virus) {
                // check si la balle +grosse que le virus et si ils se touchent
                ArrayList<Ball> miniBalles = v.comportement(b);
                if (miniBalles != null)
                    balls.addAll(miniBalles);

            }
        }
    }

    //pour le classement
    public Ball findLargestBall(ArrayList<Ball> balls) {
        Ball largestBall = null;
        double largestDiameter = 0;

        for (Ball currentBall : balls) {
            if (currentBall.getDiameter() > largestDiameter) {
                largestDiameter = currentBall.getDiameter();
                largestBall = currentBall;
            }
        }

        return largestBall;
    }

    public void rankBalls() {
        // Ball + bots triés +grand au + petit
        totalRank = new ArrayList<>();
        Ball joueur = findLargestBall(balls);
        if (!totalRank.contains(joueur))
            totalRank.add(joueur);
        for (Bot b : bots) {
            if (!totalRank.contains(b))
                totalRank.add(b);
        }
        if(totalRank !=null)Collections.sort(totalRank, Comparator.comparingDouble(Ball::getAire).reversed());
        boolean contientJoueur = false;
        int i = 0;
        for (Ball b : totalRank) {
            if (i < 10) {
                if (!(b instanceof Bot) && contientJoueur == false) {
                    arrayRank[i] = b;
                    contientJoueur = true;
                    i++;
                } else if ((b instanceof Bot)) {
                    arrayRank[i] = b;
                    i++;
                }
            } else
                break;
        }
    }

    //arret du timer du jeu
    public void stopTimer() {
        if (timer != null) {
            timer.stop();
        }
    }

    //pour l'affichage sur la mini map
    private Point calculateMiniMapPosition(Ball ball) {
        double scaleX = (double) miniMapWidth / mapWidth;
        double scaleY = (double) miniMapHeight / mapHeight;
        int x = (int) (ball.getX() * scaleX);
        int y = (int) (ball.getY() * scaleY);
        return new Point(x, y);
    }

    //fonctions lié à la souris
    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // Cette méthode est appelée lorsque la souris est déplacée
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            balls.addAll(ball.split());
        } else if (SwingUtilities.isRightMouseButton(e) && ball.getDiameter() >= 54) { //pour pas avoir diametre<50
            Ball largestBall = findLargestBall(balls);
            largestBall.lanceProjectile(mouseX, mouseY, billesGestion);
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

    //getters
    public Ball getBall() {
        return ball;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public int getViewWidth() {
        return viewWidth;
    }

    public Ball[] getArrayRank() {
        return arrayRank;
    }

}
