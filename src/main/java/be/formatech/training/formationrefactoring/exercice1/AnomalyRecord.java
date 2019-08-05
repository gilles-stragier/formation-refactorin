package be.formatech.training.formationrefactoring.exercice1;

import be.formatech.training.formationrefactoring.exercice1.internal.ResultSet;

import java.sql.Date;
import java.sql.SQLException;

public class AnomalyRecord {

    private final String lottype;
    private final String anomaly;
    private final String refSuc;
    private final String nomSuc;
    private final String refEquip;
    private final String refGest;
    private final String nomGest;
    private final String lotNo;
    private final String statut;
    private final String statutActualisation;
    private final String olc_statut;
    private final String olc_statutActualisation;
    private final String empcode;

    private final Date timeStatut;

    public AnomalyRecord(ResultSet rs) throws SQLException {
        this.anomaly = rs.getString(20);
        this.refSuc = rs.getString(1);
        this.nomSuc = rs.getString(2);
        this.refEquip = rs.getString(3);
        this.refGest = rs.getString(4);
        this.nomGest = rs.getString(5);
        this.lotNo = Long.toString(rs.getLong(6));
        this.lottype = rs.getString(7);
        this.statut = rs.getString(8);
        this.statutActualisation = rs.getString(9);
        this.olc_statut = rs.getString(10);
        this.olc_statutActualisation = rs.getString(11);
        this.empcode = rs.getString(14);
        this.timeStatut = rs.getDate(21);
    }

    public String getNomSuc() {
        return nomSuc;
    }

    public String getRefEquip() {
        return refEquip;
    }

    public String getRefGest() {
        return refGest;
    }

    public String getNomGest() {
        return nomGest;
    }

    public String getLotNo() {
        return lotNo;
    }

    public String getAnomaly() {
        return anomaly;
    }

    public String getRefSuc() {
        return refSuc;
    }

    public String getLottype() {
        return lottype;
    }


    public String getStatut() {
        return statut;
    }

    public String getStatutActualisation() {
        return statutActualisation;
    }

    public String getOlc_statut() {
        return olc_statut;
    }

    public String getOlc_statutActualisation() {
        return olc_statutActualisation;
    }

    public String getEmpcode() {
        return empcode;
    }

    public Date getTimeStatut() {
        return timeStatut;
    }

}
