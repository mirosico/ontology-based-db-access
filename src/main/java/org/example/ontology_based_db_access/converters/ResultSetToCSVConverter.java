package org.example.ontology_based_db_access.converters;

import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ResultSetToCSVConverter {

    private ResultSet rs;

    public ResultSetToCSVConverter(ResultSet rs) {
        this.rs = rs;
    }

    public void export(String filename) {
        try (OutputStream out = new FileOutputStream(filename + ".csv")) {
            ResultSetFormatter.outputAsCSV(out, rs);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
