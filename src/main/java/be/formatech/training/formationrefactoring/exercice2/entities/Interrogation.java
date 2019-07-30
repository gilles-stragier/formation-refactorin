package be.formatech.training.formationrefactoring.exercice2.entities;

import java.util.Date;

public class Interrogation {
    private Eleve eleve;
    private Cours cours;
    private Date date;
    private double note;
    private double noteMax;

    /**
     * Retourne la note obtenue pour cette interrogation
     * @return
     */
    public double getNote() {
        return note;
    }

    public void setNote(double note) {
        this.note = note;
    }

    /**
     * Retourne la note maximale pour cette interrogation
     * @return
     */
    public double getNoteMax() {
        return noteMax;
    }

    public void setNoteMax(double noteMax) {
        this.noteMax = noteMax;
    }
}
