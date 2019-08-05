package be.formatech.training.formationrefactoring.exercice1;

public class ReportLine {

    private Trimestre trimestre;
    private AnomalyRecord anomalyRecord;
    private String libelleAnomaly;
    private String rejCat;

    public ReportLine(Trimestre trimestre, AnomalyRecord anomalyRecord, String rejCat, String libelleAnomaly) {
        this.trimestre = trimestre;
        this.anomalyRecord = anomalyRecord;
        this.rejCat = rejCat;
        this.libelleAnomaly = libelleAnomaly;
    }

    public String[] toLine() {
        String[] line = new String[33];
        for (int j = 0; j < line.length; j++) {
            line[j] = "";
        }

        line[0] = trimestre.asYYYYTN();
        line[1] = anomalyRecord.getRefSuc();
        line[2] = anomalyRecord.getNomSuc();
        line[3] = anomalyRecord.getRefEquip();
        line[4] = anomalyRecord.getRefGest();
        line[5] = anomalyRecord.getNomGest();
        line[6] = anomalyRecord.getLotNo();
        line[7] = ("O".equals(anomalyRecord.getLottype()) ? "O" : "R"); // col.8

        line[8] = anomalyRecord.getStatut();
        line[9] = anomalyRecord.getStatutActualisation();
        line[10] = anomalyRecord.getOlc_statut();
        line[11] = anomalyRecord.getOlc_statutActualisation();
        line[12] = " ";
        line[13] = " ";
        line[14] = anomalyRecord.getEmpcode();

        String yyyy_mm_dd = anomalyRecord.getTimeStatut().toString();
        String dd_mm_yyyy = yyyy_mm_dd.substring(8) + "-" + yyyy_mm_dd.substring(5, 7) + "-" + yyyy_mm_dd.substring(0, 4);
        line[31] = dd_mm_yyyy; // col.27

        line[29] = rejCat;
        line[30] = libelleAnomaly;

        return line;

    }

}
