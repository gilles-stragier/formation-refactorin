package be.formatech.training.formationrefactoring.exercice1;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

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

    private String[] filenames;


    public ExcellAnomalie(
            AnomalyRecordDao anomalyRecordDao,
            ReportLineMapper reportLineMapper,
            ExcelAnomalieArgumentsValidator argumentsValidator
    ) {
        this.anomalyRecordDao = anomalyRecordDao;
        this.reportLineMapper = reportLineMapper;
        this.argumentsValidator = argumentsValidator;
    }

    public static void main(String[] args) {
        try {
            ExcellAnomalie excellAnomalie = new ExcellAnomalie(
                    new AnomalyRecordDao(),
                    new ReportLineMapper(
                            new EmployeurDao()
                    ),
                    new ExcelAnomalieArgumentsValidator()
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

    private static int createNewWorkBook(HSSFSheet mySheet, String[] headerLine) {
        // renvoi le nombre de lignes créés dans le header (1 en principe)
        mySheet.setColumnWidth(2, (short) 3000);
        mySheet.setColumnWidth(5, (short) 5000);
        mySheet.setColumnWidth(8, (short) 4000);
        mySheet.setColumnWidth(10, (short) 3000);
        mySheet.setColumnWidth(12, (short) 5000);
        mySheet.setColumnWidth(14, (short) 3000);
        mySheet.setColumnWidth(18, (short) 10000);
        mySheet.setColumnWidth(19, (short) 3000);
        mySheet.setColumnWidth(20, (short) 30000);
        int rowNum = 0;
        HSSFRow myRow = mySheet.createRow(rowNum++);
        int cellNum = 0;
        HSSFCell myCell = myRow.createCell(cellNum++);
        myCell.setCellValue(headerLine[0]);
        myCell = myRow.createCell(cellNum++);
        myCell.setCellValue(headerLine[1]);
        myCell = myRow.createCell(cellNum++);
        myCell.setCellValue(headerLine[2]);
        myCell = myRow.createCell(cellNum++);
        myCell.setCellValue(headerLine[3]);
        myCell = myRow.createCell(cellNum++);
        myCell.setCellValue(headerLine[4]);
        myCell = myRow.createCell(cellNum++);
        myCell.setCellValue(headerLine[5]);
        myCell = myRow.createCell(cellNum++);
        myCell.setCellValue(headerLine[6]);
        myCell = myRow.createCell(cellNum++);
        myCell.setCellValue(headerLine[7]);
        myCell = myRow.createCell(cellNum++);
        myCell.setCellValue(headerLine[8]);
        myCell = myRow.createCell(cellNum++);
        myCell.setCellValue(headerLine[9]);
        myCell = myRow.createCell(cellNum++);
        myCell.setCellValue(headerLine[10]);
        myCell = myRow.createCell(cellNum++);
        myCell.setCellValue(headerLine[11]);
        myCell = myRow.createCell(cellNum++);
        myCell.setCellValue(headerLine[12]);
        myCell = myRow.createCell(cellNum++);
        myCell.setCellValue(headerLine[13]);
        myCell = myRow.createCell(cellNum++);
        myCell.setCellValue(headerLine[14]);
        myCell = myRow.createCell(cellNum++);
        myCell.setCellValue(headerLine[15]);
        myCell = myRow.createCell(cellNum++);
        myCell.setCellValue(headerLine[17]);
        myCell = myRow.createCell(cellNum++);
        myCell.setCellValue(headerLine[18]);
        myCell = myRow.createCell(cellNum++);
        myCell.setCellValue(headerLine[23]);
        myCell = myRow.createCell(cellNum++);
        myCell.setCellValue(headerLine[24]);
        myCell = myRow.createCell(cellNum++);
        myCell.setCellValue(headerLine[25]);
        myCell = myRow.createCell(cellNum++);
        myCell.setCellValue(headerLine[26]);
        return rowNum;
    }

    private static int createRow(HSSFSheet mySheet, int rowNum, String[] line) {
        HSSFRow myRow = mySheet.createRow(rowNum++);
        int cellNum = 0;
        HSSFCell myCell = myRow.createCell(cellNum++);
        myCell.setCellValue(line[0]);

        myCell = myRow.createCell(cellNum++);
        myCell.setCellValue(line[1]);

        myCell = myRow.createCell(cellNum++);
        myCell.setCellValue(line[2]);

        myCell = myRow.createCell(cellNum++);
        myCell.setCellValue(line[3]);

        myCell = myRow.createCell(cellNum++);
        myCell.setCellValue(line[4]);

        myCell = myRow.createCell(cellNum++);
        myCell.setCellValue(line[5]);

        myCell = myRow.createCell(cellNum++);
        myCell.setCellValue(Long.valueOf(line[6]));

        myCell = myRow.createCell(cellNum++);
        myCell.setCellValue(line[7]);

        myCell = myRow.createCell(cellNum++);
        myCell.setCellValue(line[8]);

        myCell = myRow.createCell(cellNum++);
        myCell.setCellValue(line[14]);

        myCell = myRow.createCell(cellNum++);
        myCell.setCellValue(line[15]);

        myCell = myRow.createCell(cellNum++);
        myCell.setCellValue(line[16]);

        myCell = myRow.createCell(cellNum++);
        myCell.setCellValue(line[17]);

        myCell = myRow.createCell(cellNum++);
        myCell.setCellValue(line[18]);

        myCell = myRow.createCell(cellNum++);
        myCell.setCellValue(line[19]);

        myCell = myRow.createCell(cellNum++);
        myCell.setCellValue(line[20]);

        myCell = myRow.createCell(cellNum++);
        myCell.setCellValue(line[22]);

        myCell = myRow.createCell(cellNum++);
        myCell.setCellValue(line[23]);

        myCell = myRow.createCell(cellNum++);
        myCell.setCellValue(line[28]);

        myCell = myRow.createCell(cellNum++);
        myCell.setCellValue(line[29]);

        myCell = myRow.createCell(cellNum++);
        myCell.setCellValue(line[30]);

        myCell = myRow.createCell(cellNum);
        myCell.setCellValue(line[31]);

        return rowNum;
    }

    private static HSSFWorkbook createNextRow(String fullPathFileName, StringBuilder sMyWorkBookNum, // argument
                                              // myWorkBookNum++
                                              // !!!
                                              List<String> vFilenames, HSSFWorkbook myWorkBook, String[] headerLine, StringBuilder sRowNum, String[] line) {

        int rowNum = Integer.valueOf(sRowNum.toString());

        HSSFSheet mySheet;

        if (myWorkBook == null || rowNum > MAXROW) {

            if (myWorkBook != null) {
                try {
                    int myWorkBookNum = Integer.valueOf(sMyWorkBookNum.toString());
                    String fullPathFilename = fullPathFileName + "_" + (myWorkBookNum++) + ".xls";
                    sMyWorkBookNum.setLength(0);
                    sMyWorkBookNum.append(myWorkBookNum);
                    FileOutputStream outWorkBook = new FileOutputStream(fullPathFilename);
                    myWorkBook.write(outWorkBook);
                    outWorkBook.close();
                    vFilenames.add(fullPathFilename);
                } catch (IOException i) {
                    LOGGER.error(i.getMessage(), i);
                }
            }

            myWorkBook = new HSSFWorkbook();
            mySheet = myWorkBook.createSheet();
            rowNum = createNewWorkBook(mySheet, headerLine);
        }

        mySheet = myWorkBook.getSheetAt(0);
        rowNum = createRow(mySheet, rowNum, line);

        sRowNum.setLength(0);
        sRowNum.append(rowNum);
        return myWorkBook;

    }

    protected void executeBatch(Properties properties) throws Exercice1Exception {

        if (!argumentsValidator.isValid(properties)) return;

        Trimestre trimestre = new Trimestre(properties.getProperty("ONSSTRIMESTRE"));

        String fileName = computeMailPrefix(properties) + "_DMF_detail_" + trimestre.asYYYYTN() + "_" + formatLotType(properties) + "_" + nowAsYYYYMMDDHHMMSS(LocalDateTime.now());

        String fullPathFileName = new File(properties.getProperty("repertoire.rejet.dmf"), fileName).getAbsolutePath();

        buildExcel(fullPathFileName, trimestre, ("true".equals(properties.getProperty("ISORIGINAL").toLowerCase())));

        createGoFiles(properties.getProperty("repertoire.rejet.dmf"));

    }

    private void createGoFiles(String rejetDirectory) throws Exercice1Exception {
        for (String name : this.filenames) {
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
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    private void buildExcel(String fullPathFileName, Trimestre trimestre, boolean isOriginal) {

        List<AnomalyRecord> anomalyRecords = anomalyRecordDao.fetchAnomalyRecords(trimestre, isOriginal);

        List<ReportLine> reportLines = reportLineMapper.buildReportLines(trimestre, anomalyRecords);

        renderToExcel(fullPathFileName, reportLines);

    }

    private void renderToExcel(String fullPathFileName, List<ReportLine> reportLines) {
        List<String> vFilenames = new ArrayList<>();
        int myWorkBookNum = 0;
        HSSFWorkbook myWorkBook = null;
        HSSFSheet mySheet;
        int rowNum = 0;
        String[] headerLine = initHeader();


        for (ReportLine rl : reportLines) {
            StringBuilder sMyWorkBookNum = new StringBuilder(Integer.valueOf(myWorkBookNum).toString());
            StringBuilder sRowNum = new StringBuilder(Integer.valueOf(rowNum).toString());
            myWorkBook = createNextRow(fullPathFileName, sMyWorkBookNum, vFilenames, myWorkBook, headerLine, sRowNum, rl.toLine());
            myWorkBookNum = Integer.valueOf(sMyWorkBookNum.toString());
            rowNum = Integer.valueOf(sRowNum.toString());
        }

        if (myWorkBook == null) { // création feuille Excel sans résultats
            myWorkBook = new HSSFWorkbook();
            mySheet = myWorkBook.createSheet();
            createNewWorkBook(mySheet, headerLine);
        }

        writeWorkbookToDisk(fullPathFileName, vFilenames, myWorkBookNum, myWorkBook);
    }

    private void writeWorkbookToDisk(String fullPathFileName, List<String> vFilenames, int myWorkBookNum, HSSFWorkbook myWorkBook) {
        try {
            String fullPathFilename = fullPathFileName + "_" + myWorkBookNum + ".xls";
            FileOutputStream outWorkBook = new FileOutputStream(fullPathFilename);
            myWorkBook.write(outWorkBook);
            outWorkBook.close();
            vFilenames.add(fullPathFilename);
            this.filenames = new String[vFilenames.size()];
            for (int i = 0; i < vFilenames.size(); i++) {
                this.filenames[i] = vFilenames.get(i);
            }
        } catch (IOException i) {
            LOGGER.error(i.getMessage(), i);
        }
    }

    private String[] initHeader() {
        String[] headerLine = new String[31];
        for (int i = 0; i < headerLine.length; i++) {
            headerLine[i] = "";
        }

        /* Trim dcl : (Col. 1) Référence du trimestre de la DMF */
        headerLine[0] = "TRIM";

        /* Ref suc : (Col. 2) Réf. de la succursale qui gère le dossier */
        headerLine[1] = "Référence Succu";

        /* Nom suc : (Col. 3) Nom de la succursale qui gère le dossier */
        headerLine[2] = "Nom de la Succu";

        /* Ref equip : (Col. 4) Réf. de l'équipe qui gère le dossier */
        headerLine[3] = "Equipe";

        /* Ref gest : (Col. 5) Réf. du gestionnaire qui gère le dossier */
        headerLine[4] = "N° gestionnaire";

        /* Nom gest : (Col. 6) Nom-prénom du gestionnaire qui gère le dossier */
        headerLine[5] = "Nom gestionnaire";

        /* NLOT : (Col. 7) Numéro du lot qui contient la DMF */
        headerLine[6] = "N° Lot";

        /* Type dcl : (Col. 8) Type de DMF : Originale ou Rectificative */
        headerLine[7] = "Type Déclaration";

        /*
         * Stat DMF : (Col. 9) Valeur du statut de la DMF - Libellé du statut A FAIRE : Libellé du statut
         */
        headerLine[8] = "Statut du dossier";

        /* No Empl : (Col.10) Numéro de l'employeur */
        headerLine[9] = "Dossier";

        /* Nom Empl : (Col.11) Nom de l'employeur */
        headerLine[10] = "Nom du dossier";

        /* No Trav : (Col.12) Numéro du travailleur */
        headerLine[11] = "Numéro du travailleur";

        /* Nom Trav : (Col.13) Nom du travailleur */
        headerLine[12] = "Nom du travailleur";

        /* No Act : (Col.14) Numéro de l'activité */
        headerLine[13] = "N° Activité";

        /* Nom Act : (Col.15) Libellé de l'activité */
        headerLine[14] = "Nom de l'activité";

        /* No Contr : (Col.16) Numéro du contrat */
        headerLine[15] = "N° de contrat";

        /*
         * Nom Contr : (Col.17) Libellé du contrat PAS SUPPORTE
         */

        /* IND : (Col.18) Indice ONSS */
        headerLine[17] = "IND";

        /* CAT : (Col.19) Catégorie travailleur */
        headerLine[18] = "CAT";

        /*
         * No VOIT : (Col.20) Numéro du véhicule de société PAS SUPPORTE
         */

        /*
         * Lib Voit : (Col.21) Description du véhicule PAS SUPPORTE
         */

        /*
         * Plaque : (Col.22) Valeur de la plaque du véhicule PAS SUPPORTE
         */

        /*
         * CNL : (Col.23) Code cotisation A DEFINIR
         */

        /* REJ : (Col.24) Identifiant du rejet */
        headerLine[23] = "Classification";

        /*
         * REJ CAT : (Col.25) Catégorie du rejet (Error, Warning, Info) sur base du contenu de REJ Lib
         */
        headerLine[24] = "Catégorie de Rejet";

        /*
         * REJ Lib : (Col.26) Libellé du rejet est le contenu de la colonne 'anomalie' de la table 'onssanomalie'
         */
        headerLine[25] = "Libellé";

        /*
         * Date : (Col.27) Date du contrôle sur base de la colonne 'timestatut' de la table 'onsslot'
         */
        headerLine[26] = "Date du statut";

        /*
         * Heure : (Col.28) Heure du contrôle PAS SUPPORTE, peut être fait simplement
         */
        return headerLine;
    }

}
