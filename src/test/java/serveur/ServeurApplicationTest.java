package serveur;

import manager.DocumentManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.lang.reflect.Method;
import static org.junit.jupiter.api.Assertions.*;

class ServeurApplicationTest {

    @BeforeAll
    static void init() {
        DocumentManager.getInstance();
    }

    @Test
    void traiterReservation_DoitDetecterDocumentInexistant() throws Exception {

        String input = "1\n"
                + "999\n";
        BufferedReader in = new BufferedReader(new StringReader(input));
        StringWriter sw = new StringWriter();
        PrintWriter out = new PrintWriter(sw);


        DocumentManager.getInstance();


        Method method = ServeurApplication.class.getDeclaredMethod("traiterReservation", BufferedReader.class, PrintWriter.class);
        method.setAccessible(true);
        method.invoke(null, in, out);


        String output = sw.toString();
        assertTrue(output.contains("( ¬_¬) Référence document invalide"),
                "Message d'erreur absent. Sortie réelle : " + output);
    }
}