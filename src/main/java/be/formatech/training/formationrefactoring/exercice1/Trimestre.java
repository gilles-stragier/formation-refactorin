package be.formatech.training.formationrefactoring.exercice1;


import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Classe permettant de représenter et manipuler un trimestre
 */
public class Trimestre implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final String TRIMESTRE_FORMAT_STRING = "%1$04d%2$02d";
    private static final int FOURTH_QUARTER = 4;
    private static final int NUMBER_OF_MONTHS_IN_QUARTER = 3;
    private int annee;  // AAAA
    private int numero; //T

    /**
     * Crée un trimestre
     *
     * @param annee
     * @param numero numéro du trimestre (nombre entre 1 et 4)
     */
    public Trimestre(int annee, int numero) {
        this.annee = annee;
        this.numero = numero;
    }

    /**
     * Création d'un trimestre sur base d'une des représentations {@link String} suivantes :
     * <ul>
     * <li><code>YYYYNN</code> où <code>YYYY</code> est l'année et NN le numéro du trimestre compris entre 01 et 04</li>
     * <li><code>YYYYN</code> où <code>YYYY</code> est l'année et N le numéro du trimestre compris entre 1 et 4</li>
     * </ul>
     * L'année est explicitement limitée à la tranche 2000 - 2999.
     * <br/>
     * Libre à vous d'implémenter d'autres formats tels que YYYY/N ou encore NT/YYYY (ex: 1T2017)...
     *
     * @param trimestre
     */
    public Trimestre(String trimestre) {
        if (trimestre == null) {
            throw new IllegalArgumentException("Cannot parse null as Trimestre.");
        }
        // YYYYYNN or YYYYN ... expects YYYY >= 2000 and 01 <= NN <= 04.
        if (trimestre.matches("2\\d{3}0?[1234]")) {
            annee = Integer.parseInt(trimestre.substring(0, 4));
            numero = Integer.parseInt(trimestre.substring(4));
            return;
        }

        // YYYY/N

        //NT/YYYY    1T/2017
        //....
        throw new IllegalArgumentException("Cannot parse " + trimestre + " as Trimestre.");
    }

    public Trimestre(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Cannot parse null as Trimestre.");
        }
        this.annee = date.getYear();
        this.numero = ((date.getMonthValue() - 1) / 3) + 1;
    }

    /**
     * Retourne le dernier jour du trimestre. Les proriétés liées à l'heure sont à zéro.
     *
     * @return
     */
    public Date lastDay() {
        int lastMonth = numero * 3;
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.YEAR, annee);
        cal.set(Calendar.MONTH, lastMonth - 1);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    /**
     * Retourne le dernier jour du trimestre.
     * @return
     */
    public LocalDate lastLocalDate() {
        LocalDate d =  LocalDate.of(annee, lastMonth(), 1);
        return d.withDayOfMonth(d.lengthOfMonth());
    }

    /**
     * Retourne le premier jour du trimestre. Les proriétés liées à l'heure sont à zéro.
     *
     * @return
     */
    public Date firstDay() {
        int firstMonth = (numero - 1) * 3;
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.YEAR, annee);
        cal.set(Calendar.MONTH, firstMonth);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

    /**
     * Retourne le premier jour du trimestre.
     * @return
     */
    public LocalDate firstLocalDate() {
        return LocalDate.of(annee, firstMonth(), 1);
    }

    /**
     * Retourne la première période du trimestre au format YYYYMM.
     * @return
     */
    public String firstPeriode(){
        return String.format(TRIMESTRE_FORMAT_STRING, annee, firstMonth());
    }

    /**
     * Retourne la première période du trimestre au format YYYYMM.
     * @return
     */
    public String lastPeriode(){
        return String.format(TRIMESTRE_FORMAT_STRING, annee, lastMonth());
    }

    /**
     *
     * return the first month of the trimestre
     *
     * @return first month of the trimestre
     */
    public int firstMonth(){
        return ((numero - 1) * 3) + 1;
    }

    /**
     *
     * return the last month of the trimestre
     *
     * @return last month of the trimestre
     */
    public int lastMonth(){
        return ((numero - 1) * 3) + 3;
    }

    /**
     * Retourne le {@Trimestre} suivant.
     *
     * @return
     */
    public Trimestre next() {
        if (numero < 4) {
            return new Trimestre(annee, numero + 1);
        } else {
            return new Trimestre(annee + 1, 1);
        }
    }

    /**
     * Retourne le {@Trimestre} précédent.
     *
     * @return
     */
    public Trimestre previous() {
        if (numero > 1) {
            return new Trimestre(annee, numero - 1);
        } else {
            return new Trimestre(annee - 1, 4);
        }
    }

    public boolean contains(LocalDate date){
        if (date == null) {
            throw new IllegalArgumentException("La date ne peut être nulle.");
        }
        return this.contains(Date.from(date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
    }

    /**
     * Check if the date is the trimestre
     *
     * @param date date to check
     * @return true if it's in the trimestre
     */
    public boolean contains(Date date){
        boolean result = true;
        // si avant début, false || si après fin, false
        if (date.before(this.firstDay()) || date.after(this.lastDay())) {
            result =  false;
        }
        // sinon OK
        return result;
    }

    public String asYYYYN() {
        return String.format("%1$04d%2$01d", annee, numero);
    }

    public String asYYYYNN() {
        return String.format("%1$04d%2$02d", annee, numero);
    }

    public String asYYYYTN() {
        return String.format("%1$04dT%2$01d", annee, numero);
    }

    public int getNossEndingQuarterDateAsInt() {
        int endingQuarterDate = (getNumero() / 10) * 10000;
        switch (getNumero() % 10) {
            case 1:
                endingQuarterDate += 331;
                break;
            case 2:
                endingQuarterDate += 630;
                break;
            case 3:
                endingQuarterDate += 930;
                break;
            case 4:
                endingQuarterDate += 1231;
                break;
        }
        return endingQuarterDate;
    }

    public Short asYYYYNNShort() {
        return Short.valueOf(asYYYYN());
    }

    public int getAnnee() {
        return annee;
    }

    public int getNumero() {
        return numero;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Trimestre trimestre = (Trimestre) o;

        if (annee != trimestre.annee) {
            return false;
        }
        return numero == trimestre.numero;
    }

    @Override
    public int hashCode() {
        int result = annee;
        result = 31 * result + numero;
        return result;
    }

    /**
     * Construire une liste qui contient les mois d'un trimestre
     *
     * @return liste des mois d'un trimestre
     */
    public List<String> getQuarterMonths() {
        int maxMonthInQuarter = getNumero() * NUMBER_OF_MONTHS_IN_QUARTER;
        List<String > months = new ArrayList();
        for (int i=0; i<=2; i++) {
            if(getNumero() == FOURTH_QUARTER){
                months.add(getAnnee() + "" + (maxMonthInQuarter-2 + i));
            }
            else {
                months.add(getAnnee() + "0" + (maxMonthInQuarter - 2 + i));
            }
        }
        return months;
    }
}
