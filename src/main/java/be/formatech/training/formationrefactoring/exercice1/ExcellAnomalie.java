package be.formatech.training.formationrefactoring.exercice1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static java.time.format.DateTimeFormatter.ofPattern;

/**
 * <b>Exercice 1 : Cette classe est une simplification de la classe ExcellAnomalie de ForHRM.</b>
 * <p>
 * Les dépendances vers les autres classes de l'application ont soit été supprimées, soit le fragment nécessaire des
 * classes dépendantes a été recopié, soit ont été mockées. C'est en particulier le cas des classes du package "java.sql"
 * permettant l'accès à la base de données. L'implémentation simplifiée se trouve dans le package "internal".
 * </p>
 * <p>
 * Il est donc interdit de toucher aux classes du package "internal" puisqu'elles sont censées faire partie du JDK voire
 * d'autres bibliothèques. Toutefois, comme les requêtes à la base de données sont simulées, si vous tenez absolument à toucher
 * aux requêtes SQL ou si vous modifiez les paramètres qui sont passés à cette requête, vous devrez modifier la constante
 * QUERY_LOTS pour qu'elle corresponde à votre nouvelle requête et la constante RESULT_LOTS
 * qui contient les résultats retournés par la requête.
 * </p>
 */
public class ExcellAnomalie {
    static final int MAXROW = 65535; // dernier numéro de ligne sur une feuille (numérotées de 0 à 65535)
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcellAnomalie.class);

    private final AnomalyRecordDao anomalyRecordDao;
    private final ReportLineMapper reportLineMapper;
    private final ExcelAnomalieArgumentsValidator argumentsValidator;
    private final ReportLinesExcelRenderer reportLinesExcelRenderer;





    public ExcellAnomalie(
            AnomalyRecordDao anomalyRecordDao,
            ReportLineMapper reportLineMapper,
            ExcelAnomalieArgumentsValidator argumentsValidator,
            ReportLinesExcelRenderer reportLinesExcelRenderer
    ) {
        this.anomalyRecordDao = anomalyRecordDao;
        this.reportLineMapper = reportLineMapper;
        this.argumentsValidator = argumentsValidator;
        this.reportLinesExcelRenderer = reportLinesExcelRenderer;
    }

    public static void main(String[] args) {
        try {
            ExcellAnomalie excellAnomalie = new ExcellAnomalie(
                    new AnomalyRecordDao(),
                    new ReportLineMapper(
                            new EmployeurDao()
                    ),
                    new ExcelAnomalieArgumentsValidator(),
                    new ReportLinesExcelRenderer()
            );

            // Les quelques lignes ci-dessous simulent la récupération des arguments du batch ainsi que
            // les propriétés venant du fichier forhrm.properties.
            // Si vous modifiez les paramètres du batch (les propriétés en majuscule), vous aurez le
            // message suivant lors de l'exécution : "Mock is not smart enough to understand your query... :-(".
            Properties properties = new Properties();
            properties.put("ONSSTRIMESTRE", "201902");
            properties.put("ISORIGINAL", "true");
            properties.put("mail.adress", "toto@ucm.be");
            properties.put("repertoire.rejet.dmf", "C:\\Temp");

            excellAnomalie.executeBatch(properties);
            // Regardez le fichier Excel créé dans votre répertoire C:\Temp...
        } catch (Exception e) {
            LOGGER.error("Exception caught ", e);
        }
    }

    /**
     * Construit une liste de lignes à écrire dans le fichier temporaire sur base d'un modèle de ligne qui reprend toutes
     * les informations communes à une liste d'anomalies ayant le même identifiant dans la table OnssAnomalie.
     *
     * @param anomalies       liste des messages d'anomalie
     * @param anomaliesVauban <code>true</code> si les messages sont au format postRefactoring Vauban de 2017, <code>false</code> sinon.
     */
    static List<String> buildTempFileLinesForAnomaly(String[] immutableLine, List<String> anomalies, boolean anomaliesVauban) {
        String[] line = Arrays.copyOf(immutableLine, immutableLine.length);
        List<String> tempFileLines = new ArrayList<>();

        for (String anomaly : anomalies) {
            String rejCat = AnomalyRecord.INFO;
            if (anomaliesVauban) {
				/*
					Pour une version par du code post refactoring Vauban de 2017, il faut considérer qu'une anomalie codifiée
					comme WARNING est à catégoriser comme ERROR. Pour éviter qu'on ne voie une catérie ERROR et dans
					la colonne suivante un libellé commeçant par WARNING, il faut parser l'anomalie pour en extraire la
					partie libellé.
				 */
                String[] parts = anomaly.split("-\\d{3,5}##");
                String niveauRejet = parts[0];
                line[30] = parts[1]; // col.26
                if (AnomalyRecord.WARNING.equals(niveauRejet) || AnomalyRecord.ERROR.equals(niveauRejet)) {
                    rejCat = AnomalyRecord.ERROR;
                }
            } else {
                line[30] = anomaly;
                if (anomaly.toUpperCase().contains(AnomalyRecord.ERROR)) {
                    rejCat = AnomalyRecord.ERROR;
                } else if (anomaly.toUpperCase().contains(AnomalyRecord.WARNING)) {
                    rejCat = AnomalyRecord.WARNING;
                }
            }
            line[29] = rejCat; // col.25

            // Production d'une ligne du fichier
            tempFileLines.add(String.join("\t", line));
        }
        return tempFileLines;
    }


    protected void executeBatch(Properties properties) throws Exercice1Exception {

        if (!argumentsValidator.isValid(properties)) return;

        Trimestre trimestre = new Trimestre(properties.getProperty("ONSSTRIMESTRE"));

        String fileName = computeMailPrefix(properties) + "_DMF_detail_" + trimestre.asYYYYTN() + "_" + formatLotType(properties) + "_" + nowAsYYYYMMDDHHMMSS(LocalDateTime.now());

        String fullPathFileName = new File(properties.getProperty("repertoire.rejet.dmf"), fileName).getAbsolutePath();

        List<AnomalyRecord> anomalyRecords = anomalyRecordDao.fetchAnomalyRecords(trimestre, ("true".equals(properties.getProperty("ISORIGINAL").toLowerCase())));

        List<ReportLine> reportLines = reportLineMapper.buildReportLines(trimestre, anomalyRecords);

        List<String> fileNames = reportLinesExcelRenderer.render(fullPathFileName, reportLines);

        createGoFiles(properties.getProperty("repertoire.rejet.dmf"), fileNames);

    }

    private void createGoFiles(String rejetDirectory, List<String> fileNames) throws Exercice1Exception {
        for (String name : fileNames) {
            try {
                String n = new File(name).getName();
                new File(rejetDirectory, n.substring(0, n.length() - 4) + ".GO").createNewFile();
            } catch (IOException e) {
                throw new Exercice1Exception(e.getMessage(), e);
            }
        }
    }

    private String formatLotType(Properties properties) {
        return ("true".equals(properties.getProperty("ISORIGINAL").toLowerCase())) ? "O" : "R";
    }

    private String computeMailPrefix(Properties properties) {
        String mail_prefix_adress = "nobody"; // si un problème subsiste ...
        String mail_adress = properties.getProperty("mail.adress");

        if (mail_adress != null) {
            int ndx = mail_adress.indexOf("@");

            if (ndx != -1) {
                mail_prefix_adress = mail_adress.substring(0, ndx);
            }
        }
        return mail_prefix_adress;
    }

    String nowAsYYYYMMDDHHMMSS(LocalDateTime localDateTime) {
        return localDateTime.format(ofPattern("yyyyMMddHHmmss"));
    }

}
