package be.formatech.training.formationrefactoring.exercice1;

import be.formatech.training.formationrefactoring.exercice1.internal.ResultSet;
import be.formatech.training.formationrefactoring.exercice1.internal.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class EmployeurDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeurDao.class);

    String fetchEmpName(int nossEndingDate, String empCode) {
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

}
