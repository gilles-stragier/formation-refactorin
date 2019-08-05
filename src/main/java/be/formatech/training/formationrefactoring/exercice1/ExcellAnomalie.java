package be.formatech.training.formationrefactoring.exercice1;

import be.formatech.training.formationrefactoring.exercice1.internal.ResultSet;
import be.formatech.training.formationrefactoring.exercice1.internal.Statement;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.SQLException;
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
 * {@link Statement#QUERY_LOTS} pour qu'elle corresponde à votre nouvelle requête et la constante {@link Statement#RESULT_LOTS}
 * qui contient les résultats retournés par la requête.
 * </p>
 */
public class ExcellAnomalie {
    /**
     * Expression régulière décrivant le format d'encodage des anomalies dans la gestion des lots DMFA/DMWA adopté dans le
     * cadre du refactoring Vauban de 2017..
     * L'expression ne contient pas le message proprement dit mais bien la forme du préfixe utilisé.
     * Le format complet de codage des <code>LotAnomalie</code> est décrit par {@link #ANOMALIES_PATTERN}.
     */
    public static final String WARNING_PATTERN = "(WARNING|ERROR)-\\d{3,5}##";
    /**
     * Les anomalies associées à un identifient sont une suite de 1 ou plusieurs anomalies (d'où la forme "(....)+" de l'expression régulière).
     * Chaque anomalie est codée sour la forme d'un pattern composé du mot WARNING, d'un tiret, d'un numéro d'anomalie,
     * d'un séparateur ("##") et d'un message terminé par une des différentes formes possibles de retour à la ligne.
     */
    public static final String ANOMALIES_PATTERN = "(" + WARNING_PATTERN + ".+(\\r\\n|\\r|\\n))+";
    static final int MAXROW = 65535; // dernier numéro de ligne sur une feuille (numérotées de 0 à 65535)
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcellAnomalie.class);
    private static final String ERROR = "ERROR";
    private static final String WARNING = "WARNING";
    private static final String INFO = "INFO";
    private String[] filenames;

    public static void main(String[] args) {
        try {
            ExcellAnomalie excellAnomalie = new ExcellAnomalie();

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
     * Construction d'une liste de messages d'anomalies sur base du contenu du message contenu dans une ligne de la table
     * OnssAnomalie. Un message de la liste résultat tient en une seule ligne.
     *
     * @param anomalie             contenu d'une cellule de la table OnssAnomalie
     * @return La liste des messages d'anomalie.
     */
    protected static List<String> splitAnomalyTextArea(String anomalie) {

        List<String> anomalies = new ArrayList<>();
        String[] anomalieLines = anomalie.split("\n");

        for (int i = 0; i < anomalieLines.length; i++) {
            StringBuilder oneLine = new StringBuilder();
					/*
						En fonction du fait que l'anomalie ait été produite par du code post refactoring Vauban de 2017 ou
						du code antérieur, il faut déterminer le libellé qui sera rapporté.

						Pour une version du code antérieure au refactoring Vauban, il n'y a à priori qu'une erreur par ligne
						de la table ONSSANOMALIE, mais le libellé de l'erreur peut tenir en plusieurs lignes. Il faut dans ce cas
						lire toutes les lignes consécutives en de la même erreur en une fois et supprimer les retours à la ligne.
					 */
            String currentLibelle = anomalieLines[i].trim();

            if (anomalie.matches(ANOMALIES_PATTERN)) {
                // Code post refactoring Vauban 2017
                oneLine.append(currentLibelle);
            } else {
                // Code pré refactoring Vauban 2017
                // Dans ce code on peut retrouver une erreur sur plusieurs lignes
                oneLine.append(currentLibelle);
                // Lire toutes les lignes qui font partie de la même erreur pour n'en faire qu'une seule.
                // Dans les messages sur plusieurs ligne, la deuxième ligne et les lignes suivantes ne sont pas sencées contenir
                // les mots INFO, WARNING et ERROR ni en majuscule, ni en minuscule...
                // Donc on boucle tant qu'on ne retrouve pas une telle ligne, puis comme on
                // a lu une ligne de trop, on revient en arrière d'une ligne (une sorte de "put back").
                i++;
                while (i < anomalieLines.length && !anomalieLines[i].toUpperCase().contains(ERROR)
                        && !anomalieLines[i].toUpperCase().contains(WARNING) && !anomalieLines[i].toUpperCase().contains(INFO)) {
                    oneLine.append(" " + anomalieLines[i].replace("\r", "").replace("\n", "").trim());
                    i++;
                }
                i--;
            }
            anomalies.add(oneLine.toString());
        }
        return anomalies;
    }

    /**
     * Construit une liste de lignes à écrire dans le fichier temporaire sur base d'un modèle de ligne qui reprend toutes
     * les informations communes à une liste d'anomalies ayant le même identifiant dans la table OnssAnomalie.
     *
     * @param line            Ensemble des 32 informations de base d'une liste à faire figurer dans le fichier temporaire, et donc
     *                        dans le fichier Excel, à l'exception des messages d'anomalies (line[30]) et du statut d'erreur qui
     *                        découle de ces anomalies (line[29])
     * @param anomalies       liste des messages d'anomalie
     * @param anomaliesVauban <code>true</code> si les messages sont au format postRefactoring Vauban de 2017, <code>false</code> sinon.
     * @return
     */
    protected static List<String> buildTempFileLinesForAnomaly(String[] immutableLine, List<String> anomalies, boolean anomaliesVauban) {
        String[] line = Arrays.copyOf(immutableLine, immutableLine.length);
        List<String> tempFileLines = new ArrayList<>();

        for (String anomaly : anomalies) {
            String rejCat = INFO;
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
                if (WARNING.equals(niveauRejet) || ERROR.equals(niveauRejet)) {
                    rejCat = ERROR;
                }
            } else {
                line[30] = anomaly;
                if (anomaly.toUpperCase().indexOf(ERROR) != -1) {
                    rejCat = ERROR;
                } else if (anomaly.toUpperCase().indexOf(WARNING) != -1) {
                    rejCat = WARNING;
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
        myCell.setCellValue(Long.valueOf(line[6]).longValue());

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

        myCell = myRow.createCell(cellNum++);
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

        /* Tester les arguments manquants */
        if (properties.getProperty("ONSSTRIMESTRE") == null) {
            LOGGER.error("*** FATAL ERROR missing arg 'ONSSTRIMESTRE'.");
            return;
        } else if (!validateQuarter(properties.getProperty("ONSSTRIMESTRE"))) {
            return;
        }

        /*
         * Type de lots demandé : - true => lots originaux (non rectificatifs) - false => lots rectificatifs
         */
        if (properties.getProperty("ISORIGINAL").toLowerCase() == null) {
            LOGGER.error("*** FATAL ERROR missing arg 'ISORIGINAL'.");
            return;
        } else if (!(",true,false,".indexOf(properties.getProperty("ISORIGINAL").toLowerCase()) != -1)) {
            LOGGER.error("*** FATAL ERROR invalid value '" + properties.getProperty("ISORIGINAL").toLowerCase() + "' for arg 'ISORIGINAL'.");
            return;
        }

        Trimestre trimestre = new Trimestre(properties.getProperty("ONSSTRIMESTRE"));

        String fileName = computeMailPrefix(properties) + "_DMF_detail_" + trimestre.asYYYYTN() + "_" + formatLotType(properties) + "_" + nowAsYYYYMMDDHHMMSS(LocalDateTime.now());

        String fullPathFileName = new File(properties.getProperty("repertoire.rejet.dmf"), fileName).getAbsolutePath();

        buildExcel(fullPathFileName, trimestre, ("true".equals(properties.getProperty("ISORIGINAL").toLowerCase())));

        for (String name : this.filenames) {
            try {
                String n = new File(name).getName();
                new File(properties.getProperty("repertoire.rejet.dmf"), n.substring(0, n.length() - 4) + ".GO").createNewFile();
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

        /*
         * Liste et numéro des colonnes demandées suivant l'analyse (pas toutes peuvent être réalisées (voir détail)
         */
        String[] headerLine = initHeader();

        Statement stmt = null;
        ResultSet rs = null;

        // 1ère passe
        File temp = null;

        int nossEndingDate = Util.getNossEndingQuarterDate(trimestre.asYYYYNNShort());

        List<ReportLine> reportLines = new ArrayList<>();

        try {

            // Par souci didactique, seules la partie de la query relative aux employeurs a été conservée... :-)
            String sql = buildQueryForAnomaliesInAQuarter(trimestre, isOriginal);

            // (*) col1 où 1 fait référence à la colonne de la suite
            // présentée dans l'analyse, tandis que 1 seul fait
            // référence au numéro de colonne du SELECT sql

            stmt = Connexion.getConnection().createStatement();

            rs = stmt.executeQuery(sql);



            while (rs.next()) {

                AnomalyRecord anomalyRecord = new AnomalyRecord(rs);

                if (anomalyRecord.getAnomaly() == null || anomalyRecord.getAnomaly().length() == 0) {
                    continue;
                }


                // Génération du contenu des lignes du fichier intermédiaire correspondant à la ligne du ResultSet
                // et écriture de ces lignes dans le fichier.
                List<String> anomalies = splitAnomalyTextArea(anomalyRecord.getAnomaly());



                for (String anomaly : anomalies) {

                    String rejCat = INFO;
                    String libelleAnomaly = null;

                    if (anomalyRecord.getAnomaly().matches(ANOMALIES_PATTERN)) {
                        libelleAnomaly = anomaly.split("-\\d{3,5}##")[1];

                        if (WARNING.equals(anomaly.split("-\\d{3,5}##")[0]) || ERROR.equals(anomaly.split("-\\d{3,5}##")[0])) {
                            rejCat = ERROR;
                        }
                    } else {
                        libelleAnomaly = anomaly;
                        if (anomaly.toUpperCase().indexOf(ERROR) != -1) {
                            rejCat = ERROR;
                        } else if (anomaly.toUpperCase().indexOf(WARNING) != -1) {
                            rejCat = WARNING;
                        }
                    }

                    ReportLine rep = new ReportLine(trimestre, anomalyRecord, rejCat, libelleAnomaly, fetchEmpName(nossEndingDate, anomalyRecord.getEmpcode()));
                    reportLines.add(rep);

                    rep.setStatut(computeStatut(anomalyRecord, rep));
                }

            }

        } catch (SQLException | Exercice1Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException s) {
                    LOGGER.error(s.getMessage(), s);
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException s) {
                    LOGGER.error(s.getMessage(), s);
                }
            }
        }

        List<String> vFilenames = new ArrayList<>();
        int myWorkBookNum = 0;
        HSSFWorkbook myWorkBook = null;
        HSSFSheet mySheet;
        int rowNum = 0;

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

    private String fetchEmpName(int nossEndingDate, String empCode) {
        if (empCode != null && empCode.trim().length() != 0) {
            Statement stmt11 = null;
            ResultSet rs11 = null;
            try {
                String sql11 = "select v11.valeurtext from valeur v11 " + "where v11.entite = 'EMPLOYEUR' and v11.parametre = 'EMPNOM' and "
                        + "v11.identifiant = '" + empCode + "' and " + "v11.debutvalidite <= to_date('" + nossEndingDate + "', 'yyyymmdd') and "
                        + "v11.finvalidite >= to_date('" + nossEndingDate + "', 'yyyymmdd') ";
                stmt11 = Connexion.getConnection().createStatement();
                rs11 = stmt11.executeQuery(sql11);
                if (rs11.next()) {
                    return rs11.getString(1);
                }
            } catch (SQLException | Exercice1Exception s) {
                LOGGER.error(s.getMessage(), s);
            } finally {
                if (rs11 != null) {
                    try {
                        rs11.close();
                    } catch (SQLException s) {
                        LOGGER.error(s.getMessage(), s);
                    }
                }
                if (stmt11 != null) {
                    try {
                        stmt11.close();
                    } catch (SQLException s) {
                        LOGGER.error(s.getMessage(), s);
                    }
                }
            }
        }
        return "";
    }

    private String computeStatut(AnomalyRecord record, ReportLine reportLine) {
        /* Traduction des statuts du lot ou de la société */
        String olStat = record.getStatut();
        String olStatActu = record.getStatutActualisation();
        String olcStat = record.getOlc_statut();
        String olcStatActu = record.getOlc_statutActualisation();

        String statut = null;

        if (record.getEmpcode() != null && record.getEmpcode().trim().length() != 0) {
            // ligne société, travailleur ou contrat
            if ("O".equals(record.getLottype())) { // lot orig.
                if ("05".equals(olcStat)) {
                    // Implique que la société est à (re)générer
                    statut = "à corriger";
                } else {

                    if ("01".equals(olStat)) {
                        // Implique que la société est à (re)générer
                        statut = "à générer";
                    } else if ("04".equals(olStat)) {
                        // Impliqu'une des sociétés du lot est en cours de génération,
                        // donc au pire la société est à regénérer
                        statut = "en cours";
                    } else if ("03".equals(olStat)) {
                        // Impliqu'une ou plusieurs des sociétés du lot sont en erreur
                        if ("03".equals(olcStat)) { // société en erreur ...
                            statut = "rejet";
                        } else if ("02".equals(olcStat)) {
                            statut = "généré";
                        } else { // ... sinon à regénérer
                            statut = "à générer";
                        }
                    } else if ("02".equals(olStat)) {
                        // Implique toutes sociétés du lot sont générées
                        statut = "généré";
                    } else if ("09".equals(olStat)) {
                        statut = "envoi en cours";
                    } else if ("08".equals(olStat)) {
                        statut = "envoi en erreur";
                    } else if ("10".equals(olStat)) {
                        statut = "envoyé";
                    } else if ("21".equals(olStat)) {
                        statut = "accusé négatif";
                    } else if (",20,31,30,".indexOf("," + olStat + ",") != -1) {
                        // une ou plusieurs sociétés du lot ont été soit ...
                        if ("30".equals(olcStat)) {
                            statut = "notification positive ";
                        } else if ("31".equals(olcStat)) {
                            statut = "notification négative";
                        } else if ("40".equals(olcStat)) {
                            statut = "révoqué";
                        } else {
                            statut = "accusé positif"; // pas de noti reçue
                        }
                        // Ce qui suit écrase la valeur précèdente de 'rejCat'
                        // qui ne peut qu'être moins relevante
                        if ("40".equals(olcStat)) {
                            reportLine.setRejCat(ERROR);
                        }
                    } else {
                        statut = "?"; // cas imprévu !
                    }
                }
                statut += " (" + record.getStatut() + "/" + record.getOlc_statut() + ")";
            } else { // lot rectif.
                if ("00".equals(olStat)) {
                    statut = "consultation "; // phase 1 : consultation de la déclaration actuelle chez l'ONSS
                    if ("101".equals(olcStatActu)) {
                        statut += "à faire";
                    } else if ("103".equals(olcStatActu)) {
                        statut += "- envoi en cours";
                    } else if ("105".equals(olcStatActu)) {
                        statut += "- envoi en erreur";
                    } else if ("109".equals(olcStatActu)) {
                        statut += "envoyée";
                    } else if ("123".equals(olcStatActu)) {
                        statut += "- réception en cours";
                    } else if ("125".equals(olcStatActu)) {
                        statut += "- réception en erreur";
                    } else if (",127,128,129,".indexOf("," + olcStatActu + ",") != -1) {
                        statut += "reçue";
                    } else {
                        statut = "?"; // autres cas
                    }
                } else if (",101,104,103,102,".indexOf(olStat) != -1) {
                    statut = "actualisation "; // phase 2 : générer la déclaration actuelle chez ForHRM (même
                    // forme que l'original)
                    if ("101".equals(olcStat)) {
                        statut += "à faire";
                    } else if ("104".equals(olcStat)) {
                        statut += "en cours";
                    } else if ("103".equals(olcStat)) {
                        statut += "en erreur";
                    } else if ("102".equals(olcStat)) {
                        statut += "faite";
                    } else {
                        statut = "?"; // autres cas
                    }
                } else if ("01,04,03,02,".contains(olStat)) {
                    statut = "génération "; // phase 3 : comparer la déclaration de ForHRM avec celle de l'ONSS
                    // pour générer la déclaration
                    // rectificative à envoyer
                    if ("01".equals(olcStat)) {
                        statut += "à faire";
                    } else if ("04".equals(olcStat)) {
                        statut += "en cours";
                    } else if ("03".equals(olcStat)) {
                        statut += "en erreur";
                    } else if ("02".equals(olcStat)) {
                        if ("200".equals(olcStatActu)) {
                            statut += "complète faite";
                        } else if ("201".equals(olcStatActu)) {
                            statut += "partielle faite";
                        } else if ("209".equals(olcStatActu)) {
                            statut += "- rien à rectifier";
                        } else if ("210".equals(olcStatActu)) {
                            statut += "- complète CNL uniquement";
                        } else {
                            statut = "?"; // cas imprévu !
                        }

                    } else {
                        statut = "?"; // autres cas
                    }
                }
                // phase 4 : envoi
                else if ("09".equals(olStat)) {
                    statut = "envoi en cours";
                } else if ("08".equals(olStat)) {
                    statut = "envoi en erreur";
                } else if ("10".equals(olStat)) {
                    statut = "envoyé";
                } else if ("21".equals(olStat)) {
                    statut = "accusé négatif";
                } else if (",20,33,32,31,30,".contains("," + olStat + ",")) {
                    // une ou plusieurs sociétés du lot ont été soit ...
                    if ("33".equals(olcStat)) {
                        statut = "notification partielle négative";
                    } else if ("32".equals(olcStat)) {
                        statut = "notication partielle positive";
                    } else if ("31".equals(olcStat)) {
                        statut = "notication négative";
                    } else if ("30".equals(olcStat)) {
                        statut = "notication positive";
                    } else if ("40".equals(olcStat)) {
                        statut = "révoqué";
                    } else {
                        statut = "accusé positive";
                    }
                    // Ce qui suit écrase la valeur précèdente de 'rejCat'
                    // qui ne peut qu'être moins relevante
                    if ("40".equals(olcStat)) {
                        reportLine.setRejCat(ERROR);
                    }
                } else {
                    statut = "?"; // cas imprévu !
                }

            }
        } else if (record.getLotNo() != null && record.getLotNo().trim().length() != 0) {
            // ligne lot ou indice
            if ("O".equals(record.getLottype())) { // lot orig.
                if ("01".equals(olStat)) {
                    // Implique que toutes les sociétés sont à (re)générer
                    statut = "à générer";
                } else if ("04".equals(olStat)) {
                    // Impliqu'une des sociétés du lot est en cours de génération
                    statut = "génération en cours";
                } else if ("03".equals(olStat)) {
                    // Impliqu'une ou plusieurs des sociétés du lot sont en erreur
                    statut = "rejet";
                } else if ("02".equals(olStat)) {
                    // Implique toutes sociétés du lot sont générées
                    statut = "généré";
                } else if ("09".equals(olStat)) {
                    statut = "envoi en cours";
                } else if ("08".equals(olStat)) {
                    statut = "envoi en erreur";
                } else if ("10".equals(olStat)) {
                    statut = "envoyé";
                } else if ("21".equals(olStat)) {
                    statut = "accusé -";
                } else if ("20".equals(olStat)) {
                    // Implique que des notifications de société du lot sont attendues mais pas toutes reçues
                    statut = "accusé +";
                } else if ("31".equals(olStat)) {
                    // Implique que les notifications des sociétés du lot sont toutes reçues dont quelques unes
                    // sont négatives
                    statut = "sociétés à révoquer";
                } else if ("30".equals(olStat)) {
                    // Implique que les notifications des sociétés du lot sont toutes reçues dont les négatives
                    // sont révoquées
                    statut = "terminé";
                } else {
                    statut = "?"; // cas imprévu !
                }
                statut += " (" + record.getStatut() + ")";
            } else { // lot rectif.
                // phase 1 : consultation de la déclaration actuelle chez l'ONSS
                if ("00".equals(olStat)) {
                    statut = "consultation ";
                }
                // phase 2 : générer la déclaration actuelle chez ForHRM (même forme que l'original)
                else if ("101".equals(olStat)) {
                    statut = "actualisation à faire";
                } else if ("104".equals(olStat)) {
                    statut = "actualisation en cours";
                } else if ("103".equals(olStat)) {
                    statut = "actualisation en erreur";
                } else if ("102".equals(olStat)) {
                    statut = "actualisation faite";
                }
                // phase 3 : comparer la déclaration de ForHRM avec celle de l'ONSS pour générer la déclaration
                // rectificative à envoyer
                else if ("01".equals(olStat)) {
                    statut = "génération à faire";
                } else if ("04".equals(olStat)) {
                    statut = "génération en cours";
                } else if ("03".equals(olStat)) {
                    statut = "génération en erreur";
                } else if ("02".equals(olStat)) {
                    if ("200".equals(olStatActu)) {
                        statut = "génération complète faite";
                    } else if ("201".equals(olStatActu)) {
                        statut = "génération partielle faite";
                    } else if ("209".equals(olStatActu)) {
                        statut = "rien à rectifier";
                    } else if ("210".equals(olcStatActu)) {
                        statut += "- complète CNL uniquement";
                    } else {
                        statut = "?"; // cas imprévu !
                    }
                }
                // phase 4 : envoi
                else if ("09".equals(olStat)) {
                    statut = "envoi en cours";
                } else if ("08".equals(olStat)) {
                    statut = "envoi en erreur";
                } else if ("10".equals(olStat)) {
                    statut = "envoyé";
                } else if ("21".equals(olStat)) {
                    statut = "accusé négatif";
                } else if ("20".equals(olStat)) {
                    statut = "accusé positif";
                } else if ("33".equals(olStat)) {
                    statut = "notification partielle négative";
                } else if ("32".equals(olStat)) {
                    statut = "notication partielle positive";
                } else if ("31".equals(olStat)) {
                    statut = "notication négative";
                } else if ("30".equals(olStat)) {
                    statut = "notication positive";
                } else {
                    statut = "?"; // cas imprévu !
                }
                statut += " (" + record.getStatut() + "," + record.getStatutActualisation() + ")";
            }
        }
        return statut;
    }

    private String fetchSuccu(int nossEndingDate, String empCode) {
        Statement stmt3 = null;
        ResultSet rs3 = null;
        try {
            String sql3 = "select v3.valeurtext from valeur v3 " + "where v3.entite = 'SUCCU' and " + "v3.parametre = 'NOMSUCCU' and "
                    + "v3.identifiant = (select f_get_succuid_for_employer(sysdate, '1', '" + empCode + "') from dual) and "
                    + "v3.debutvalidite <= to_date('" + nossEndingDate + "', 'yyyymmdd') and " + "v3.finvalidite >= to_date('" + nossEndingDate
                    + "', 'yyyymmdd') ";
            stmt3 = Connexion.getConnection().createStatement();
            rs3 = stmt3.executeQuery(sql3);
            if (rs3.next()) {
                return rs3.getString(1);
            }
        } catch (SQLException | Exercice1Exception s) {
            LOGGER.error(s.getMessage(), s);
        } finally {
            if (rs3 != null) {
                try {
                    rs3.close();
                } catch (SQLException s) {
                    LOGGER.error(s.getMessage(), s);
                }
            }
            if (stmt3 != null) {
                try {
                    stmt3.close();
                } catch (SQLException s) {
                    LOGGER.error(s.getMessage(), s);
                }
            }
        }
        return "";
    }

    private String buildQueryForAnomaliesInAQuarter(Trimestre trimestre, boolean isOriginal) {
        return "select col2, col3, col4, col5, col6, col7, col8, col9a,  col9b,  col9c,  col9d,  col9e,  col9f, col10, col12, col14, col16, col18, col19, col25and26, col27  from ( "
                +

                /*
                    Ajout des erreurs de niveau lot
                 */
                "select f_get_succuref_for_employer(sysdate, '1', olc.empcode) 																					as col2, "
                + // 1
                "f_getvaleur('SUCCU', 'NOMSUCCU',  f_get_succuid_for_employer(CURRENT_DATE, '1', olc.empcode), CURRENT_DATE, '')  								as col3, "
                + // 2
                "substr(f_getparametre('CONTACTCLI', 'ENTITEID', f_get_contactid_for_employer(CURRENT_DATE, '1', olc.empcode), CURRENT_DATE), "
                + "instr(f_getparametre('CONTACTCLI', 'ENTITEID', f_get_contactid_for_employer(CURRENT_DATE, '1', olc.empcode), CURRENT_DATE), 'EQUIPE:') + 16,2) as col4, "
                + // 3
                "f_get_contactid_for_employer(CURRENT_DATE, '1', olc.empcode)                                                                                   as col5, "
                + // 4
                "f_get_contactlname_for_emp(CURRENT_DATE, '1', olc.empcode) || ' ' || f_get_contactfname_for_emp(CURRENT_DATE, '1', olc.empcode)                as col6, "
                + // 5
                "ol.lotno as col7, "
                + // 6
                "ol.lottype as col8, "
                + // 7
                "ol.statut as col9a, "
                + // 8
                "ol.statutactualisation as col9b, "
                + // 9
                "olc.statut as col9c, "
                + // 10
                "olc.statutactualisation as col9d, "
                + // 11
                "' ' as col9e, "
                + // 12
                "' ' as col9f, "
                + // 13
                "olc.empcode as col10, "
                + // 14
                "' ' as col12, "
                + // 15
                "' ' as col14, "
                + // 16
                "' ' as col16, "
                + // 17
                "-1 as col18, "
                + // 18
                "' ' as col19, "
                + // 19
                "a.anomalie as col25and26, "
                + // 20
                "ol.timestatut as col27 "
                + // 21
                "from onsslot ol, onsslotclient olc, onssanomalie a "
                + "where ol.lottype "
                + (isOriginal ? "not in" : "in")
                + " ('R') and "
                + "ol.travtrimestre = '"
                + trimestre.asYYYYNN()
                + "' and "
                + "ol.lotno = olc.lotno and "
                + "a.identifiant like to_char(ol.lotno) || '.' || olc.empcode || '.______' )";
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

    private boolean validateQuarter(String onssTrimestreProperty) {

        try {
            Integer.valueOf(onssTrimestreProperty);
        } catch (Exception e) {
            LOGGER.error("*** FATAL ERROR invalid value '" + onssTrimestreProperty + "' for arg 'ONSSTRIMESTRE'.");
            return false;
        }

        int yyyy0q = Integer.valueOf(onssTrimestreProperty);

        if (trimestreYear(yyyy0q) < 2000 || (trimestreYear(yyyy0q)) > 2099 || trimestreQuarter(yyyy0q) < 1 || (trimestreQuarter(yyyy0q)) > 4) {
            LOGGER.error("*** FATAL ERROR invalid value '" + onssTrimestreProperty + "' for arg 'ONSSTRIMESTRE'.");
            return false;
        }

        return true;
    }

    private int trimestreQuarter(int yyyy0q) {
        return yyyy0q % 100;
    }

    private int trimestreYear(int yyyy0q) {
        return yyyy0q / 100;
    }

}
