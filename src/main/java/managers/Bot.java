package managers;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Random;

import controllers.Ball;
import models.BilleState;
import models.Cercle;

public class Bot extends Ball {
    private int diametreDetection;
    private Cercle cercleDetection;

    private double velocityX;
    private double velocityY;
    private static int count = 0; 
    private int ID; // UTILISE POUR LE NOM BOT : IL PEUT ÉVOLUER si le bot est mangé

    public Bot(int mapWidth, int mapHeight){
        super(50, "", mapWidth, mapHeight);
        diametreUpdate();
        cercleDetection = new Cercle( (int) Math.round(this.x-diametreDetection*0.4), (int) Math.round(this.x-diametreDetection*0.4), diametreDetection);
        initVelocity();
        this.ID=count++;
        name = "Bot "+ID;
    }   

    public void initVelocity(){
        Random rand = new Random();
        int randomNumber = rand.nextInt(3) - 1;
        while(randomNumber==0) randomNumber=rand.nextInt(3) - 1;
        velocityX=randomNumber;
        velocityY=randomNumber;
    }

    //mouvement des bots
    public void botMove(int mapWidth, int mapHeight, Ball b, ArrayList<BilleState> billes, ArrayList<Bot> bots){
        if (cercleDetection.intersects(b)){
            if(this.getDiameter()>b.getDiameter()){
                double angle = Math.atan2(b.getY(), b.getX());
                velocityX = Math.cos(angle);
                velocityY = Math.sin(angle);
                x+=velocityX*speed*0.7; 
                y+=velocityY*speed*0.7;
            }
            //si le bots se sent en danger car plus petit alors il va aller dans la direction opposé
            if(this.getDiameter()<b.getDiameter()){    
                velocityX*= -1;
                velocityY*= -1;
                x+=velocityX*speed*0.7; 
                y+=velocityY*speed*0.7;
            }
        }
        else {
            for(BilleState bi : billes){
                if(cercleDetection.intersects(bi)){
                    double angle = Math.atan2(bi.getY(), bi.getX());
                    velocityX =Math.cos(angle);
                    velocityY = Math.sin(angle);
                    x+=velocityX*speed*0.7; 
                    y+=velocityY*speed*0.7;
                }
            }
            for(Bot bot : bots){
                if(this!=bot){
                    if(cercleDetection.intersects(bot)){
                        if(bot.getDiameter()<this.getDiameter()){
                            double angle = Math.atan2(bot.getY(), bot.getX());
                            velocityX = Math.cos(angle);
                            velocityY = Math.sin(angle);
                            x+=velocityX*speed*0.7; 
                            y+=velocityY*speed*0.7;
                        }
                    }
                }
            }
        }

        x+=velocityX*speed*0.7; 
        y+=velocityY*speed*0.7;
        
        if (x < 0) {
            x = 0;
            velocityX*=-1;
        }
        else if (y < 0){
            y = 0;
            velocityY*=-1;
        }
        else if (x > mapWidth - diametre){
            x = mapWidth - diametre;
            velocityX*=-1;
        }
        else if (y > mapHeight - diametre){
            y = mapHeight - diametre;
            velocityY*=-1;
        }
        
                    
        //System.out.println(x + "   " + y);
    }

    //mise a jour des bots
    public void botUpdate(){
        diametreUpdate();
        detectionUpdate();
    }

    //mise a jour de leur coordonées de directions
    public void detectionUpdate(){
        cercleDetection.setX((int) Math.round(this.x-diametreDetection*0.4));
        cercleDetection.setY((int) Math.round(this.y-diametreDetection*0.4));
    }

    public void diametreUpdate(){
        diametreDetection=diametre*5;
    }

    //affichage
    public void botDraw(Graphics g){
        g.setColor(color);
        g.fillOval((int) Math.round(x), (int) Math.round(y), diametre, diametre);
        Graphics2D g2d = (Graphics2D) g.create(); 
        g2d.setColor(Color.BLACK); 
        g2d.setStroke(new BasicStroke(2)); 
        g2d.drawOval((int) Math.round(x), (int) Math.round(y), diametre, diametre); 
        FontMetrics fm = g.getFontMetrics();
        int stringWidth = fm.stringWidth(name) / 2;
        int stringAscent = fm.getAscent();
        int xCenter = (int) x + (diametre / 2) - stringWidth;
        int yCenter = (int) y + (diametre / 2) + stringAscent / 4;
        g.setColor(Color.BLACK);
        g.drawString(name, xCenter, yCenter);
    }

    //quand un bot a été mangé on l'enlève du jeu
    public static void remove(int ID, ArrayList<Bot> bots){
        for(Bot b : bots){
            if(b.ID==ID){
                //System.out.println(b.ID);
                bots.remove(b);
                return;
            } 
        }
    }

    //les bots se split pas seul mais quand ils rencontre un virus alors que trop gros
    public void splitBot(){
        if(this.diametre > 60){
            this.diametre /=2;
        }

    }

    //on refait un nouveau bot quand un a été mangé
    public void botMangeUpdate(int MapWidth, int MapHeight) {
        //correspond à une "réinitialisation"
        increaseCount();
        this.setID(Bot.getCount());
        this.setName("Bot "+this.getID());
        this.diametre = 50;
        Random random = new Random();
        this.setX(random.nextInt(MapWidth-100)+100);
        this.setY(random.nextInt(MapHeight-100)+100);
    }

    //getter et setters
    public int getID(){
        return ID;
    }

    public static int getCount() {
        return count;
    }

    public static void increaseCount(){
        count++;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
}
