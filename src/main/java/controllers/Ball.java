package controllers;

import models.BilleState;
import managers.BillesGestion;
import models.Cercle;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
import java.awt.BasicStroke;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Random;
import java.awt.Font;
import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

public class Ball extends Cercle {

    protected Color color;
    protected double speed = 5;
    protected String name;
    private static final Random rand = new Random();
    //pour gérer le comportement avec le virus et éviter que split soit trop appelée
    private boolean infectée = false;
    private int contaminePar=-1;
    protected String imgPathStr;
    protected File imagePath ; 

    // ensemble de balles dans le cas du split
    private ArrayList<Ball> balls = new ArrayList<>();


    private static final Color[] colors = {
        Color.RED, Color.GREEN, Color.BLUE, 
        Color.YELLOW, Color.ORANGE, Color.PINK, Color.CYAN
    };

    //constructeur de base sans les coordonées
    public Ball(int x, int y, int diametre, String color, String name) {
        super(x, y, diametre);

        // si c'est bien une couleur
        if (color.startsWith("#")) {
            this.color = hexToColor(color);
        } else {
            this.imgPathStr = color;
            this.imagePath = new File(color);
        }
        this.name = name;
        balls.add(this);
    }

    //ajouter que colorSelect peut etre un chemin
    public Ball(int diametre, String name, int mapWidth, int mapHeight,String colorSelect) {
        super(rand.nextInt(mapWidth - diametre), 
              rand.nextInt(mapHeight - diametre), diametre);

        if (colorSelect == null || colorSelect.isEmpty()) {
            this.color = getRandomColor();
        } else {
            // si c'est une couleur
            if (colorSelect.startsWith("#")) {
                this.color = hexToColor(colorSelect);
            } else {
                this.imgPathStr = colorSelect;
                this.imagePath = new File(colorSelect);
            }
        }
        this.name = name;
    }

    //on le garde pour les bots
    public Ball(int diametre, String name, int mapWidth, int mapHeight) {
        super(rand.nextInt(mapWidth - diametre), 
              rand.nextInt(mapHeight - diametre), diametre);
        this.color = getRandomColor();
        this.name = name;
        balls.add(this);
    }

    public void moveTowards(int targetX, int targetY, int gameWidth, int gameHeight) {
        double angle = Math.atan2(targetY - y, targetX - x);
        x += speed * Math.cos(angle);
        y += speed * Math.sin(angle);

        // Vérifiez les limites pour empêcher la Ball de sortir de la zone du jeu.
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (x > gameWidth - diametre) x = gameWidth - diametre;
        if (y > gameHeight - diametre) y = gameHeight - diametre;

        //System.out.println("x : " + x + " y : " + y);
    }

    private double lerpFactor = 0.005; //Facteur d'interpolation, contrôle la fluidité du mouvement

    public void moveTo(int targetX, int targetY) {
        this.x += (targetX - this.x) * lerpFactor;
        this.y += (targetY - this.y) * lerpFactor;
    }

    public static Color getRandomColor() {
        Random random = new Random();
        return colors[random.nextInt(colors.length)];
    }
    
