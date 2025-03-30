package model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class AbonneTest {

    @Test
    void calculerAge_DoitRetourner20_AvecDateNaissance2003() {
        Abonne abonne = new Abonne(1, "Test", LocalDate.of(2003, 1, 1));
        assertEquals(LocalDate.now().getYear() - 2003, abonne.calculerAge());
    }

    @Test
    void getNumero_DoitRetourner5() {
        Abonne abonne = new Abonne(5, "Test", LocalDate.now());
        assertEquals(5, abonne.getNumero());
    }
}