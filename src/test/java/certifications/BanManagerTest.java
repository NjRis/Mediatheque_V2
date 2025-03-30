package certifications;

import model.Abonne;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class BanManagerTest {

    @Test
    void verifierBanissement_DoitBannirPourRetardMajeur() {
        // Configuration
        Abonne abonne = new Abonne(1, "Test", LocalDate.now());
        LocalDateTime dateEmprunt = LocalDateTime.now().minusWeeks(3).minusDays(1);

        // Exécution
        BanManager.verifierBanissement(abonne, dateEmprunt, false);

        // Vérification
        assertTrue(BanManager.estBanni(1));
    }

    @Test
    void estBanni_DoitRetournerFauxQuandNonBanni() {
        assertFalse(BanManager.estBanni(999));
    }
}