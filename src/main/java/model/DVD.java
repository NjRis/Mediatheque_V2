package model;

import certifications.EmailManager;
import certifications.BanManager;
import exceptions.*;
import manager.DocumentManager;

import java.time.LocalDateTime;

public class DVD implements Document {
    private final int numero;
    private final String titre;
    private final boolean adulte;
    private Abonne emprunteur;
    private Abonne reserveur;
    private LocalDateTime dateEmprunt;

    public DVD(int numero, String titre, boolean adulte) {
        this.numero = numero;
        this.titre = titre;
        this.adulte = adulte;
    }



    public synchronized void reserver(Abonne ab) throws ReservationException {
        if (emprunteur != null || (reserveur != null && !reserveur.equals(ab))) {
            throw new ReservationException("DVD déjà réservé");
        }
        if (adulte && ab.calculerAge() < 16) {
            throw new ReservationException("Réservation interdite -18 ans");
        }
        reserveur = ab;
    }

    public boolean isAdulte() {
        return adulte;
    }

    public synchronized void emprunter(Abonne ab) throws EmpruntException {
        if (emprunteur != null) throw new EmpruntException("Déjà emprunté");
        if (reserveur != null && !reserveur.equals(ab)) {
            throw new EmpruntException("Réservé par " + reserveur.getNumero());
        }
        if (adulte && ab.calculerAge() < 16) {
            throw new EmpruntException("Âge insuffisant");
        }
        emprunteur = ab;
        dateEmprunt = LocalDateTime.now();
    }

    public synchronized void retourner(boolean estEndommage) {
        BanManager.verifierBanissement(emprunteur, dateEmprunt, estEndommage);

        if (DocumentManager.getInstance().hasAlerte(numero)) {
            EmailManager.envoyerAlerte(titre);
            DocumentManager.getInstance().supprimerAlerte(numero);
        }

        emprunteur = null;
        reserveur = null;
    }


    public int numero() { return numero; }
    public Abonne emprunteur() { return emprunteur; }
    public Abonne reserveur() { return reserveur; }
}
