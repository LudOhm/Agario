package gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

//écran pour mettre son propre fichier png (ne fonctionne qu'en solo)
public class SelfSkin extends JPanel {
    private Image backgroundImage;
    private JButton startButton, returnButton;
    private JLabel selectedFileLabel;
    private JButton uploadButton;
    private Image selectedImage;

    public SelfSkin(JFrame frame, String playerName){
        setLayout(null);

        try {
            backgroundImage = ImageIO.read(new File("src/main/ressource/fond1.jpeg"));
        } catch (IOException e) {
            e.printStackTrace();
            backgroundImage = null;
        }

        JLabel choose = new JLabel("upload your own skin :");
        choose.setBounds(100, 50, 200, 30);
        Font font = choose.getFont();
        choose.setFont(new Font(font.getName(), Font.BOLD, font.getSize()));
        add(choose);

        //si rien n'est choisi
        selectedFileLabel = new JLabel("No file selected");
        selectedFileLabel.setBounds(100, 100, 400, 30);
        add(selectedFileLabel);

        //bouton qui amène dans les fichier de l'appareil
        uploadButton = new JButton("Upload PNG");
        uploadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int result = fileChooser.showOpenDialog(SelfSkin.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    selectedFileLabel.setText(selectedFile.getAbsolutePath());

                    try {
                        selectedImage = ImageIO.read(selectedFile);
                        repaint();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        uploadButton.setBounds(100, 150, 150, 30);
        add(uploadButton);

        //pour aller à l'écran de selection
        startButton = new JButton("Start Game");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedFilePath = selectedFileLabel.getText();
                //si rien n'est choisi on previens et on ne peut pas lancer dans ce cas
                if(selectedFilePath.equals("No file selected")){
                    JOptionPane.showMessageDialog(frame, "No file selected", "Error", JOptionPane.ERROR_MESSAGE);
                }else{
                    frame.setContentPane(new ModeSelectPanel(frame, playerName, selectedFilePath));
                }
                frame.invalidate();
                frame.validate();
            }
        });
        startButton.setBounds(600, 400, 100, 45);
        add(startButton);

        //pour retourner en arrière donc à l'écran de choix de couleur
        returnButton = new JButton("Back");
        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setContentPane(new ChooseColor(frame,playerName));
                frame.invalidate();
                frame.validate();
            }
        });
        returnButton.setBounds(600, 460, 100, 45);
        add(returnButton);

    }


    //affichage
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
        }

        if (selectedImage != null) {
            BufferedImage bufferedImage = new BufferedImage(selectedImage.getWidth(null), selectedImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2dBuffered = bufferedImage.createGraphics();
            Shape clip = new Ellipse2D.Double(0, 0, selectedImage.getWidth(null), selectedImage.getHeight(null));
            g2dBuffered.setClip(clip);
            g2dBuffered.drawImage(selectedImage, 0, 0, null);
            g.drawImage(bufferedImage, 300, 200, 200, 200, null);

            g2dBuffered.dispose();
        }
    }

}
