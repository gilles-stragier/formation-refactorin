package be.formatech.training.formationrefactoring.exercice1;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ExcelAnomalieGoFilesGenerator implements GoFilesGenerator {

    public void createGoFiles(String directory, List<String> fileNames) throws Exercice1Exception {
        for (String name : fileNames) {
            try {
                String n = new File(name).getName();
                new File(directory, n.substring(0, n.length() - 4) + ".GO").createNewFile();
            } catch (IOException e) {
                throw new Exercice1Exception(e.getMessage(), e);
            }
        }
    }
}
