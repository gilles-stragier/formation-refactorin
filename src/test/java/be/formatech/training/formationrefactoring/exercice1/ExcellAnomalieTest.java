package be.formatech.training.formationrefactoring.exercice1;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ExcellAnomalieTest {
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


    @Test
    public void testBuildAnomaliesVauban() {
        List<String> result = ExcellAnomalie.buildAnomalies(ANOMALIE_VAUBAN, true);
        assertEquals(2, result.size());
        assertTrue(result.get(0).contains(ANOMALIE_VAUBAN_1));
        assertTrue(result.get(1).contains(ANOMALIE_VAUBAN_2));
    }

    @Test
    public void testBuildAnomaliesPreVauban() {
        List<String> result = ExcellAnomalie.buildAnomalies(ANOMALIE_OLD, false);
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
