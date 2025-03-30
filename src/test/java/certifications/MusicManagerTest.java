package certifications;

import org.junit.jupiter.api.Test;
import javax.sound.sampled.Clip;
import static org.mockito.Mockito.*;

class MusicManagerTest {

    @Test
    void jouerMusique_DoitOuvrirLeClip() throws Exception {

        Clip clipMock = mock(Clip.class);
        MusicManager.setClip(clipMock);


        MusicManager.jouerMusique("test.wav");


        verify(clipMock).open(any());
        verify(clipMock).loop(Clip.LOOP_CONTINUOUSLY);

        //marche pas lol
    }
}