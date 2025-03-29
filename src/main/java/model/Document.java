package model;

import exceptions.EmpruntException;
import exceptions.ReservationException;

public interface Document {
    int numero();
    void reserver(Abonne ab) throws ReservationException;
    void emprunter(Abonne ab) throws EmpruntException;
    void retourner(boolean estEndommage);
    Abonne emprunteur();
    Abonne reserveur();
}