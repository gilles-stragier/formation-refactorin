package be.formatech.training.formationrefactoring.exercice1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
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

    private void buildExcel(String fullPathFileName, short quarter, boolean isOriginal) {
    }
}
