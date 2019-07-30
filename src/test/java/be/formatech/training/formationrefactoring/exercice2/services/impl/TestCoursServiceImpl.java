package be.formatech.training.formationrefactoring.exercice2.services.impl;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import be.formatech.training.formationrefactoring.exercice2.dao.InterrogationDao;
import be.formatech.training.formationrefactoring.exercice2.entities.Cours;
import be.formatech.training.formationrefactoring.exercice2.entities.Eleve;
import be.formatech.training.formationrefactoring.exercice2.entities.Interrogation;
import be.formatech.training.formationrefactoring.exercice2.services.CoursService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class TestCoursServiceImpl {
    private InterrogationDao interrogationDao;

    private CoursService coursService;

    private Eleve toto;

    private Cours math;
    private Cours gymnastique;
    private Cours laboChimie;

    @Before
    public void init() {
        interrogationDao = Mockito.mock(InterrogationDao.class);
        coursService = new CoursServiceImpl(interrogationDao);

        math = new Cours("Math", "principal");
        gymnastique = new Cours("Gymnastique", "secondaire");
        laboChimie = new Cours("Labo Chimie", "labo");
        toto = new Eleve("Toto", "Lehéros");
    }

    @Test
    public void reussiteCoursPrincipalQuandMoyenneSupEq60EtInterrosSupEq50() {
        List<Interrogation> interrogations = new ArrayList<>();
        interrogations.add(new Interrogation(toto, math, 7.0, 10.0));
        interrogations.add(new Interrogation(toto, math, 5.0, 10.0));

        when(interrogationDao.findInterrogation(eq(toto), eq(math))).thenReturn(interrogations);

        boolean reussi = coursService.coursReussi(toto, math);

        assertTrue("Le cours principal doit réussir", reussi);
    }

    @Test
    public void echecCoursPrincipalQuandMoyenneSupEq60EtUneInterroInf50() {
        List<Interrogation> interrogations = new ArrayList<>();
        interrogations.add(new Interrogation(toto, math, 9.0, 10.0));
        interrogations.add(new Interrogation(toto, math, 4.0, 10.0));

        when(interrogationDao.findInterrogation(eq(toto), eq(math))).thenReturn(interrogations);

        boolean reussi = coursService.coursReussi(toto, math);

        assertFalse("Le cours principal doit être en échec", reussi);
    }

    @Test
    public void echecCoursPrincipalQuandMoyenneInf60EtInterroSup50() {
        List<Interrogation> interrogations = new ArrayList<>();
        interrogations.add(new Interrogation(toto, math, 5.0, 10.0));
        interrogations.add(new Interrogation(toto, math, 6.0, 10.0));

        when(interrogationDao.findInterrogation(eq(toto), eq(math))).thenReturn(interrogations);

        boolean reussi = coursService.coursReussi(toto, math);

        assertFalse("Le cours principal doit être en échec", reussi);
    }

    @Test
    public void reussiteCoursSecondaireQuandToutesInterrosSupEq50() {
        List<Interrogation> interrogations = new ArrayList<>();
        interrogations.add(new Interrogation(toto, gymnastique, 6.0, 10.0));
        interrogations.add(new Interrogation(toto, gymnastique, 5.0, 10.0));

        when(interrogationDao.findInterrogation(eq(toto), eq(gymnastique))).thenReturn(interrogations);

        boolean reussi = coursService.coursReussi(toto, gymnastique);

        assertTrue("Le cours secondaire doit réussir", reussi);
    }

    @Test
    public void echecCoursSecondaireQuandUneInterroInf50() {
        List<Interrogation> interrogations = new ArrayList<>();
        interrogations.add(new Interrogation(toto, gymnastique, 10.0, 10.0));
        interrogations.add(new Interrogation(toto, gymnastique, 4.0, 10.0));

        when(interrogationDao.findInterrogation(eq(toto), eq(gymnastique))).thenReturn(interrogations);

        boolean reussi = coursService.coursReussi(toto, gymnastique);

        assertFalse("Le cours secondaire doit être en échec", reussi);
    }

    @Test
    public void reussiteLaboQuandMoyenneSupEq50() {
        List<Interrogation> interrogations = new ArrayList<>();
        interrogations.add(new Interrogation(toto, laboChimie, 8.0, 10.0));
        interrogations.add(new Interrogation(toto, laboChimie, 4.0, 10.0));

        when(interrogationDao.findInterrogation(eq(toto), eq(laboChimie))).thenReturn(interrogations);

        boolean reussi = coursService.coursReussi(toto, laboChimie);

        assertTrue("Le laboratoir doit réussir", reussi);
    }

    @Test
    public void echecLaboQuandMoyenneInf50() {
        List<Interrogation> interrogations = new ArrayList<>();
        interrogations.add(new Interrogation(toto, laboChimie, 4.0, 10.0));
        interrogations.add(new Interrogation(toto, laboChimie, 5.0, 10.0));

        when(interrogationDao.findInterrogation(eq(toto), eq(laboChimie))).thenReturn(interrogations);

        boolean reussi = coursService.coursReussi(toto, laboChimie);

        assertFalse("Le laboratoir doit être en échec", reussi);
    }

}
