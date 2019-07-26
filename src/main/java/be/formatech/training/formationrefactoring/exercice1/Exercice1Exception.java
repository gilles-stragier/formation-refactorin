package be.formatech.training.formationrefactoring.exercice1;

public class Exercice1Exception extends Exception {

    public Exercice1Exception(Exception nestedException) {
        super(nestedException);
    }

    public Exercice1Exception(String message, Exception nestedException) {
        super("" + message, nestedException);
    }

    public String getMessage() {
        return this.getMessage();
    }
}
