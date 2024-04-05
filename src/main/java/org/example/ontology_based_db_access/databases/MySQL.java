package org.example.ontology_based_db_access.databases;

import org.apache.jena.rdf.model.Model;
import org.example.ontology_based_db_access.converters.RDBToRdfConverter;

import java.sql.*;
import java.util.ArrayList;


public class MySQL implements DB {
    private static Connection conn;


    public void connect(String DB_URL, String DB_USER, String DB_PASS) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        try {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public void close() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String[] getCollections() {
        if (!isConnected())
            return null;
        try {
            DatabaseMetaData md = conn.getMetaData();
            ResultSet rs = md.getTables(conn.getCatalog(), null, "%", null);
            ArrayList<String> tables = new ArrayList<>();
            while (rs.next()) {
                tables.add(rs.getString("TABLE_NAME"));
            }
            return tables.toArray(new String[0]);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String[] getCollectionProps(String tableName) {
        if (!isConnected())
            return null;
        ArrayList<String> columns = new ArrayList<>();
        try {
            ResultSet rs = conn.getMetaData().getColumns(conn.getCatalog(), null, tableName, null);
            while (rs.next()) {
                columns.add(rs.getString("COLUMN_NAME"));
            }
            return columns.toArray(new String[0]);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public String getCatalog() {
        if (!isConnected())
            return null;
        try {
            return conn.getCatalog();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getUserName() {
        if (!isConnected())
            return null;
        try {
            return conn.getMetaData().getUserName();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getTableName(String sql) {
        String[] tokens = sql.split(" ");
        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i].equalsIgnoreCase("from")) {
                return tokens[i + 1];
            }
        }
        return null;
    }

    public Model convertToRDF(String sql) {
        if (!isConnected())
            return null;
        try {
            Statement stmt = conn.createStatement();
            String tableName = getTableName(sql);
            ResultSet rs = stmt.executeQuery(sql);
            RDBToRdfConverter converter = new RDBToRdfConverter();
            return converter.convert(rs, tableName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}