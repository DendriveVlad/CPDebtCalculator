package ru.itsokay.launcher.Database;

import java.sql.SQLException;

public class Executor {
    private Connector conn;
    public Executor() {
        this.conn = new Connector();
    }

    public void insert(String table, String data) {
        try {
            this.conn.execute("INSERT INTO " + table + " VALUES('" + data + "')");
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void update(String table, String data, int index) {
        try {
            this.conn.execute("UPDATE " + table + " SET rawdata = '" + data + "' WHERE rowid = " + (index + 1));
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void delete(String table, int index) {
        try {
            this.conn.execute("DELETE FROM " + table + " WHERE rowid = " + (index + 1));
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public String select(String table) {
        try {
            return this.conn.execute("SELECT rawdata FROM " + table);
        } catch (SQLException e) {
            System.out.println(e);
            return "";
        }
    }

    public void end() {
        this.conn.disconnect();
    }
}
