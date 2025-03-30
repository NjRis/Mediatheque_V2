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
        DocumentManager.getInstance(); // Initialiser les données
    }

    @Test
    void traiterReservation_DoitDetecterDocumentInexistant() throws Exception {
        // Configuration
        String input = "1\n999\n";
        BufferedReader in = new BufferedReader(new StringReader(input));
        StringWriter sw = new StringWriter();
        PrintWriter out = new PrintWriter(sw);

        // Accès à la méthode privée
        Method method = ServeurApplication.class.getDeclaredMethod("traiterReservation", BufferedReader.class, PrintWriter.class);
        method.setAccessible(true);

        // Exécution
        method.invoke(null, in, out);

        // Vérification
        assertTrue(sw.toString().contains("❌ Référence document invalide"));
    }
}