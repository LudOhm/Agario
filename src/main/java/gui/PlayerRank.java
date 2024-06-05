package gui;

import controllers.Ball;
import managers.Bot;

import java.awt.*;

public class PlayerRank {
        private final int largeur,hauteur, x, y;
        private GamePanel gp;
        private Ball[] balles;

        public PlayerRank(GamePanel gp){
            this.gp = gp;
            hauteur = 280;
            largeur= 210;
            x= gp.getViewWidth()-largeur-10;
            y= 10;
            balles = gp.getArrayRank();
        }

        //affichage en jeu
        public void draw(Graphics g){
            g.setColor(new Color(0,0,0, 127));
            g.fillRect(x,y,largeur,hauteur);
            g.setColor(new Color(255,255,255));
            g.setFont(new Font("arial",Font.BOLD, 26));
            g.drawString("Leaderboard", this.x+30,  this.y+30);
            //afficher les 10 premiers puis le joueur
            g.setFont(new Font("arial",Font.ITALIC, 16));
            boolean playerInTop10 = false;
            int xText = this.x + 15;
            int yText = this.y + 55;
            for(int i=0; i<10; i++){
                if(balles[i] != null){
                    if(!(balles[i] instanceof Bot)){
                        g.setColor(new Color(222,45,100));
                        playerInTop10 = true;
                    }else{
                        g.setColor(new Color(255,255,255));
                    }
                    String s = String.valueOf(i+1)+".\t"+balles[i].getName();
                    g.drawString(s,xText,yText);
                    yText+=22;
                }

            }
            if(!playerInTop10){
                g.setColor(new Color(222,45,100));
                String s = gp.getBall().getName();
                g.drawString(s,xText,yText);
            }
        }

}
