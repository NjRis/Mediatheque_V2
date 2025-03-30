package certifications;

import model.Abonne;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class BanManagerTest {

    @Test
    void verifierBanissement_DoitBannirPourRetardMajeur() {

        Abonne abonne = new Abonne(1, "Test", LocalDate.now());
        LocalDateTime dateEmprunt = LocalDateTime.now().minusWeeks(3).minusDays(1);


        BanManager.verifierBanissement(abonne, dateEmprunt, false);


        assertTrue(BanManager.estBanni(1));
    }

    @Test
    void estBanni_DoitRetournerFauxQuandNonBanni() {
        assertFalse(BanManager.estBanni(999));
    }
}