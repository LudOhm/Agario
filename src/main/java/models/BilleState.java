package models;

import java.awt.*;
import java.util.Random;

public class BilleState extends Cercle {
    //  petite bille colorée qui fait grossir les boules des joueur

    private String c;

    private final int apportNutritionnel;

    private static int ID = 0;

    private int IDBille;
    private boolean projectile;

    //mode solo
    public BilleState(int x, int y){
        super(x,y, 15);
        this.c= getRandomColorHex();
        apportNutritionnel = 4;
        IDBille = ID;
        ID++;
        projectile = false;
    }

    //constructeur pour les projectiles des balles
    public BilleState(int x, int y, String c){
        //diametre different pour pouvoir visuellement distinguer les projectiles
        super(x,y, 20);
        this.c= c;
        apportNutritionnel = 5;
        IDBille = ID;
        ID++;
        projectile=true;
    }

    // pour le mode online
    public BilleState(int x, int y, int id, int diameter) {
        super(x,y, diameter);
        projectile = false;
        this.IDBille = id;
        apportNutritionnel = 4;
        this.c = getRandomColorHex();
    }

    private String getRandomColorHex() {
        Random rand = new Random();
        String hex = String.format("#%02X%02X%02X", rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
        return hex;
    }

    public static Color hexToColor(String hex) {
        try {
            return Color.decode(hex);
        } catch (NumberFormatException e) {
            // si l'héxadécimal est invalide
            System.err.println("Error: Invalid hexadecimal color string. Returning default color (black).");
            return Color.BLACK; // retourne la couleur noir par défaut
        }
    }
    public void draw(Graphics g, int screenX, int screenY) {
        g.setColor(hexToColor(this.c));
        g.fillOval(screenX, screenY, diametre, diametre);
    }

    public int getApportNutritionnel() {
        return apportNutritionnel;
    }
    public int getIDBille() {
        return IDBille;
    }

    public boolean isProjectile() {
        return projectile;
    }

    public String getColor() { return c; }

    public void setColor(String color) { this.c = color; }

    public int getId() {
        return IDBille;
    }

    public void setId(int Id) {
        this.IDBille = Id;
    }
}