package model;

import exceptions.*;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class DVDTest {

    @Test
    void reserver_DoitLeverExceptionPourMineurSurDVDAdulte() {
        DVD dvd = new DVD(1, "Film Adulte", true);
        Abonne mineur = new Abonne(2, "Enfant", LocalDate.of(2010, 1, 1));

        assertThrows(ReservationException.class, () -> dvd.reserver(mineur));
    }
}