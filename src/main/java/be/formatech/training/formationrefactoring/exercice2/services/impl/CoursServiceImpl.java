package be.formatech.training.formationrefactoring.exercice2.services.impl;

import java.util.List;

import be.formatech.training.formationrefactoring.exercice2.dao.InterrogationDao;
import be.formatech.training.formationrefactoring.exercice2.entities.Cours;
import be.formatech.training.formationrefactoring.exercice2.entities.Eleve;
import be.formatech.training.formationrefactoring.exercice2.entities.Interrogation;
import be.formatech.training.formationrefactoring.exercice2.services.CoursService;

public class CoursServiceImpl implements CoursService {
    private final InterrogationDao interrogationDao;

    public CoursServiceImpl(InterrogationDao interrogationDao) {
        this.interrogationDao = interrogationDao;
    }

    @Override
    public boolean coursReussi(Eleve eleve, Cours cours) {
        List<Interrogation> interros = interrogationDao.findInterrogation(eleve, cours);

        if(cours.getTypeCours().equals("principal")) {
            //Si c'est un cours principal, il doit avoir 60% de moyenne et plus de 50% dans chacune des interros
            double totalPoints = 0;
            double maxPoints = 0;
            for(Interrogation i:interros){
                totalPoints += i.getNote();
                maxPoints += i.getNoteMax();

                if(i.getNote()/i.getNoteMax()<0.5){
                    return false;
                }
            }
            if(totalPoints/maxPoints < 0.6){
                return false;
            } else {
                return true;
            }
        } else if(cours.getTypeCours().equals("secondaire") ){
            //Si c'est un cours secondaire, il doit avoir 50% à toutes ses interros
            for(Interrogation i:interros){
                if(i.getNote()/i.getNoteMax()<0.5){
                    return false;
                }
            }
            return true;
        } else {
            //Si c'est un labo, il doit avoir 50% de moyenne
            double totalPoints = 0;
            double maxPoints = 0;
            for(Interrogation i:interros){
                totalPoints += i.getNote();
                maxPoints += i.getNoteMax();
            }
            if(totalPoints/maxPoints < 0.5){
                return false;
            } else {
                return true;
            }

        }
    }
}
