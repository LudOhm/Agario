package gui;

import javax.swing.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//réglage pour le son du jeu
public class Settings extends JPanel {

    private JButton plusButton, minusButton, returnButton;
    private JProgressBar volumeBar;
    private Image backgroundImage;

    public Settings(JFrame frame, boolean isSongRunning){
        setLayout(null);

        try {
            backgroundImage = ImageIO.read(new File("src/main/ressource/fond1.jpeg"));
        } catch (IOException e) {
            e.printStackTrace();
            backgroundImage = null;
        }

        volumeBar = new JProgressBar(0, 100);
        volumeBar.setValue(SoundManager.getVolumeMultiplicateur() * 20); 

        //pour augmenter le son
        plusButton = new JButton("+");
        plusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SoundManager.addVolumeMultiplicateur();
                updateVolumeBar();
            }
        });

        //pour baisser le son
        minusButton = new JButton("-");
        minusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SoundManager.removeVolumeMultiplicateur();
                updateVolumeBar();
            }
        });

        //pour revenir à l'écran d'accueil
        returnButton = new JButton("Back");
        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setContentPane(new StartPanel(frame, isSongRunning)); //normalement toujours true ici car deja été lancé avant
                frame.invalidate();
                frame.validate();
            }
        });

        plusButton.setBounds(620, 250, 50, 30);
        minusButton.setBounds(200, 250, 50, 30);
        returnButton.setBounds(400, 400, 100, 45);
        volumeBar.setBounds(300, 250, 300, 30); 
        add(plusButton);
        add(minusButton);
        add(returnButton);
        add(volumeBar);
    }

    //met à jour la barre de volume
    private void updateVolumeBar() {
        int volume = SoundManager.getVolumeMultiplicateur() * 20;
        volumeBar.setValue(volume);
    }

    //affichage
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
            
            String message = "Volume setting";
            Font font = new Font("Arial", Font.BOLD, 60);
            FontMetrics fontMetrics = g.getFontMetrics(font);
            int messageWidth = fontMetrics.stringWidth(message);
            int x = (this.getWidth() - messageWidth) / 2;
            int y = this.getHeight() / 4; // Mettre le titre au-dessus de la moitié
            g.setFont(font);
            g.setColor(Color.BLACK);
            g.drawString(message, x, y);
        }   
    }
}
