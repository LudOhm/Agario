package models;

import controllers.Ball;
import managers.Bot;

import java.awt.*;
import java.util.ArrayList;

public class VirusState {
    // forme octogonal
    private int x, y; // positions qui ne changent pas, aléatoires

    private final int diameter;

    private final int aire;
    private String color = "#00FF00"; // vert

    private Rectangle coeur;
    private int virusID;
    private static int ID = 0;

    public VirusState(int x, int y) {
        this.x = x;
        this.y = y;
        diameter = 80;
        aire = diameter * diameter;
        coeur = new Rectangle(x - diameter / 2, y - diameter / 2, diameter, diameter);
        virusID = ID;
        ID++;
    }

    //affichage
    public void draw(Graphics g, int screenX, int screenY) {
        g.setColor(new Color(31, 173, 53));
        int[] xPoints = new int[8];
        int[] yPoints = new int[8];
        for (int i = 0; i < 8; i++) {
            double angle = 2 * Math.PI * i / 8;
            xPoints[i] = (int) (screenX + diameter * Math.cos(angle));
            yPoints[i] = (int) (screenY + diameter * Math.sin(angle));
        }
        g.fillPolygon(xPoints, yPoints, 8);
        // TEST Limites
        // g.setColor(Color.MAGENTA);
        // g.fillRect(screenX-cote/2, screenY-cote/2, cote, cote);
    }

    //si la taille de la balle est plus grande que le virus alors considéré comme un danger
    public boolean isDangerousFor(Ball b) {
        return b.getAire() > this.aire;
    }

    public boolean isTouchedBy(Ball b) {
        // on considère la balle touchée quand le centre de la balle se raproche trop du
        // centre du virus
        // mais on pourra éventuellement changer ce principe
        return (b.intersects(coeur));
    }

    //on split si la balle touche et est plus grande que le virus
    public ArrayList<Ball> comportement(Ball b) {
        // partie du if commentée en attendant de régler le problème du split continu
        if (isTouchedBy(b) && isDangerousFor(b)) {
            // vérifier que la balle n'a pas déja été infectée par le virus
            if (!b.isInfectée() || (b.isInfectée() && b.getContaminePar() != this.virusID)) {
                b.setInfectée(true);
                b.setContaminePar(this.virusID);
                if (b instanceof Bot) {
                    ((Bot) b).splitBot();
                    return null;
                } else
                    return (b.split());
            } else
                return null;
        } else {
            return null;
        }
    }

    //getter
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }

    public int getDiameter() {
        return diameter;
    }
    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

}
