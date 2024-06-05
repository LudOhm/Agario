package gui;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

//fichier principal pour lancer le jeu
public class Game extends JFrame{
    public Game(){
        //seulement sur windows pour l'icon du jeu
        ImageIcon iconFile = new ImageIcon("src/main/ressource/icon.png");
        setIconImage(iconFile.getImage());
        setVisible(true);
        setTitle("Agario");
        setSize(900,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setContentPane(new StartPanel(this,false));//pour l'instant false car le son n'est pas lancé donc il ne peut être que false
        setResizable(false); //pour ne pas changé la taille de l'écran
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(()-> {
            Game game = new Game();
            game.setVisible(true);
        });
    }
}
