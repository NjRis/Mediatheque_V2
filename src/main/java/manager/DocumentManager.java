package manager;

import model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class DocumentManager {
    private static DocumentManager instance;
    private final Map<Integer, Abonne> abonnes = new HashMap<>();
    private final Map<Integer, Document> documents = new HashMap<>();
    private final Map<Integer, LocalDateTime> reservations = new HashMap<>();

    private DocumentManager() {
        documents.put(1, new Livre(1, "1984", 328));
        documents.put(2, new DVD(2, "Matrix", true));
        abonnes.put(1, new Abonne(1, "Alice", LocalDate.of(1990, 5, 15)));
        abonnes.put(2, new Abonne(2, "Bob", LocalDate.of(2005, 3, 22)));
    }

    public LocalDateTime getDateFinReservation(int numDoc) {
        return reservations.get(numDoc);
    }

    public void ajouterReservation(int numDoc) {
        reservations.put(numDoc, LocalDateTime.now().plusHours(1));
    }

    public static synchronized DocumentManager getInstance() {
        if (instance == null) {
            instance = new DocumentManager();
        }
        return instance;
    }

    public Document getDocument(int numero) {
        return documents.get(numero);
    }
    private final Map<Integer, Boolean> alertes = new HashMap<>();

    public boolean hasAlerte(int numeroDoc) {
        return alertes.containsKey(numeroDoc);
    }

    public void ajouterAlerte(int numeroDoc) {
        alertes.put(numeroDoc, true);
    }

    public void supprimerAlerte(int numeroDoc) {
        alertes.remove(numeroDoc);
    }
    public Abonne getAbonne(int numero) {
        // À implémenter selon votre gestion des abonnés
        return abonnes.get(numero);
    }

    public void supprimerReservation(int numDoc) {
        Document doc = documents.get(numDoc);
        if (doc != null) doc.retourner(false);
    }


}
