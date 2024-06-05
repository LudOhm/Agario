package gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class ChooseColor extends JPanel {
    private Image backgroundImage;
    private Color colorSelect = Color.RED;
    private JButton startButton, returnButton;
    private Color[] colors = { Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.ORANGE, Color.PINK, Color.CYAN,
            Color.MAGENTA };

    private String color;

    //constructeur 
    public ChooseColor(JFrame frame, String playerName) {
        setLayout(null);

        try {
            backgroundImage = ImageIO.read(new File("src/main/ressource/fond1.jpeg"));
        } catch (IOException e) {
            e.printStackTrace();
            backgroundImage = null;
        }

        JLabel choose = new JLabel("Choose your skin :");
        choose.setBounds(100, 50, 200, 30);
        Font font = choose.getFont();
        choose.setFont(new Font(font.getName(), Font.BOLD, font.getSize()));
        add(choose);

        int buttonSize = 60;
        int x = 100, y = 100;
        int buttonSpacing = 30;
        int column = 0;

        //mettre tous les boutons des différentes couleurs, appel a un autre fichier
        for (Color color : colors) {
            ColorCircleButton colorButton = new ColorCircleButton(color);
            int xPos = x + (column % 2) * (buttonSize + buttonSpacing);
            int yPos = y + (column / 2) * (buttonSize + buttonSpacing);
            colorButton.setBounds(xPos, yPos, buttonSize, buttonSize);
            colorButton.setPreferredSize(new Dimension(buttonSize, buttonSize));
            colorButton.addActionListener(this::colorButtonActionPerformed);
            add(colorButton);

            column++;
            if (column % 2 == 0)
                yPos += buttonSize;
        }

        //aller a l'endroit pour mettre son fichier 
        JButton selfSkinButton = new JButton("Upload Skin");
        selfSkinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setContentPane(new SelfSkin(frame, playerName));
                frame.invalidate();
                frame.validate();
            }
        });
        selfSkinButton.setBounds(600, 340, 100, 45); // position ajusté
        add(selfSkinButton);

        //commencer le jeu
        startButton = new JButton("Start Game");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                color = colorToHex(colorSelect);
                frame.setContentPane(new ModeSelectPanel(frame, playerName, color));
                frame.invalidate();
                frame.validate();
            }
        });
        startButton.setBounds(600, 400, 100, 45);
        add(startButton);

        //revenir en arrière
        returnButton = new JButton("Back");
        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setContentPane(new StartPanel(frame, true)); //toujours true car la chanson a forcément été lancé avant
                frame.invalidate();
                frame.validate();
            }
        });
        returnButton.setBounds(600, 460, 100, 45);
        add(returnButton);

    }

    //changer la couleur selon le bouton
    private void colorButtonActionPerformed(ActionEvent e) {
        ColorCircleButton button = (ColorCircleButton) e.getSource();
        colorSelect = button.getColor();
        repaint();
    }

    //affichage à l'écran
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //les couleurs sous la forme qu'elles auront dans le jeu 
        int circleDiameter = 200;
        int circleX = getWidth() - circleDiameter - 350;
        int circleY = (getHeight() - circleDiameter) / 2;
        g2.setColor(colorSelect);
        g2.fillOval(circleX, circleY, circleDiameter, circleDiameter);
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(5));
        g2.drawOval(circleX, circleY, circleDiameter, circleDiameter);
        Color backgroundColor = new Color(112, 128, 144, 123);
        g2.setColor(backgroundColor);

        int backgroundPadding = 10;
        int buttonSize = 60;
        int buttonSpacing = 30;
        int columns = 2;
        int rows = (int) Math.ceil((double) colors.length / columns);
        int backgroundWidth = columns * buttonSize + (columns - 1) * buttonSpacing + 2 * backgroundPadding;
        int backgroundHeight = rows * buttonSize + (rows - 1) * buttonSpacing + 2 * backgroundPadding;

        int arcWidth = 20;
        int arcHeight = 20;
        g2.fillRoundRect(100 - backgroundPadding, 100 - backgroundPadding, backgroundWidth, backgroundHeight, arcWidth,
                arcHeight);

        g2.setColor(Color.BLACK);
        Stroke oldStroke = g2.getStroke();
        g2.setStroke(new BasicStroke(2f));

        g2.drawRoundRect(100 - backgroundPadding, 100 - backgroundPadding, backgroundWidth, backgroundHeight, arcWidth,
                arcHeight);

        g2.setStroke(oldStroke); 
        g2.dispose();
    }

    //pour aovir la couleur en hexadecimal
    public static String colorToHex(Color color) {
        String hex = String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
        return hex;
    }

    //getters
    public Color getColorSelect() {
        return colorSelect;
    }

}
