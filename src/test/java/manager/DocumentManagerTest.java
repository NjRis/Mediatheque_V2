package manager;

import model.Document;
import model.Livre;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DocumentManagerTest {

    @Test
    void getInstance_DoitRetournerSingleton() {
        DocumentManager instance1 = DocumentManager.getInstance();
        DocumentManager instance2 = DocumentManager.getInstance();
        assertSame(instance1, instance2);
    }

    @Test
    void getDocument_DoitRetournerLivre1() {
        Document doc = DocumentManager.getInstance().getDocument(1);
        assertEquals("1984", ((Livre) doc).getTitre());
    }
}