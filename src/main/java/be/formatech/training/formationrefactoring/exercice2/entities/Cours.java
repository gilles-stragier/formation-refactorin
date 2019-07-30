package be.formatech.training.formationrefactoring.exercice2.entities;

import java.util.List;

public class Cours {
    private Long id;
    private String nom;
    private String typeCours;
    private List<Eleve> eleves;

    protected Cours() {
    }

    public Cours(String nom, String typeCours) {
        this.nom = nom;
        this.typeCours = typeCours;
    }

    public void setTypeCours(String typeCours) {
        this.typeCours = typeCours;
    }

    public String getTypeCours() {
        return this.typeCours;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public List<Eleve> getEleves() {
        return eleves;
    }

    public void setEleves(List<Eleve> eleves) {
        this.eleves = eleves;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
