package serveur;

import certifications.BanManager;
import certifications.MusicManager;
import exceptions.EmpruntException;
import exceptions.ReservationException;
import manager.DocumentManager;
import model.Abonne;
import model.DVD;
import model.Document;
import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class ServeurApplication {

    public static void main(String[] args) {
        demarrerServeur(2000, "Réservation");
        demarrerServeur(3000, "Emprunt");
        demarrerServeur(4000, "Retour");
    }

    private static void demarrerServeur(int port, String nomService) {
        new Thread(() -> {
            try (ServerSocket server = new ServerSocket(port)) {
                System.out.println("🟢 Service " + nomService + " actif sur port " + port);
                while (true) {
                    Socket client = server.accept();
                    new Thread(() -> traiterClient(client, port)).start();
                }
            } catch (IOException e) {
                System.err.println("🔴 Erreur " + nomService + " : " + e.getMessage());
            }
        }).start();
    }

    private static void traiterClient(Socket client, int port) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
             PrintWriter out = new PrintWriter(client.getOutputStream(), true)) {

            switch(port) {
                case 2000: traiterReservation(in, out); break;
                case 3000: traiterEmprunt(in, out); break;
                case 4000: traiterRetour(in, out); break;
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Erreur client: " + e.getMessage());
        }
    }

    private static void traiterReservation(BufferedReader in, PrintWriter out) throws IOException, InterruptedException {
        try {
            // Lecture et validation du numéro d'abonné
            out.println("Entrez votre numéro d'abonné :");
            int numAbonne = lireEntier(in, out);
            if (numAbonne == -1) return;

            // Vérification existence abonné
            Abonne abonne = DocumentManager.getInstance().getAbonne(numAbonne);
            if (abonne == null) {
                out.println("❌ Aucun abonné trouvé avec ce numéro");
                return;
            }

            // Lecture et validation du document
            out.println("Entrez le numéro du document :");
            int numDoc = lireEntier(in, out);
            if (numDoc == -1) return;

            Document doc = DocumentManager.getInstance().getDocument(numDoc);
            if (doc == null) {
                out.println("❌ Référence document invalide");
                return;
            }

            // Bloc synchronisé pour accès thread-safe
            synchronized(doc) {
                if (doc.reserveur() == null) {
                    try {
                        doc.reserver(abonne);
                        DocumentManager.getInstance().ajouterReservation(numDoc);
                        out.println("✅ Réservé jusqu'à " +
                                LocalDateTime.now().plusHours(1).format(DateTimeFormatter.ofPattern("HH:mm")));
                    } catch (ReservationException e) {
                        out.println("❌ Échec : " + e.getMessage());
                    }
                } else {
                    LocalDateTime finReservation = DocumentManager.getInstance().getDateFinReservation(numDoc);
                    long tempsRestant = ChronoUnit.SECONDS.between(LocalDateTime.now(), finReservation);

                    if (tempsRestant <= 60) {
                        out.println("🎵 Démarrage musique d'attente...");
                        MusicManager.jouerMusique("attente.wav");

                        try {
                            while (tempsRestant-- > 0 && doc.reserveur() != null) {
                                Thread.sleep(1000);
                            }
                        } finally {
                            MusicManager.arreterMusique();
                        }

                        if (doc.reserveur() == null) {
                            doc.reserver(abonne);
                            out.println("✅ Réservation acquise après attente !");
                        } else {
                            out.println("⏳ Le document a été récupéré par un autre utilisateur");
                        }
                    } else {
                        out.println("⌛ Temps restant trop important : " + tempsRestant + " secondes");
                    }
                }
            }

        } catch (NumberFormatException e) {
            out.println("❌ Format numérique invalide : uniquement des chiffres !");
        } catch (Exception e) {
            out.println("⚠️ Erreur système - Veuillez réessayer");
            System.err.println("Erreur réservation : " + e.getMessage());
        }
    }

    // Méthode helper pour la lecture sécurisée
    private static int lireEntier(BufferedReader in, PrintWriter out) throws IOException {
        try {
            return Integer.parseInt(in.readLine().trim());
        } catch (NumberFormatException e) {
            out.println("❌ Entrée invalide : veuillez saisir un nombre");
            return -1;
        }
    }

    private static void reserverDocument(Document doc, int numAbonne, PrintWriter out) {
        try {
            Abonne abonne = DocumentManager.getInstance().getAbonne(numAbonne);
            doc.reserver(abonne);
            DocumentManager.getInstance().ajouterReservation(doc.numero());
            out.println("✅ Réservé jusqu'à " + LocalDateTime.now().plusHours(1).toLocalTime());
        } catch (Exception e) {
            out.println("❌ Erreur : " + e.getMessage());
        }
    }

    // Dans ServeurApplication.java
    private static void traiterEmprunt(BufferedReader in, PrintWriter out) throws IOException {
        try {
            // 1. Lecture des informations
            out.println("Entrez votre numéro d'abonné :");
            int numAbonne = Integer.parseInt(in.readLine().trim());

            // 2. Vérification abonné
            Abonne abonne = DocumentManager.getInstance().getAbonne(numAbonne);
            if (abonne == null) {
                out.println("❌ Abonné inconnu");
                return;
            }

            // 3. Vérification bannissement
            if (BanManager.estBanni(numAbonne)) {
                out.println("⛔ Vous êtes banni jusqu'au "
                        + BanManager.getDateFinBan(numAbonne));
                return;
            }

            // 4. Sélection document
            out.println("Entrez le numéro du document :");
            int numDoc = Integer.parseInt(in.readLine().trim());
            Document doc = DocumentManager.getInstance().getDocument(numDoc);

            // 5. Vérifications document
            if (doc == null) {
                out.println("❌ Document introuvable");
                return;
            }
            if (doc.emprunteur() != null) {
                out.println("⏳ Document déjà emprunté");
                return;
            }
            if (doc.reserveur() != null && !doc.reserveur().equals(abonne)) {
                out.println("🔒 Réservé par abonné #" + doc.reserveur().getNumero());
                return;
            }

            // 6. Vérification âge pour DVD
            if (doc instanceof DVD && ((DVD) doc).isAdulte()
                    && abonne.calculerAge() < 16) {
                out.println("🔞 Accès refusé - Réservé aux +16 ans");
                return;
            }

            // 7. Validation emprunt
            doc.emprunter(abonne);
            out.println("✅ Emprunt réussi !");
            if (doc.reserveur() != null) {
                DocumentManager.getInstance().supprimerReservation(numDoc);
            }

        } catch (NumberFormatException e) {
            out.println("❌ Format numérique invalide");
        } catch (EmpruntException e) {
            out.println("⚠️ Erreur : " + e.getMessage());
        }
    }

    private static void traiterRetour(BufferedReader in, PrintWriter out) throws IOException {
        try {
            // 1. Lecture document
            out.println("Entrez le numéro du document :");
            int numDoc = Integer.parseInt(in.readLine());
            Document doc = DocumentManager.getInstance().getDocument(numDoc);

            // 2. Vérification existence
            if (doc == null) {
                out.println("❌ Document inconnu");
                return;
            }

            // 3. Vérification état
            out.println("Le document est-il endommagé ? (oui/non)");
            boolean estEndommage = in.readLine().equalsIgnoreCase("oui");

            // 4. Traitement retour
            doc.retourner(estEndommage);
            out.println("✅ Retour enregistré !");

            // 5. Notification dégradation
            if (estEndommage) {
                out.println("📢 Un modérateur contrôlera le document");
            }

        } catch (NumberFormatException e) {
            out.println("❌ Format numérique invalide");
        }
    }
}