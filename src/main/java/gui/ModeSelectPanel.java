package gui;

import javax.swing.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ModeSelectPanel extends JPanel {
    private Image backgroundImage;
    private JButton onlineButton, soloButton, returnButton;
    private String color;

    //écran de choix du mode de jeu
    public ModeSelectPanel(JFrame frame, String playerName,String colorSelect) {
        setLayout(null);

        try {
            backgroundImage = ImageIO.read(new File("src/main/ressource/startbdGround.png"));
        } catch (IOException e) {
            e.printStackTrace();
            backgroundImage = null;
        }
        
        // mode en ligne
        onlineButton = new JButton("Online");
        onlineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                color = colorSelect;
                frame.setContentPane(new IpPanel(frame, playerName, color));
                frame.invalidate();
                frame.validate();
            }
        });

        //jeu solo
        soloButton = new JButton("Solo");
        soloButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                color = colorSelect;
                frame.setContentPane(new GamePanel(frame, playerName,color));
                frame.invalidate();
                frame.validate();
            }
        });

        //pour retourner en arrière donc à la selection du skin du joueur
        returnButton = new JButton("Back");
        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setContentPane(new ChooseColor(frame, playerName));
                frame.invalidate();
                frame.validate();
            }
        });
        
        onlineButton.setBounds(340, 355, 100, 45);
        soloButton.setBounds(460, 355, 100, 45);
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
        }

        // message "astuce"
        String message = "CLIC GAUCHE POUR SPLIT";
        Font font = new Font("Arial", Font.BOLD, 15);
        g.setFont(font);
        g.setColor(Color.MAGENTA);
        g.drawString(message, 100, 470);
        message = "CLIC DROIT POUR LANCER UN PROJECTILE";
        g.setColor(Color.BLUE);
        g.drawString(message, 100, 490);
    }

}

