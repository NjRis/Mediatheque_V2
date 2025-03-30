package certifications;

import org.junit.jupiter.api.Test;
import javax.sound.sampled.Clip;
import static org.mockito.Mockito.*;

class MusicManagerTest {

    @Test
    void jouerMusique_DoitOuvrirLeClip() throws Exception {
        // Configuration
        Clip clipMock = mock(Clip.class);
        MusicManager.setClip(clipMock);

        // Exécution
        MusicManager.jouerMusique("test.wav");

        // Vérification
        verify(clipMock).open(any());
        verify(clipMock).loop(Clip.LOOP_CONTINUOUSLY);
    }
}