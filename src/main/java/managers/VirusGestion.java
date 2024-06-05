package managers;

import gui.GamePanel;
import models.VirusState;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class VirusGestion {

    private final int nbrVirus =10;

    private ArrayList<VirusState> listeVirus = new ArrayList<>();

    int maxPosX, maxPosY, minPosX, minPosY;

    public VirusGestion(GamePanel gp) {
        maxPosX = gp.getMapWidth() - 100;
        maxPosY = gp.getMapHeight() - 100;
        minPosX = 100;
        minPosY = 100;
        initListe();
    }

    //ils seront placé de façon aléatoire sur la map
    public int randomCoord(int max, int min) {
        Random rand = new Random();
        return rand.nextInt(max - min) + min;
    }

    //on ajoute les virus a la liste
    private void initListe() {
        for (int i = 0; i < nbrVirus; i++) {
            int x = randomCoord(maxPosX, minPosX);
            int y = randomCoord(maxPosY, minPosY);
            listeVirus.add(new VirusState(x, y));
            // TEST
            // System.out.println("x : " + x + " y : " + y);
        }
    }

    //affichage
    public void drawVirus(Graphics g, GamePanel game) {
        int screenX;
        int screenY;
        for (VirusState virus : listeVirus) {
            screenX = virus.getX() - game.getOffsetX();
            screenY = virus.getY() - game.getOffsetY();
            virus.draw(g, screenX, screenY);
        }
    }

    //getter
    public ArrayList<VirusState> getListeVirus() {
        return listeVirus;
    }
}
