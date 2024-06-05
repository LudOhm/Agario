package gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.IOException;

import java.util.regex.Pattern;

public class IpPanel extends JPanel {
    private Image backgroundImage;
    private JTextField ipTextField;
    private JButton connectButton, backButton;

    private static final String IPV4_PATTERN = 
        "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
    private static final Pattern ipv4Pattern = Pattern.compile(IPV4_PATTERN);


    //écran pour mettre l'adresse ip du serveur où on veut se connecter
    public IpPanel(JFrame frame, String playerName, String colorSelect) {
        setLayout(null);

        try {
            backgroundImage = ImageIO.read(new File("src/main/ressource/fond1.jpeg"));
        } catch (IOException e) {
            e.printStackTrace();
            backgroundImage = null;
        }

        ipTextField = new JTextField("Enter IP Address");
        ipTextField.setBounds(340, 250, 200, 50);
        //pour que le texte ne s'affiche pas lorsqu'on clique sur la cellule
        ipTextField.addFocusListener(new FocusListener() {
            //si on clique sur la boite de texte il s'enlève tout seul
            @Override
            public void focusGained(FocusEvent e) {
                if (ipTextField.getText().equals("Enter IP Address")) {
                    ipTextField.setText("");
                }
            }
            //sinon le texte revient si rien n'est marqué
            @Override
            public void focusLost(FocusEvent e) {
                if (ipTextField.getText().isEmpty()) {
                    ipTextField.setText("Enter IP Address");
                }
            }
        });
        add(ipTextField);

        //pour se connecter au jeu online
        connectButton = new JButton("Connect");
        connectButton.setBounds(400, 350, 100, 45);
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ipAddress = ipTextField.getText();
                //on verifie qu'une addresse a été mise
                if(ipAddress.equals("Enter IP Address")){
                    String errString = "There is no IP Address";
                    JOptionPane.showMessageDialog(frame.getContentPane(), errString,"Error",
                            JOptionPane.INFORMATION_MESSAGE);
                //verifie si le format est bien une addresse ip
                }else if(isValidIPv4(ipAddress)){
                    OnlineGamePanel onlineGamePanel = new OnlineGamePanel(frame, playerName, colorSelect, 24935, ipAddress);
                    frame.setContentPane(onlineGamePanel);
                    frame.invalidate();
                    frame.validate();
                }else{
                    String errString = "IP Address invalid";
                    JOptionPane.showMessageDialog(frame.getContentPane(), errString,"Error",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        add(connectButton);

        //retour à l'écran de selection
        backButton = new JButton("Back");
        backButton.setBounds(600, 460, 100, 45);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setContentPane(new ModeSelectPanel(frame, playerName, colorSelect));
                frame.invalidate();
                frame.validate();
            }
        });
        add(backButton);
    }

    //verifier une addresse ip valide ou non 
    public static boolean isValidIPv4(String ip) {
        return ipv4Pattern.matcher(ip).matches();
    }

    //affichage
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
        }
    }

}
