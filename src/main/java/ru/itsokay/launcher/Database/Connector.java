package ru.itsokay.launcher.Database;

import java.io.File;
import java.sql.*;

public class Connector {
    private Connection connection;
    private Statement st;
    private ResultSet rs;

    public Connector() {
        String FileFolder = null;
        String os = System.getProperty("os.name").toUpperCase();
        if (os.contains("WIN")) FileFolder = System.getenv("APPDATA") + "\\PartyCalc\\";
        if (os.contains("MAC")) FileFolder = System.getProperty("user.home") + "/Library/\"Application Support\"/PartyCalc/";
        if (os.contains("NUX")) FileFolder = System.getProperty("user.dir") + ".PartyCalc.";

        if (FileFolder == null) return;
        File dir = new File(FileFolder);
        if (!dir.exists()) if (!dir.mkdir()) return;

        try {
            Class.forName("org.sqlite.JDBC");

            this.connection = DriverManager.getConnection("jdbc:sqlite:" + FileFolder + "base.db");
            this.st = this.connection.createStatement();

            String[] types = {"TABLE"};
            this.rs = this.connection.getMetaData().getTables(null, null, "%", types);

            if (!this.rs.next()) {
                this.st.execute("CREATE TABLE products (rawdata TEXT)");
                this.st.execute("CREATE TABLE members (rawdata TEXT)");
            }
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String execute(String request) throws SQLException {
        StringBuilder result = new StringBuilder();
        this.st.execute(request);
        try {
            this.rs = this.st.getResultSet();
            while (this.rs.next()) {
                result.append(this.rs.getString("rawdata")).append("%");
            }
            return result.toString();
        } catch (NullPointerException e) {
            return "There is nothing to return";
        }
    }

    public void disconnect() {
        if (this.connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
