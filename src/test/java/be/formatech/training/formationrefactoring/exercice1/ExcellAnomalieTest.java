package be.formatech.training.formationrefactoring.exercice1;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static java.time.Month.MARCH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ExcellAnomalieTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcellAnomalieTest.class);
    private static final String ERROR = "ERROR";
    private static final String WARNING="WARNING";

    private final static String ANOMALIE_VAUBAN_PREMIER_MESSAGE = "PremierMessage";
    private final static String ANOMALIE_VAUBAN_SECOND_MESSAGE = "SecondMessage";
    private final static String ANOMALIE_VAUBAN_1 = "WARNING-123##" + ANOMALIE_VAUBAN_PREMIER_MESSAGE;
    private final static String ANOMALIE_VAUBAN_2 = "WARNING-456##"+ ANOMALIE_VAUBAN_SECOND_MESSAGE;
    private final static String ANOMALIE_VAUBAN = ANOMALIE_VAUBAN_1 + "\n" + ANOMALIE_VAUBAN_2 + "\n";

    private final static String ANOMALIE_OLD_1_LINE_1 = "ERROR PremierMessageLigne1";
    private final static String ANOMALIE_OLD_1_LINE_2 = "PremierMessageLigne2";
    private final static String ANOMALIE_OLD_1 = ANOMALIE_OLD_1_LINE_1 + "\r\n" + ANOMALIE_OLD_1_LINE_2;
    private final static String ANOMALIE_OLD_2_LINE_1 = "WARNING SecondMessageLigne1";
    private final static String ANOMALIE_OLD_2_LINE_2 = "SecondMessageLigne2";
    private final static String ANOMALIE_OLD_2 = ANOMALIE_OLD_2_LINE_1 + "\r\n" + ANOMALIE_OLD_2_LINE_2;
    private final static String ANOMALIE_OLD= ANOMALIE_OLD_1 + "\r\n" + ANOMALIE_OLD_2;

    @Before
    public void setup() {
        File[] candidates = new File("C:\\Temp").listFiles((dir, name) -> name.matches("toto_DMF_detail_2019T2_O_.*xls"));
        Arrays.stream(candidates).forEach(File::delete);

        File[] candidatesGoFiles = new File("C:\\Temp").listFiles((dir, name) -> name.matches("toto_DMF_detail_2019T2_O_.*GO"));
        Arrays.stream(candidatesGoFiles).forEach(File::delete);
    }

    @Test
    public void nowAsString(){
        LocalDateTime someDate = LocalDateTime.of(2000, MARCH, 1, 13, 3, 4);

        ExcellAnomalie excellAnomalie = new ExcellAnomalie(
                new AnomalyRecordDao(),
                new EmployeurDao()
        );
        assertEquals("20000301130304", excellAnomalie.nowAsYYYYMMDDHHMMSS(someDate));

    }

    @Test
    public void validateExcellFile() throws Exception {
        ExcellAnomalie.main(null);

        assertEquals(1, new File("C:\\Temp").listFiles((dir, name) -> name.matches("toto_DMF_detail_2019T2_O_.*GO")).length);

        File[] candidates = new File("C:\\Temp").listFiles((dir, name) -> name.matches("toto_DMF_detail_2019T2_O_.*xls"));
        assertEquals(1, candidates.length);

        File file = candidates[0];
        LOGGER.info("Selected Excel file: " + file.getAbsolutePath());

        HSSFWorkbook myWorkBook = new HSSFWorkbook(new FileInputStream(file));
        HSSFSheet sheet = myWorkBook.getSheetAt(0);
        HSSFRow header = sheet.getRow(0);

        String[] expectedHeaders = {
                "TRIM", "Référence Succu", "Nom de la Succu", "Equipe", "N° gestionnaire", "Nom gestionnaire",
                "N° Lot", "Type Déclaration", "Statut du dossier", "Dossier", "Nom du dossier", "Numéro du travailleur", "Nom du travailleur", "N° Activité", "Nom de l'activité",
                "N° de contrat", "IND", "CAT", "Classification", "Catégorie de Rejet", "Libellé", "Date du statut"
        };

        for (int i=0; i < expectedHeaders.length; i++) {
            assertEquals("La colonne " + i + " (0-based) n'a pas la valeur attendue.", expectedHeaders[i], header.getCell(i).toString());
        }

        Object[][] expectedRows = {
                { "2019T2", "N", "Namur", "91", "N6", "Géraldine Macaux", "1234.0", "O", "rejet (03/03)", "038287", "Cesi", "", "", "", "", "", "", "", "", "ERROR", "***Error : Merveilleuse anomalie.", "13-07-2019" },
                { "2019T2", "N", "Namur", "92", "NA", "Cynthia Benedetti", "1234.0", "O", "rejet (03/03)", "043711", "Traiteur Paulus", "", "", "", "", "", "", "", "", "ERROR", "***Error : Une autre anomalie.", "13-07-2019" }
        };

        for (int row=0; row < expectedRows.length; row++) {
            HSSFRow currentRRow = sheet.getRow(row + 1);
            for (int i = 0; i < expectedRows[row].length; i++) {
                assertEquals("La colonne " + i + " de la ligne " + row+1  + "  (0-based) n'a pas la valeur attendue.", expectedRows[row][i], currentRRow.getCell(i).toString());
            }
        }
    }

    @Test
    public void testBuildAnomaliesVauban() {
        List<String> result = ExcellAnomalie.splitAnomalyTextArea(ANOMALIE_VAUBAN);
        assertEquals(2, result.size());
        assertTrue(result.get(0).contains(ANOMALIE_VAUBAN_1));
        assertTrue(result.get(1).contains(ANOMALIE_VAUBAN_2));
    }

    @Test
    public void testBuildAnomaliesPreVauban() {
        List<String> result = ExcellAnomalie.splitAnomalyTextArea(ANOMALIE_OLD);
        assertEquals(2, result.size());
        assertEquals(ANOMALIE_OLD_1_LINE_1 + " " + ANOMALIE_OLD_1_LINE_2, result.get(0));
        assertEquals(ANOMALIE_OLD_2_LINE_1 + " " + ANOMALIE_OLD_2_LINE_2, result.get(1));
    }

    @Test
    public void testBuildTempFileLinesForAnomalyVauban() {
        String[] line = createTempFileTemplateLine();
        List<String> anomalies = new ArrayList<>();
        anomalies.add(ANOMALIE_VAUBAN_1);
        anomalies.add(ANOMALIE_VAUBAN_2);

        List<String> result = ExcellAnomalie.buildTempFileLinesForAnomaly(line, anomalies, true);
        assertEquals(2, result.size());
        line[29] = ERROR;
        line[30] = ANOMALIE_VAUBAN_PREMIER_MESSAGE;
        String expected = createExpectedTempFileLine(line);
        assertEquals(expected, result.get(0));
        line[30] = ANOMALIE_VAUBAN_SECOND_MESSAGE;
        expected = createExpectedTempFileLine(line);
        assertEquals(expected, result.get(1));
    }

    @Test
    public void testBuildTempFileLinesForAnomalyPreVauban() {
        String[] line = createTempFileTemplateLine();
        List<String> anomalies = new ArrayList<>();
        anomalies.add(ANOMALIE_OLD_1);
        anomalies.add(ANOMALIE_OLD_2);

        List<String> result = ExcellAnomalie.buildTempFileLinesForAnomaly(line, anomalies, false);
        assertEquals(2, result.size());
        line[29] = ERROR;
        line[30] = ANOMALIE_OLD_1;
        String expected = createExpectedTempFileLine(line);
        assertEquals(expected, result.get(0));
        line[29] = WARNING;
        line[30] = ANOMALIE_OLD_2;
        expected = createExpectedTempFileLine(line);
        assertEquals(expected, result.get(1));
    }

    private String[] createTempFileTemplateLine() {
        String[] line = new String[33];
        for (int i=0; i < line.length; i++) {
            line[i] = "" + i;
        }
        return line;
    }

    private String createExpectedTempFileLine(String[] line) {
        return String.join("\t", line);
    }
}
