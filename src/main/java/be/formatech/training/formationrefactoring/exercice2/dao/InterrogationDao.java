package be.formatech.training.formationrefactoring.exercice2.dao;

import java.util.List;

import be.formatech.training.formationrefactoring.exercice2.entities.Cours;
import be.formatech.training.formationrefactoring.exercice2.entities.Eleve;
import be.formatech.training.formationrefactoring.exercice2.entities.Interrogation;

public interface InterrogationDao {
    List<Interrogation> findInterrogation(Eleve eleve, Cours cours);
}
