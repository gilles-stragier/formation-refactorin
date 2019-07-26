package be.formatech.training.formationrefactoring.exercice1.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class Statement {
    private static final Logger LOGGER = LoggerFactory.getLogger(Statement.class);

    private static final String QUERY_LOTS = "select col2, col3, col4, col5, col6, col7, col8, col9a,  col9b,  col9c,  col9d,  col9e,  col9f, col10, col12, col14, col16, col18, col19, col25and26, col27  from ( select f_get_succuref_for_employer(sysdate, '1', olc.empcode) \t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tas col2, f_getvaleur('SUCCU', 'NOMSUCCU',  f_get_succuid_for_employer(CURRENT_DATE, '1', olc.empcode), CURRENT_DATE, '')  \t\t\t\t\t\t\t\tas col3, substr(f_getparametre('CONTACTCLI', 'ENTITEID', f_get_contactid_for_employer(CURRENT_DATE, '1', olc.empcode), CURRENT_DATE), instr(f_getparametre('CONTACTCLI', 'ENTITEID', f_get_contactid_for_employer(CURRENT_DATE, '1', olc.empcode), CURRENT_DATE), 'EQUIPE:') + 16,2) as col4, f_get_contactid_for_employer(CURRENT_DATE, '1', olc.empcode)                                                                                   as col5, f_get_contactlname_for_emp(CURRENT_DATE, '1', olc.empcode) || ' ' || f_get_contactfname_for_emp(CURRENT_DATE, '1', olc.empcode)                as col6, ol.lotno as col7, ol.lottype as col8, ol.statut as col9a, ol.statutactualisation as col9b, olc.statut as col9c, olc.statutactualisation as col9d, ' ' as col9e, ' ' as col9f, olc.empcode as col10, ' ' as col12, ' ' as col14, ' ' as col16, -1 as col18, ' ' as col19, a.anomalie as col25and26, ol.timestatut as col27 from onsslot ol, onsslotclient olc, onssanomalie a where ol.lottype not in ('R') and ol.travtrimestre = '201902' and ol.lotno = olc.lotno and a.identifiant like to_char(ol.lotno) || '.' || olc.empcode || '.______' )";
    private static final String QUERY_SUCCU_038287 = "select v3.valeurtext from valeur v3 where v3.entite = 'SUCCU' and v3.parametre = 'NOMSUCCU' and v3.identifiant = (select f_get_succuid_for_employer(sysdate, '1', '038287') from dual) and v3.debutvalidite <= to_date('20190630', 'yyyymmdd') and v3.finvalidite >= to_date('20190630', 'yyyymmdd') ";
    private static final String QUERY_SUCCU_043711 = "select v3.valeurtext from valeur v3 where v3.entite = 'SUCCU' and v3.parametre = 'NOMSUCCU' and v3.identifiant = (select f_get_succuid_for_employer(sysdate, '1', '043711') from dual) and v3.debutvalidite <= to_date('20190630', 'yyyymmdd') and v3.finvalidite >= to_date('20190630', 'yyyymmdd') ";
    private static final String QUERY_NOM_038287 = "select v11.valeurtext from valeur v11 where v11.entite = 'EMPLOYEUR' and v11.parametre = 'EMPNOM' and v11.identifiant = '038287' and v11.debutvalidite <= to_date('20190630', 'yyyymmdd') and v11.finvalidite >= to_date('20190630', 'yyyymmdd') ";
    private static final String QUERY_NOM_043711= "select v11.valeurtext from valeur v11 where v11.entite = 'EMPLOYEUR' and v11.parametre = 'EMPNOM' and v11.identifiant = '043711' and v11.debutvalidite <= to_date('20190630', 'yyyymmdd') and v11.finvalidite >= to_date('20190630', 'yyyymmdd') ";

    private static final Object[][] RESULT_LOTS = {
            {"N", "Namur", "91", "N6", "Géraldine Macaux", "1234", "O", "03", " ", "03", "", " ", " ", "038287", " ", " ", " ", "-1", " ", "***Error : Merveilleuse anomalie.", "2019-07-13T11:05:12"},
            {"N", "Namur", "92", "NA", "Cynthia Benedetti", "1234", "O", "03", " ", "03", "", " ", " ", "043711", " ", " ", " ", "-1", " ", "***Error : Une autre anomalie.", "2019-07-13T11:06:18"}
    };

    private static final Object[][] RESULT_SUCCU_038287 = {
            {"Namur"}
    };

    private static final Object[][] RESULT_SUCCU_043711 = {
            {"Namur"}
    };

    private static final Object[][] RESULT_NOM_038287 = {
            {"Cesi"}
    };

    private static final Object[][] RESULT_NOM_043711 = {
            {"Traiteur Paulus"}
    };


    public boolean closed = true;

    public ResultSet executeQuery(String sql) throws SQLException{
        if (sql == null) {
            throw new SQLException("Query may not be null.");
        }

        LOGGER.info("Query: " + sql);

        closed = false;
        if (QUERY_LOTS.equals(sql)) {
            return new ResultSet(RESULT_LOTS);
        }
        if (QUERY_SUCCU_038287.equals(sql)) {
            return new ResultSet(RESULT_SUCCU_038287);
        }
        if (QUERY_SUCCU_043711.equals(sql)) {
            return new ResultSet(RESULT_SUCCU_043711);
        }
        if (QUERY_NOM_038287.equals(sql)) {
            return new ResultSet(RESULT_NOM_038287);
        }
        if (QUERY_NOM_043711.equals(sql)) {
            return new ResultSet(RESULT_NOM_043711);
        }

        throw new SQLException("Mock is not smart enough to understand your query... :-(");
    }

    public void close() throws SQLException {
        if (closed) {
            throw new SQLException("Statement already closed.");
        }
        closed = true;
    }
}
