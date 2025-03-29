package model;

import java.time.LocalDate;
import java.time.Period;

public class Abonne {
    private final int numero;
    private final String nom;
    private final LocalDate dateNaissance;

    public Abonne(int numero, String nom, LocalDate dateNaissance) {
        this.numero = numero;
        this.nom = nom;
        this.dateNaissance = dateNaissance;
    }

    public int calculerAge() {
        return Period.between(dateNaissance, LocalDate.now()).getYears();
    }

    public int getNumero() {
        return numero;
    }
}