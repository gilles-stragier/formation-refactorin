package be.formatech.training.formationrefactoring.exercice1;

import be.formatech.training.formationrefactoring.exercice1.internal.ResultSet;

import java.sql.SQLException;

public class AnomalyRecord {

    private String anomaly;
    private String refSuc;

    public AnomalyRecord(ResultSet rs) throws SQLException {
        this.anomaly = rs.getString(20);
        this.refSuc = rs.getString(1);
    }

    public String getAnomaly() {
        return anomaly;
    }

    public String getRefSuc() {
        return refSuc;
    }
}
