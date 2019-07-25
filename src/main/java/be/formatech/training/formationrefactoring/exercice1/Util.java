package be.formatech.training.formationrefactoring.exercice1;

/**
 * Reprise d'un framgment de classe ForHRM pour les besoins de l'exercice.
 */

public class Util {
    static public int getNossEndingQuarterDate(short quarter) {
        int endingQuarterDate = (quarter / 10) * 10000;
        switch (quarter % 10) {
            case 1:
                endingQuarterDate += 331;
                break;
            case 2:
                endingQuarterDate += 630;
                break;
            case 3:
                endingQuarterDate += 930;
                break;
            case 4:
                endingQuarterDate += 1231;
                break;
        }
        return endingQuarterDate;
    }

}