    public void draw(Graphics g) {

        //si ce n'est pas avec un png
        if (imagePath == null) {
            g.setColor(color);
            // Convert x and y to int values by casting
            g.fillOval((int) Math.round(x), (int) Math.round(y), diametre, diametre); 
            // Contour en noir
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

            g2d.dispose();

        //avec l'image png en skin
        }else {
            try {
                Image image = ImageIO.read(imagePath);
                BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2dBuffered = bufferedImage.createGraphics();
                // pour avoir un ercle mais si image pas cercle
                Shape clip = new Ellipse2D.Double(0, 0, image.getWidth(null), image.getHeight(null));
                g2dBuffered.setClip(clip);
                g2dBuffered.drawImage(image, 0, 0, null);
                g.drawImage(bufferedImage, (int) Math.round(x), (int) Math.round(y), diametre, diametre, null);

                g2dBuffered.dispose();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Graphics2D g2d = (Graphics2D) g.create(); 
            g2d.setColor(Color.BLACK); 
            g2d.setStroke(new BasicStroke(2)); 
            g2d.drawOval((int) Math.round(x), (int) Math.round(y), diametre, diametre);


            Font boldFont = new Font(g.getFont().getName(), Font.BOLD, g.getFont().getSize());
            g.setFont(boldFont);
            FontMetrics fm = g.getFontMetrics();
            int stringWidth = fm.stringWidth(name) / 2;
            int stringAscent = fm.getAscent();
            int xCenter = (int) x + (diametre / 2) - stringWidth;
            int yCenter = (int) y + (diametre / 2) + stringAscent / 4;
            g.setColor(Color.BLACK);
            g.drawString(name, xCenter, yCenter);

            g2d.dispose();
        }

    }

    //fonctionnalité split
    public ArrayList<Ball> split() {
        if(balls.isEmpty()) balls.add(this);
        //si la balle est deja split elle peut avoir encore plus de balle qu'une seule donc nouvelle liste
        ArrayList<Ball> enfants = new ArrayList<>();
        for (Ball currentBall : balls) {
            if(currentBall.diametre > 60){
                currentBall.diametre /= 2;
                //on ajoute tous les enfants dans la liste de la balle du joueur
                if(color!=null){
                    enfants.add(new Ball(currentBall.getX(), currentBall.getY(), currentBall.getDiameter(), currentBall.getColorStr(), currentBall.getName()));
                }else{
                    //si image png
                    enfants.add(new Ball(currentBall.getX(), currentBall.getY(), currentBall.getDiameter(), currentBall.getImagePath(), currentBall.getName()));
                }
            }


        }
        balls.addAll(enfants);
        return enfants;
    }

    public void grossit(int calories){
        this.diametre += calories;
        aireRayonUpdate();
        reinitialiseInfection();
    }

    //quand on mange un autre joueur
    public void mange(Ball nourriture){
        grossit(nourriture.getDiameter());
        slowDown();
    }

    //quand on mange une bille de la map
    public void mange(BilleState nourriture){
        grossit(nourriture.getApportNutritionnel());
        slowDown();
    }


    //fonctionnalité projectile
    public void lanceProjectile(int mouseX, int mouseY, BillesGestion bg){
        diametre -= 4; // correspond à la valeur de l'apport nutritionnel d'une bille

        //Securité en plus mais vérifier si vraiment utile
        if(this.color ==null) this.color=Color.BLUE;
        String colorProj = colorToHex(this.color);
        BilleState projectile = new BilleState((int) x, (int) y, colorProj);

        // Détermine la direction du mouvement de la balle (par exemple, angle de la dernière direction)
        double angle = Math.atan2(mouseY - y, mouseX - x);

        // Déplace le projectile en avant (ajuste les coordonnées en fonction de la direction)
        double projectileSpeed = 10; // Vous pouvez ajuster la vitesse du projectile selon vos besoins
        projectile.setX((int) (x + diametre * Math.cos(angle) + projectileSpeed * Math.cos(angle)));
        projectile.setY((int) (y + diametre * Math.sin(angle) + projectileSpeed * Math.sin(angle)));

        // Ajoute le projectile à la liste des billes
        bg.ajouterBille(projectile);
        //Accélération une fois poids retiré
        speedUp();

    }

    public void reinitialiseInfection(){
        infectée = false;
        contaminePar = -1;
    }

    //quand un joueur ou une bille est mangé on va moins vite
    public void slowDown() {
        if(this.speed > 3) this.speed -= 0.2;
        else if (this.speed <= 3 && this.speed > 1) this.speed -= 0.1;
        else this.speed = 0.5;
    }

    //quand on lance des projectile ou quand on split le joueur gagne de la vitesse
    public void speedUp(){
        if(this.speed < 5){
            if(this.speed > 3) this.speed += 0.2;
            else if (this.speed <= 3 && this.speed > 1) this.speed += 0.1;
            else this.speed += 0.5;
        }
    }

    //setters
    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setContaminePar(int contaminePar) {
        this.contaminePar = contaminePar;
    }

    public void setInfectée(boolean infectée) {
        this.infectée = infectée;
    }

    //getters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Color getColor() {
        if (color != null) return color;
        return Color.BLUE; 
    }

    public String getColorStr() {
        return colorToHex(color); 
    }

    public String getImagePath(){
        return imgPathStr;
    }

        public boolean isInfectée() {
        return infectée;
    }

    public int getContaminePar() {
        return contaminePar;
    }


    public ArrayList<Ball> getBalls() {
        return balls;
    }

    //pour avoir une couleur depuis un String en hexadecimal
    public static Color hexToColor(String hex) {
        try {
            return Color.decode(hex);
        } catch (NumberFormatException e) {
            // si l'hexadecimal n'est pas valide
            System.err.println("Error: Invalid hexadecimal color string. Returning default color (black).");
            return Color.BLACK; // retourne la couleur noire de base dans ce cas
        }
    }

    //pour convertir d'une couleur a un hexadecimal
    public static String colorToHex(Color color) {
        String hex = String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
        return hex;
    }
}
