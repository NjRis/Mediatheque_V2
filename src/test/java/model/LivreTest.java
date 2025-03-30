package model;

import exceptions.ReservationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class LivreTest {

    private Livre livre;
    private Abonne abonne;

    @BeforeEach
    void setUp() {
        livre = new Livre(1, "Dune", 412);
        abonne = new Abonne(1, "Paul", LocalDate.now());
    }

    @Test
    void emprunter_DoitChangerEmprunteur() throws Exception {
        livre.emprunter(abonne);
        assertEquals(abonne, livre.emprunteur());
    }
}