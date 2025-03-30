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
                System.out.println("˶ᵔ ᵕ ᵔ˶ Service " + nomService + " actif sur port " + port);
                while (true) {
                    Socket client = server.accept();
                    new Thread(() -> traiterClient(client, port)).start();
                }
            } catch (IOException e) {
                System.err.println("╥﹏╥ Erreur " + nomService + " : " + e.getMessage());
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

        } catch (SocketException e) {  // <-- Ajout spécifique
            System.err.println("[❋] Client déconnecté : " + e.getMessage());
        } catch (IOException | InterruptedException e) {
            System.err.println("Erreur client: " + e.getMessage());
        }
    }

    private static void traiterReservation(BufferedReader in, PrintWriter out) throws IOException, InterruptedException {


        try {
            // Lecture numéro abonné
            out.println("Entrez votre numéro d'abonné :");
            String input = in.readLine().trim();
            if ("QUIT".equalsIgnoreCase(input)) {
                System.out.println("[❋] Déconnexion client initiée");
                return; // Arrêt immédiat du traitement
            }
            int numAbonne = lireEntier(in, out);
            if (numAbonne == -1) return;


            // Vérification abonné
            Abonne abonne = DocumentManager.getInstance().getAbonne(numAbonne);
            if (abonne == null) {
                System.err.println("[( ¬_¬)] Tentative de réservation avec abonné inexistant: " + numAbonne);
                out.println("╥﹏╥ Référence document invalide");
                return;
            }

            // Lecture numéro document
            out.println("Entrez le numéro du document :");
            int numDoc = lireEntier(in, out);
            if (numDoc == -1) return;

            // Récupération document
            Document doc = DocumentManager.getInstance().getDocument(numDoc);
            if (doc == null) {
                System.err.println("[( ¬_¬)] Tentative d'accès document inexistant: " + numDoc);
                out.println("╥﹏╥ Référence document invalide");
                return;
            }

            // Bloc synchronisé
            synchronized(doc) {
                System.out.println("[✩] Traitement réservation pour document " + numDoc);

                if (doc.reserveur() == null) {
                    try {
                        // Tentative réservation
                        doc.reserver(abonne);
                        DocumentManager.getInstance().ajouterReservation(numDoc);

                        // Log succès
                        String heureFin = LocalDateTime.now().plusHours(1)
                                .format(DateTimeFormatter.ofPattern("HH:mm"));
                        System.out.println("[(⸝⸝> ᴗ•⸝⸝)] Réservation réussie - Doc " + numDoc
                                + " par abonné " + numAbonne + " jusqu'à " + heureFin);
                        out.println("(⸝⸝> ᴗ•⸝⸝) Réservé jusqu'à " + heureFin);

                    } catch (ReservationException e) {
                        System.err.println("[╥﹏╥] Échec réservation: " + e.getMessage());
                        out.println("╥﹏╥ Erreur: " + e.getMessage());
                    }
                } else {
                    // Gestion conflit
                    LocalDateTime finReservation = DocumentManager.getInstance().getDateFinReservation(numDoc);
                    long tempsRestant = ChronoUnit.SECONDS.between(LocalDateTime.now(), finReservation);

                    System.out.println("[###] Conflit résolution pour doc " + numDoc
                            + " - Temps restant: " + tempsRestant + "s");

                    if (tempsRestant <= 60) {
                        System.out.println("[~~~~] Démarrage musique pour abonné " + numAbonne);
                        MusicManager.jouerMusique("attente.wav");
                        out.println("~~~~ Attente musicale démarrée...");

                        try {
                            while (tempsRestant-- > 0 && doc.reserveur() != null) {
                                Thread.sleep(1000);
                            }
                        } finally {
                            MusicManager.arreterMusique();
                            System.out.println("[^^] Musique stoppée pour abonné " + numAbonne);
                        }

                        if (doc.reserveur() == null) {
                            doc.reserver(abonne);
                            System.out.println("[!!!] Réservation acquise après attente - Doc " + numDoc);
                            out.println("(⸝⸝> ᴗ•⸝⸝) Réservation réussie après attente !");
                        } else {
                            System.out.println("Échec attente - Doc " + numDoc + " récupéré");
                            out.println("Le document a été pris par un autre utilisateur");
                        }
                    } else {
                        System.out.println("Temps restant trop long: " + tempsRestant + "s");
                        out.println("Temps restant trop important: " + tempsRestant + "s");
                    }
                }
            }

        } catch (NumberFormatException e) {
            System.err.println("[(•ˋ _ ˊ•)] Format numérique invalide");
            out.println("╥﹏╥ Veuillez entrer uniquement des chiffres !");
        } catch (Exception e) {
            System.err.println("[╥﹏╥] Erreur critique: " + e.getMessage());
            out.println("╥﹏╥ Erreur système - Veuillez réessayer");
        }
    }

    // Méthode helper pour lecture sécurisée
    private static int lireEntier(BufferedReader in, PrintWriter out) throws IOException {


        try {
            return Integer.parseInt(in.readLine().trim());
        } catch (NumberFormatException e) {
            System.err.println("[╥﹏╥] Entrée non numérique détectée");
            out.println("╥﹏╥ Entrée invalide : nombre attendu");
            return -1;
        }
    }

    private static void reserverDocument(Document doc, int numAbonne, PrintWriter out) {
        try {
            Abonne abonne = DocumentManager.getInstance().getAbonne(numAbonne);
            doc.reserver(abonne);
            DocumentManager.getInstance().ajouterReservation(doc.numero());
            out.println("(ᵔ◡ᵔ) Réservé jusqu'à " + LocalDateTime.now().plusHours(1).toLocalTime());
        } catch (Exception e) {
            out.println("╥﹏╥ Erreur : " + e.getMessage());
        }
    }

    // Dans ServeurApplication.java
    private static void traiterEmprunt(BufferedReader in, PrintWriter out) throws IOException {


        try {
            // 1. Lecture des informations
            out.println("Entrez votre numéro d'abonné :");
            String input = in.readLine().trim();
            if ("QUIT".equalsIgnoreCase(input)) {
                System.out.println("[📡] Déconnexion client initiée");
                return; // Arrêt immédiat du traitement
            }
            int numAbonne = Integer.parseInt(in.readLine().trim());
            System.out.println("[ℹ️] Tentative d'emprunt par abonné #" + numAbonne);

            // 2. Vérification abonné
            Abonne abonne = DocumentManager.getInstance().getAbonne(numAbonne);
            if (abonne == null) {
                System.err.println("[╥﹏╥] Abonné inconnu: " + numAbonne);
                out.println("╥﹏╥ Abonné inconnu");
                return;
            }

            // 3. Vérification bannissement
            if (BanManager.estBanni(numAbonne)) {
                String dateFin = BanManager.getDateFinBan(numAbonne).format(DateTimeFormatter.ISO_DATE);
                System.err.println("[⛔] Abonné banni tenté: " + numAbonne + " jusqu'au " + dateFin);
                out.println("⛔ Vous êtes banni jusqu'au " + dateFin);
                return;
            }

            // 4. Sélection document
            out.println("Entrez le numéro du document :");
            int numDoc = Integer.parseInt(in.readLine().trim());
            System.out.println("[ℹ️] Demande emprunt doc #" + numDoc + " par abonné #" + numAbonne);

            Document doc = DocumentManager.getInstance().getDocument(numDoc);

            // 5. Vérifications document
            if (doc == null) {
                System.err.println("[╥﹏╥] Document introuvable: " + numDoc);
                out.println("╥﹏╥ Document introuvable");
                return;
            }
            if (doc.emprunteur() != null) {
                System.err.println("Document déjà emprunté: " + numDoc);
                out.println("Document déjà emprunté");
                return;
            }
            if (doc.reserveur() != null && !doc.reserveur().equals(abonne)) {
                int reserveurId = doc.reserveur().getNumero();
                System.err.println("Conflit réservation doc " + numDoc + " par abonné #" + reserveurId);
                out.println("Réservé par abonné #" + reserveurId);
                return;
            }

            // 6. Vérification âge pour DVD
            if (doc instanceof DVD && ((DVD) doc).isAdulte() && abonne.calculerAge() < 16) {
                System.err.println("[🔞] Accès refusé à doc " + numDoc + " pour abonné #" + numAbonne);
                out.println("🔞 Accès refusé - Réservé aux +16 ans");
                return;
            }

            // 7. Validation emprunt
            doc.emprunter(abonne);
            System.out.println("[(ᵔ◡ᵔ)] Emprunt réussi - Doc " + numDoc + " par abonné " + numAbonne);
            out.println("(ᵔ◡ᵔ) Emprunt réussi !");

            if (doc.reserveur() != null) {
                DocumentManager.getInstance().supprimerReservation(numDoc);
                System.out.println("[🗑️] Réservation supprimée pour doc " + numDoc);
            }

        } catch (NumberFormatException e) {
            System.err.println("[╥﹏╥] Format numérique invalide: " + e.getMessage());
            out.println("╥﹏╥ Format numérique invalide");
        } catch (EmpruntException e) {
            System.err.println("[╥﹏╥] Échec emprunt: " + e.getMessage());
            out.println("╥﹏╥ Erreur : " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erreur inattendue: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            out.println("╥﹏╥ Erreur système - Veuillez réessayer");
            e.printStackTrace();
        }
    }

    private static void traiterRetour(BufferedReader in, PrintWriter out) throws IOException {


        try {
            System.out.println("[ℹ️] Début traitement retour...");

            // 1. Lecture document
            out.println("Entrez le numéro du document :");
            String input = in.readLine().trim();
            if ("QUIT".equalsIgnoreCase(input)) {
                System.out.println("[📡] Déconnexion client initiée");
                return; // Arrêt immédiat du traitement
            }
            int numDoc = Integer.parseInt(in.readLine().trim());
            System.out.println("Document #" + numDoc + " sélectionné pour retour");

            Document doc = DocumentManager.getInstance().getDocument(numDoc);

            // 2. Vérification existence
            if (doc == null) {
                System.err.println("[(ᗒᗣᗕ)՞] Document introuvable: " + numDoc);
                out.println("(ᗒᗣᗕ)՞ Document inconnu");
                return;
            }

            // Récupération état avant modification
            Abonne ancienEmprunteur = doc.emprunteur();

            // 2b. Validation emprunteur existant
            if (ancienEmprunteur == null) {
                System.err.println("[🚨] Opération invalide : Document non emprunté");
                out.println("(ᗒᗣᗕ)՞ Ce document n'est pas actuellement emprunté");
                return;
            }

            boolean avaitAlerte = DocumentManager.getInstance().hasAlerte(numDoc);
            System.out.println("[👤] Ancien emprunteur: " + ancienEmprunteur.getNumero());

            // 3. Vérification état
            out.println("Le document est-il endommagé ? (oui/non)");
            String reponse = in.readLine().trim().toLowerCase();
            boolean estEndommage = reponse.equals("oui");
            System.out.println("[🔍] État document: " + (estEndommage ? "DÉGRADÉ" : "OK"));

            // 4. Traitement retour
            doc.retourner(estEndommage);
            out.println("˙ᴗ˙ Retour enregistré !");

            // 5. Notifications complémentaires
            if (estEndommage) {
                System.out.println("[⚠️] Dégradation signalée - Doc " + numDoc);
                out.println("📢 Un modérateur contrôlera le document");
            }

            if (avaitAlerte) {
                System.out.println("[📧] Alerte envoyée pour doc " + numDoc);
            }

            // Nettoyage réservation
            if (doc.reserveur() != null) {
                System.out.println("[🗑️] Réservation nettoyée - Doc " + numDoc);
            }

            out.flush();

        } catch (NumberFormatException e) {
            System.err.println("[╥﹏╥] Format document invalide: " + e.getMessage());
            out.println("╥﹏╥ Veuillez entrer un nombre valide");
        } catch (Exception e) {
            System.err.println("[(˶º⤙º˶)] Erreur critique: " + e.getClass().getSimpleName());
            e.printStackTrace();
            out.println("(˶º⤙º˶) Erreur système - Opération annulée");
        } finally {
            System.out.println("[✧*｡٩(ˊᗜˋ*)و✧*｡] Fin traitement retour\n");
        }
    }
}

