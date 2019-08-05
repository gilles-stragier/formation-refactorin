package be.formatech.training.formationrefactoring.exercice1;

import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TrimestreTest {

    @Test
    public void testConstructor () {
        Trimestre t = new Trimestre("20161");
        Assert.assertEquals(2016, t.getAnnee());
        Assert.assertEquals(1, t.getNumero());

        t = new Trimestre("201703");
        Assert.assertEquals(2017, t.getAnnee());
        Assert.assertEquals(3, t.getNumero());

    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullStringTrimestre() {
        new Trimestre((String)null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullDateTrimestre() {
        new Trimestre((LocalDate)null);
    }

    @Test
    public void testContructorDate_1() {
        Trimestre t = new Trimestre(LocalDate.of(2017, 1, 2));
        Assert.assertEquals(2017, t.getAnnee());
        Assert.assertEquals(1, t.getNumero());
    }
    @Test
    public void testContructorDate_2(){
        Trimestre t = new Trimestre(LocalDate.of(2017, 2, 2));
        Assert.assertEquals(2017, t.getAnnee());
        Assert.assertEquals(1, t.getNumero());

    }
    @Test
    public void testContructorDate_3(){
        Trimestre t = new Trimestre(LocalDate.of(2017, 3, 2));
        Assert.assertEquals(2017, t.getAnnee());
        Assert.assertEquals(1, t.getNumero());

    }
    @Test
    public void testContructorDate_4(){
        Trimestre t = new Trimestre(LocalDate.of(2017, 4, 2));
        Assert.assertEquals(2017, t.getAnnee());
        Assert.assertEquals(2, t.getNumero());

    }
    @Test
    public void testContructorDate_5(){
        Trimestre t = new Trimestre(LocalDate.of(2017, 5, 2));
        Assert.assertEquals(2017, t.getAnnee());
        Assert.assertEquals(2, t.getNumero());

    }
    @Test
    public void testContructorDate_6(){
        Trimestre t = new Trimestre(LocalDate.of(2017, 6, 2));
        Assert.assertEquals(2017, t.getAnnee());
        Assert.assertEquals(2, t.getNumero());

    }
    @Test
    public void testContructorDate_7(){
        Trimestre t = new Trimestre(LocalDate.of(2017, 7, 2));
        Assert.assertEquals(2017, t.getAnnee());
        Assert.assertEquals(3, t.getNumero());

    }
    @Test
    public void testContructorDate_8(){
        Trimestre t = new Trimestre(LocalDate.of(2017, 8, 2));
        Assert.assertEquals(2017, t.getAnnee());
        Assert.assertEquals(3, t.getNumero());

    }
    @Test
    public void testContructorDate_9(){
        Trimestre t = new Trimestre(LocalDate.of(2017, 9, 2));
        Assert.assertEquals(2017, t.getAnnee());
        Assert.assertEquals(3, t.getNumero());

    }
    @Test
    public void testContructorDate_10(){
        Trimestre t = new Trimestre(LocalDate.of(2017, 10, 2));
        Assert.assertEquals(2017, t.getAnnee());
        Assert.assertEquals(4, t.getNumero());

    }
    @Test
    public void testContructorDate_11(){
        Trimestre t = new Trimestre(LocalDate.of(2017, 11, 2));
        Assert.assertEquals(2017, t.getAnnee());
        Assert.assertEquals(4, t.getNumero());

    }
    @Test
    public void testContructorDate_12(){
        Trimestre t = new Trimestre(LocalDate.of(2017, 12, 2));
        Assert.assertEquals(2017, t.getAnnee());
        Assert.assertEquals(4, t.getNumero());

    }

    @Test(expected = IllegalArgumentException.class)
    public void testBefore2000Trimestre() {
        Trimestre t = new Trimestre("199901");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test5thTrimestre() {
        Trimestre t = new Trimestre("20015");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test5thTrimestreBis() {
        Trimestre t = new Trimestre("200105");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test15thTrimestreBis() {
        Trimestre t = new Trimestre("200115");
    }

    @Test
    public void testLastDayOfTrimestre() {
        Trimestre t = new Trimestre("20161");
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(2016, 2, 31);
        Assert.assertEquals(cal.getTime(), t.lastDay());

        t = new Trimestre("20162");
        cal.set(2016, 5, 30);
        Assert.assertEquals(cal.getTime(), t.lastDay());

        t = new Trimestre("20163");
        cal.set(2016, 8, 30);
        Assert.assertEquals(cal.getTime(), t.lastDay());

        t = new Trimestre("20164");
        cal.set(2016, 11, 31);
        Assert.assertEquals(cal.getTime(), t.lastDay());
    }

    @Test
    public void testFirstDayOfTrimestre() {
        Trimestre t = new Trimestre("20161");
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(2016, 0, 1);
        Assert.assertEquals(cal.getTime(), t.firstDay());

        t = new Trimestre("20162");
        cal.set(2016, 3, 1);
        Assert.assertEquals(cal.getTime(), t.firstDay());

        t = new Trimestre("20163");
        cal.set(2016, 6, 1);
        Assert.assertEquals(cal.getTime(), t.firstDay());

        t = new Trimestre("20164");
        cal.set(2016, 9, 1);
        Assert.assertEquals(cal.getTime(), t.firstDay());
    }

    @Test
    public void testNextTrimestre() {
        Trimestre t = new Trimestre("20161");
        Trimestre t1 = t.next();
        Assert.assertEquals(new Trimestre(2016, 2), t1);

        t = new Trimestre("20162");
        t1 = t.next();
        Assert.assertEquals(new Trimestre(2016, 3), t1);

        t = new Trimestre("20163");
        t1 = t.next();
        Assert.assertEquals(new Trimestre(2016, 4), t1);

        t = new Trimestre("20164");
        t1 = t.next();
        Assert.assertEquals(new Trimestre(2017, 1), t1);
    }

    @Test
    public void testMe() {
        Trimestre t1 = new Trimestre(2016, 1);
        Trimestre t2 = new Trimestre(2016, 1);
        Assert.assertEquals(t2, t1);

    }

    @Test
    public void testPreviousTrimestre() {
        Trimestre t = new Trimestre("20161");
        Trimestre t1 = t.previous();
        Assert.assertEquals(new Trimestre(2015, 4), t1);

        t = new Trimestre("20162");
        t1 = t.previous();
        Assert.assertEquals(new Trimestre(2016, 1), t1);

        t = new Trimestre("20163");
        t1 = t.previous();
        Assert.assertEquals(new Trimestre(2016, 2), t1);

        t = new Trimestre("20164");
        t1 = t.previous();
        Assert.assertEquals(new Trimestre(2016, 3), t1);
    }

    @Test
    public void testFormat() {
        Trimestre t = new Trimestre("20161");
        Assert.assertEquals("20161", t.asYYYYN());
        Assert.assertEquals("201601", t.asYYYYNN());
    }

    @Test
    public void testFormatYYYYTN() {
        Trimestre t = new Trimestre("20161");
        Assert.assertEquals("2016T1", t.asYYYYTN());
    }

    @Test
    public void testLastLocalDate() {
        Trimestre t = new Trimestre(2017, 2);
        LocalDate d = LocalDate.of(2017, 6, 30);
        Assert.assertEquals(d, t.lastLocalDate());
    }

    @Test
    public void testFirstLocalDate() {
        Trimestre t = new Trimestre(2017, 2);
        LocalDate d = LocalDate.of(2017, 4, 1);
        Assert.assertEquals(d, t.firstLocalDate());
    }

    @Test
    public void testFirstPeriode() {
        Trimestre t = new Trimestre(2017, 2);
        Assert.assertEquals("201704", t.firstPeriode());
    }

    @Test
    public void testLastPeriode() {
        Trimestre t = new Trimestre(2017, 2);
        Assert.assertEquals("201706", t.lastPeriode());
    }

    @Test
    public void getQuarterMonths() {
        Trimestre trimestre = new Trimestre("201701");
        List<String> months = trimestre.getQuarterMonths();
        assertEquals("201701", months.get(0));
        assertEquals("201702", months.get(1));
        assertEquals("201703", months.get(2));;

        trimestre = new Trimestre("201702");
        months = trimestre.getQuarterMonths();
        assertEquals("201704", months.get(0));
        assertEquals("201705", months.get(1));
        assertEquals("201706", months.get(2));

        trimestre = new Trimestre("201703");
        months = trimestre.getQuarterMonths();
        assertEquals("201707", months.get(0));
        assertEquals("201708", months.get(1));
        assertEquals("201709", months.get(2));

        trimestre = new Trimestre("201704");
        months = trimestre.getQuarterMonths();
        assertEquals("201710", months.get(0));
        assertEquals("201711", months.get(1));
        assertEquals("201712", months.get(2));
    }
}
