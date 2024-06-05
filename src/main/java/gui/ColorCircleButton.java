package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;


//pour faire les boutons rond dans le ChooseColor
public class ColorCircleButton extends JButton {
    private Color color;

    public ColorCircleButton(Color color) {
        this.color = color;
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.fill(new Ellipse2D.Double(0, 0, getWidth(), getHeight()));
        g2.dispose();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(50, 50);
    }

    //getter
    public Color getColor() {
        return color;
    }
    
}

