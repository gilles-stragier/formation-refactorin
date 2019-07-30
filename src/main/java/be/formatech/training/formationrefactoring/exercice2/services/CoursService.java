package be.formatech.training.formationrefactoring.exercice2.services;

import be.formatech.training.formationrefactoring.exercice2.entities.Cours;
import be.formatech.training.formationrefactoring.exercice2.entities.Eleve;

public interface CoursService {
    /**
     * La méthode vérifie si un élève a réussi un cours en vérifiant les résultats de ses Interrogation
     * @param eleve
     * @param cours
     * @return
     */
    boolean coursReussi(Eleve eleve, Cours cours);
}
