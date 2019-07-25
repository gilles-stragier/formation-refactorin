package be.formatech.training.formationrefactoring.exercice1.internal;

import java.sql.SQLException;

public class Connection {
    public boolean closed = true;

    public Connection() {
        closed = false;
    }

    public Statement createStatement() {
        return new Statement();
    }

    public void close() throws SQLException {
        if (closed) {
            throw new SQLException("Connection already closed.");
        }
        closed = false;
    }
}
