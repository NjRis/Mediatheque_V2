package model;

import certifications.BanManager;
import exceptions.*;

import java.time.LocalDateTime;

public class Livre implements Document {
    private final int numero;
    private final String titre;
    private final int pages;
    private Abonne emprunteur;
    private Abonne reserveur;
    private LocalDateTime dateEmprunt;

    public Livre(int numero, String titre, int pages) {
        this.numero = numero;
        this.titre = titre;
        this.pages = pages;
    }


    public synchronized void reserver(Abonne ab) throws ReservationException {
        if (reserveur != null) throw new ReservationException("Déjà réservé");
        reserveur = ab;
    }

    public synchronized void emprunter(Abonne ab) throws EmpruntException {
        if (emprunteur != null) throw new EmpruntException("Déjà emprunté");
        emprunteur = ab;
        dateEmprunt = LocalDateTime.now();
    }

    public synchronized void retourner(boolean estEndommage) {
        BanManager.verifierBanissement(emprunteur, dateEmprunt, estEndommage);
        emprunteur = null;
        reserveur = null;
    }

    // Getters
    public int numero() { return numero; }
    public Abonne emprunteur() { return emprunteur; }
    public Abonne reserveur() { return reserveur; }

    public String getTitre() {
        return titre;
    }
}