package be.formatech.training.formationrefactoring.exercice1;


import be.formatech.training.formationrefactoring.exercice1.internal.Connection;

public class Connexion {
    private static Connection connection;

    public static Connection getConnection() throws Exercice1Exception {
        if (connection == null) {
            connection = new Connection();
        }
        return connection;
    }
}
