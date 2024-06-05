package gui;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//quand le joueur perd il arrive sur cet écran
public class GameOver extends JPanel {
    private Image backgroundImage;
    private JButton onlineButton, soloButton, returnButton;

    public GameOver(JFrame frame, String playerName,String colorSelect) {
        setLayout(null);

        try {
            backgroundImage = ImageIO.read(new File("src/main/ressource/fond1.jpeg"));
        } catch (IOException e) {
            e.printStackTrace();
            backgroundImage = null;
        }

        //jouer en online
        onlineButton = new JButton("Online");
        onlineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setContentPane(new IpPanel(frame, playerName, colorSelect));
                frame.invalidate();
                frame.validate();
            }
        });

        //recomencer en solo
        soloButton = new JButton("Restart Solo");
        soloButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setContentPane(new GamePanel(frame, playerName,colorSelect));
                frame.invalidate();
                frame.validate();
            }
        });

        //retour au début du jeu
        returnButton = new JButton("Back");
        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setContentPane(new StartPanel(frame, true)); //toujours true acar la chanson a forcément été lancé avant
                frame.invalidate();
                frame.validate();
            }
        });
        
        onlineButton.setBounds(340, 350, 100, 45);
        soloButton.setBounds(460, 350, 100, 45);
        returnButton.setBounds(600, 460, 100, 45);
        add(onlineButton);
        add(soloButton);
        add(returnButton);
    }

    //affichage
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
            
            // Ajoute le message "Défaite" au milieu de l'écran
            String message = "Game Over";
            Font font = new Font("Arial", Font.BOLD, 80);
            FontMetrics fontMetrics = g.getFontMetrics(font);
            int messageWidth = fontMetrics.stringWidth(message);
            int x = (this.getWidth() - messageWidth) / 2;
            int y = this.getHeight() / 2 ;
            g.setFont(font);
            g.setColor(Color.BLACK);
            g.drawString(message, x, y);


        }

        
    }
}

