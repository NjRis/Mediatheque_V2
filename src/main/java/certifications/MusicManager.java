package certifications;

import javax.sound.sampled.*;
import java.util.Objects;

public class MusicManager {
    private static Clip clip;

    public static void jouerMusique(String fichier) {
        try {
            AudioInputStream audio = AudioSystem.getAudioInputStream(
                    Objects.requireNonNull(MusicManager.class.getClassLoader().getResourceAsStream("./resources/attente.wav"))
            );
            clip = AudioSystem.getClip();
            clip.open(audio);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            System.err.println("Erreur musique : " + e.getMessage());
        }
    }

    public static void arreterMusique() {
        if (clip != null) clip.stop();
    }

    public static void setClip(Clip clipMock) {
        clip = clipMock;
    }
}