package models;

import java.awt.*;

public class Cercle {
    protected int x;
    protected int y;
    protected int diametre;
    private int rayon;
    protected double aire;

    //va servir pour les bots et le joueur
    public Cercle(int x, int y, int diametre){
        this.x = x;
        this.y = y;
        this.diametre = diametre;
        this.rayon = diametre / 2;
        aire = Math.PI * rayon * rayon;
    }

    //quand deux cercles se croisent
    public boolean intersects(Cercle c){
        // Deux cerlces se croisent ssi (Rayon1 - Rayon2) <= SQRT((X1 - X2)^2 + (Y1-Y2)^2 <= (Rayon1 + Rayon2)
        // METTRE 2 COTES DE L'ÉQUATION AU CARRE + RAPIDE
        int sommeRayons= Math.abs(this.rayon + c.rayon) ;
        int diffRayons= Math.abs(this.rayon - c.rayon) ;
        int distanceCentre = (int) (Math.pow(this.x - c.x, 2) + Math.pow((this.y - c.y), 2));
        return(
                (int)( Math.pow(diffRayons, 2)) <= distanceCentre && (int)( Math.pow(sommeRayons, 2)) >= distanceCentre
                );
    }
    
     public boolean intersects(Rectangle rectangle) {
        // rectangle 'zone' cercle
        Rectangle zone = new Rectangle(x - rayon, y - rayon, diametre, diametre);
        return zone.intersects(rectangle);
    }

    public void aireRayonUpdate(){
        this.rayon = this.diametre/2;
        this.aire = Math.PI * this.rayon * this.rayon;
    }

    //setters
    public void setY(int newY) {
        this.y = newY;
    }

    public void setX(int newX) {
        this.x=newX;
    }

    //getters
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getDiameter(){
        return this.diametre;
    }

    public double getAire() {
        return aire;
    }
}
