package gui;

import java.io.IOException;
import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

//pour gérer le son du jeu
public class SoundManager {
    private static FloatControl fc;
    private static float volume = -30f;
    private static int volumeMulitplicateur = 1;
    //les deux fonctions appeler par l'écran de réglage
    public static void addVolumeMultiplicateur() {volumeMulitplicateur++; checkVolume();}
    public static void removeVolumeMultiplicateur() {volumeMulitplicateur--; checkVolume();}

    public void playSong() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("src/main/ressource/ingame.wav"));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            fc = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
            checkVolume();
            clip.start();
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }

    //retourne le multiplicateur de volume actuel
    public static int getVolumeMultiplicateur() {
        return volumeMulitplicateur;
    }

    //quand on change le volume dans les paramètre
    private static void checkVolume(){
        switch (volumeMulitplicateur) {
            case 0:
                volume = -50f;
                break;
            case 1:
                volume = -30f;
                break;
            case 2:
                volume = -20f;
                break;
            case 3:
                volume = -10f;
                break;
            case 4:
                volume = -5f;
                break;
            case 5 :
                volume = 1f;
                break;
            default:
            volumeMulitplicateur = Math.max(0, Math.min(volumeMulitplicateur, 5));
            volume = fc.getValue();
        }
        fc.setValue(volume);
    }
}

