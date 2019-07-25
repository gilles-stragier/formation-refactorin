package be.formatech.training.formationrefactoring.exercice1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import be.formatech.training.formationrefactoring.exercice1.internal.ResultSet;
import java.sql.SQLException;
import be.formatech.training.formationrefactoring.exercice1.internal.Statement;
import java.util.Calendar;
import java.util.Properties;

public class ExcellAnomalie {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcellAnomalie.class);
    private static final String ERROR = "ERROR";
    private static final String WARNING="WARNING";
    private static final String INFO="INFO";

    private String[] filenames;

    public static void main(String[] args) {
        try {
            ExcellAnomalie excellAnomalie = new ExcellAnomalie();
            Properties properties = new Properties();

            properties.put("ONSSTRIMESTRE", "201902");
            properties.put("ISORIGINAL", "true");
            properties.put("mail.adress", "toto@ucm.be");
            properties.put("repertoire.rejet.dmf", "C:\\Temp");

            excellAnomalie.executeBatch(properties);
        } catch (Exception e) {
            LOGGER.error("Exception caught ", e);
        }
    }

    protected  void executeBatch(Properties properties) throws Exercice1Exception {

        /* Trimestre demandé sous la forme 'AAAA0T' */
        String onssTrimestre = properties.getProperty("ONSSTRIMESTRE");
        /*
         * Type de lots demandé : - true => lots originaux (non rectificatifs) - false => lots rectificatifs
         */
        String trueOrFalse = properties.getProperty("ISORIGINAL").toLowerCase();

        /* Tester les arguments manquants */
        if (onssTrimestre == null) {
            LOGGER.error("*** FATAL ERROR missing arg 'ONSSTRIMESTRE'.");
        }
        if (trueOrFalse == null) {
            LOGGER.error("*** FATAL ERROR missing arg 'ISORIGINAL'.");
        }

        if (onssTrimestre == null || trueOrFalse == null) {
            return;
        }

        /* Tester les valeurs invalides des arguments */
        boolean quarterOk = true;
        short quarter = 20104;
        try {
            int yyyy0q = Integer.valueOf(onssTrimestre).intValue();
            int yyyy = yyyy0q / 100;
            if (yyyy < 2000 || yyyy > 2099) {
                quarterOk = false;
            }
            int q = yyyy0q % 100;
            if (q < 1 || q > 4) {
                quarterOk = false;
            }
            quarter = (short) ((yyyy * 10) + q);
        } catch (Exception e) {
            quarterOk = false;
        }

        boolean isOriginalOk = (",true,false,".indexOf(trueOrFalse) != -1);
        boolean isOriginal = ("true".equals(trueOrFalse));

        if (!quarterOk) {
            LOGGER.error("*** FATAL ERROR invalid value '" + onssTrimestre + "' for arg 'ONSSTRIMESTRE'.");
        }
        if (!isOriginalOk) {
            LOGGER.error("*** FATAL ERROR invalid value '" + trueOrFalse + "' for arg 'ISORIGINAL'.");
        }
        if (!quarterOk || !isOriginalOk) {
            return;
        }

        String repertoire = "";
        String mail_prefix_adress = "nobody"; // si un problème subsiste ...
        String mail_adress = properties.getProperty("mail.adress");
        repertoire = properties.getProperty("repertoire.rejet.dmf");
        if (!repertoire.substring(repertoire.length() - 1, repertoire.length()).equals("/")) {
            repertoire = repertoire + "/";
        }
        int ndx = -1;
        if (mail_adress != null) {
            ndx = mail_adress.indexOf("@");
        }
        if (ndx != -1) {
            mail_prefix_adress = mail_adress.substring(0, ndx);
        }

        String yearOfQuarter = Integer.valueOf(quarter / 10).toString();
        String quarterOfYear = Integer.valueOf(quarter - (quarter / 10) * 10).toString();

        Calendar now = Calendar.getInstance();
        String yyyy = Integer.valueOf(now.get(Calendar.YEAR)).toString();
        String mm = Integer.valueOf(now.get(Calendar.MONTH) + 1).toString();
        while (mm.length() < 2) {
            mm = "0" + mm;
        }
        String dd = Integer.valueOf(now.get(Calendar.DAY_OF_MONTH)).toString();
        while (dd.length() < 2) {
            dd = "0" + dd;
        }
        String hh = Integer.valueOf(now.get(Calendar.HOUR_OF_DAY)).toString();
        while (hh.length() < 2) {
            hh = "0" + hh;
        }
        String mi = Integer.valueOf(now.get(Calendar.MINUTE)).toString();
        while (mi.length() < 2) {
            mi = "0" + mi;
        }
        String ss = Integer.valueOf(now.get(Calendar.SECOND)).toString();
        while (ss.length() < 2) {
            ss = "0" + ss;
        }

        String fileName = mail_prefix_adress + "_DMF_detail_" + yearOfQuarter + "T" + quarterOfYear + "_" + (isOriginal ? "O" : "R") + "_" + yyyy + mm + dd
                + hh + mi + ss;

        /* Construction du chemin d'accès au fichier EXCEL (celui du reporting) */
        /* Construction du nom complet du fichier EXCEL sans l'extension '.xls' */
        String fullPathFileName = repertoire + fileName;


        buildExcel(fullPathFileName, quarter, isOriginal);

        for (String name : this.filenames) {
            try {
                String n = new File(name).getName();
                new File(repertoire + n.substring(0, n.length() - 4) + ".GO").createNewFile();
            } catch (IOException e) {
                throw new Exercice1Exception(e.getMessage(), e);
            }
        }


    }

    private void buildExcel(String fullPathFileName, short quarterLikeYYYYQ, boolean isOriginal) {

        /*
         * Liste et numéro des colonnes demandées suivant l'analyse (pas toutes peuvent être réalisées (voir détail)
         */
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

        // Par souci didactique, les 20 autres colonnes du fichier ont été supprimées... :-)
        // ...

        Statement stmt = null;
        ResultSet rs = null;

        // 1ère passe
        File temp = null;
        String YYYYQ = Short.valueOf(quarterLikeYYYYQ).toString();
        String YYYY0Q = YYYYQ.substring(0, 4) + "0" + YYYYQ.substring(4);
        int nossEndingDate = Util.getNossEndingQuarterDate(quarterLikeYYYYQ);
        try {
            temp = File.createTempFile("test", ".poi");
            temp.deleteOnExit();
            PrintWriter out = new PrintWriter(temp);

            String sql = "select col2, col3, col4, col5, col6, col7, col8, col9a,  col9b,  col9c,  col9d,  col9e,  col9f, col10, col12, col14, col16, col18, col19, col25and26, col27  from ( "
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
                    + YYYY0Q
                    + "' and "
                    + "ol.lotno = olc.lotno and "
                    + "a.identifiant like to_char(ol.lotno) || '.' || olc.empcode || '.______' )";

            // (*) col1 où 1 fait référence à la colonne de la suite
            // présentée dans l'analyse, tandis que 1 seul fait
            // référence au numéro de colonne du SELECT sql

            stmt = Connexion.getConnection().createStatement();

            rs = stmt.executeQuery(sql);

            while (rs.next()) {

                String anomalie = rs.getString(20); //
                // if (filterOn)
                // anomalie = filtreAnomalie(anomalie);
                if (anomalie == null || anomalie.length() == 0) {
                    continue;
                }

                String[] line = new String[33];
                for (int j = 0; j < line.length; j++) {
                    line[j] = "";
                    // note : line[x-1] correspond à Col.X dans la suite
                    // définie dans l'analyse
                }

                line[0] = YYYYQ.substring(0, 4) + "T" + YYYYQ.substring(4); // col.1

                String refSuc = rs.getString(1);
                if (refSuc == null) {
                    refSuc = "";
                }
                line[1] = refSuc; // col.2

                String nomSuc = rs.getString(2);
                if (nomSuc == null) {
                    nomSuc = "";
                }
                line[2] = nomSuc; // col.3

                String refEquip = rs.getString(3);
                if (refEquip == null) {
                    refEquip = "";
                }
                line[3] = refEquip; // col.4

                String refGest = rs.getString(4);
                if (refGest == null) {
                    refGest = "";
                }
                line[4] = refGest; // col.5

                String nomGest = rs.getString(5);
                if (nomGest == null) {
                    nomGest = "";
                }
                line[5] = nomGest; // col.6

                Long lotNo = rs.getLong(6);
                line[6] = lotNo.toString(); // col.7

                String lottype = rs.getString(7);
                line[7] = ("O".equals(lottype) ? "O" : "R"); // col.8

                line[8] = rs.getString(8); // col.9a
                line[9] = rs.getString(9); // col.9b
                line[10] = rs.getString(10); // col.9c
                line[11] = rs.getString(11); // col.9d
                line[12] = rs.getString(12); // col.9c
                line[13] = rs.getString(13); // col.9d

                String empCode = rs.getString(14);
                line[14] = empCode; // col.10

            }
        } catch (IOException | SQLException | Exercice1Exception e) {
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

    }
}
