package managers;



import gui.GamePanel;
import models.BilleState;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class BillesGestion {
    // ici que les billes vont être placées sur la map
    private final int nbrBillesTotal = 100; // nombres de billes à l'inititialisation,
    // pourront s'ajouter à ce nombre les projectiles joueurs pendant la partie
    private int nbrbilleCourantes;
    int maxPosX, maxPosY, minPosX, minPosY;
    private ArrayList<BilleState> listeBilles = new ArrayList<>();

    public  BillesGestion(GamePanel gp){
        nbrbilleCourantes = 0;
        maxPosX= gp.getMapWidth()-100;
        maxPosY= gp.getMapHeight()-100;
        minPosX = 100;
        minPosY = 100;
        initialiseListe();
    }

    private int randomCoordonnees(int max, int min){
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }
    
    //on ajoute les billes de base
    public void initialiseListe(){
        while(nbrbilleCourantes < nbrBillesTotal){
            int x =randomCoordonnees(maxPosX,minPosX);
            int y= randomCoordonnees(maxPosY,minPosY);
            BilleState b = new BilleState(x,y);
            listeBilles.add(b);
            nbrbilleCourantes++;
        }
    }

    //quand une bille est mangé on prend des nouvelles coordonées
    public void billeMangeeUpdate(BilleState b) {
        int newX = randomCoordonnees(maxPosX, minPosX);
        int newY = randomCoordonnees(maxPosY, minPosY);
        b.setX(newX);
        b.setY(newY);
    }

    //rajouer des billes a chaque fois
    public void ajouterBille(BilleState nouvelleBille) {
        listeBilles.add(nouvelleBille);
        nbrbilleCourantes++;
    }

    //affichage
    public void dessinerBilles(Graphics g, GamePanel game){
        int screenX;
        int screenY;
        for (BilleState bille : listeBilles) {
            screenX = bille.getX() - game.getOffsetX();
            screenY = bille.getY() -  game.getOffsetY();
            bille.draw(g, screenX, screenY);
        }
    }

    //getter
    public ArrayList<BilleState> getListeBilles() {
        return listeBilles;
    }

}
