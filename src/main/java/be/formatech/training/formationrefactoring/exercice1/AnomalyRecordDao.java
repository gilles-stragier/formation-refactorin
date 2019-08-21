package be.formatech.training.formationrefactoring.exercice1;

import be.formatech.training.formationrefactoring.exercice1.internal.ResultSet;
import be.formatech.training.formationrefactoring.exercice1.internal.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AnomalyRecordDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnomalyRecordDao.class);

    List<AnomalyRecord> fetchAnomalyRecords(Trimestre trimestre, boolean isOriginal) {
        Statement stmt = null;
        ResultSet rs = null;

        List<AnomalyRecord> anomalyRecords = new ArrayList<>();
        try {

            // Par souci didactique, seules la partie de la query relative aux employeurs a été conservée... :-)
            String sql = buildQueryForAnomaliesInAQuarter(trimestre, isOriginal);

            // (*) col1 où 1 fait référence à la colonne de la suite
            // présentée dans l'analyse, tandis que 1 seul fait
            // référence au numéro de colonne du SELECT sql

            stmt = Connexion.getConnection().createStatement();

            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                anomalyRecords.add(new AnomalyRecord(rs));

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
        return anomalyRecords;
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


}
