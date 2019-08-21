package be.formatech.training.formationrefactoring.exercice1;

import java.util.List;

public interface GoFilesGenerator {

    void createGoFiles(String rejetDirectory, List<String> fileNames) throws Exercice1Exception;
}
