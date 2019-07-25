package be.formatech.training.formationrefactoring.exercice1.internal;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ResultSet {

    private int currentIndex;
    Object[][] data;
    boolean closed = true;

    public ResultSet(Object[][] data) {
        if (data == null) {
            throw new IllegalArgumentException("ResultSet may not be null.");
        }
        this.data = data;
        currentIndex = -1;
        closed = false;
    }

    public boolean hasNext() throws SQLException {
        if (closed) {
            throw new SQLException("ResultSet is closed.");
        }

        return currentIndex < data.length;
    }

    public boolean next() throws SQLException {
        if (closed) {
            throw new SQLException("ResultSet is closed.");
        }
        currentIndex++;
        return hasNext();
    }

    public String getString(int index) throws SQLException {
        if (closed) {
            throw new SQLException("ResultSet is closed.");
        }
        if (index < 1 || index > data[currentIndex].length) {
            throw new SQLException("Index out of bound.");
        }
        return (String) data[currentIndex][index-1];
    }

    public long getLong(int index) throws SQLException {
        if (closed) {
            throw new SQLException("ResultSet is closed.");
        }
        if (index < 1 || index > data[currentIndex].length) {
            throw new SQLException("Index out of bound.");
        }
        return Long.parseLong((String)data[currentIndex][index-1]);
    }

    public int getInt(int index) throws SQLException {
        if (closed) {
            throw new SQLException("ResultSet is closed.");
        }
        if (index < 1 || index > data[currentIndex].length) {
            throw new SQLException("Index out of bound.");
        }
        return Integer.parseInt((String)data[currentIndex][index-1]);
    }

    public java.sql.Date getDate(int index) throws SQLException {
        if (closed) {
            throw new SQLException("ResultSet is closed.");
        }
        if (index < 1 || index > data[currentIndex].length) {
            throw new SQLException("Index out of bound.");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate locald = LocalDate.parse((String) data[currentIndex][index-1], formatter);
        return Date.valueOf(locald);
    }

    public void close() throws SQLException {
        if (closed) {
            throw new SQLException("ResultSet already closed.");
        }
        closed = true;
    }
}
