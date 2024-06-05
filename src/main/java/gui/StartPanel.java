package gui;

import javax.swing.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//premier écran afficher quand on lance le jeu
public class StartPanel extends JPanel {
    private Image backgroundImage;
    private JButton startButton;
    private JTextField nameField;


    public StartPanel(JFrame frame, boolean isSongRunning) {
        setLayout(null);

        try {
            backgroundImage = ImageIO.read(new File("src/main/ressource/startbdGround.png"));
        } catch (IOException e) {
            e.printStackTrace();
            backgroundImage = null;
        }

        nameField = new JTextField(); 
        nameField.setBounds(340, 355, 100, 50);
        add(nameField);

        //pouvoir aller à l'écran de selection du skin
        startButton = new JButton("Start");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String playerName = nameField.getText().isEmpty() ? "invite000" : nameField.getText();
                frame.setContentPane(new ChooseColor(frame, playerName));
                frame.invalidate();
                frame.validate();
            }
        });
        startButton.setBounds(460, 355, 100, 45);
        add(startButton);

        //on vérifie si le son n'est pas déjà lancé pour ne pas le relancer par dessus le précédent
        if (isSongRunning == false){
            SoundManager song = new SoundManager();
            song.playSong();
        }

        //pour aller dans les réglages du son
        JButton settingsButton = new JButton("Settings");
        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setContentPane(new Settings(frame,true)); //ici il devient true s'il ne l'était pas ou le reste car chanson toujours en cours
                frame.invalidate();
                frame.validate();
            }
        });
        settingsButton.setBounds(750, 500, 100, 45);
        add(settingsButton);


    }

    //affichage
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
        }
        // message "but du jeu"
        String message = "Soyez le plus gourmand";
        Font font = new Font("Arial", Font.ITALIC, 30);
        FontMetrics fontMetrics = g.getFontMetrics(font);
        int messageWidth = fontMetrics.stringWidth(message);
        int x = (this.getWidth() - messageWidth) / 2;
        int y = this.getHeight() / 3 ;
        g.setFont(font);
        g.setColor(Color.BLACK);
        g.drawString(message, x, y);
    }
}
