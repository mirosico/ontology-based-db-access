package org.example.ontology_based_db_access.converters;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.example.ontology_based_db_access.ontology.RDFModel;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;


public class RDBToRdfConverter {
    private final String tableType = "rdb";

    public Model convert(ResultSet rs, String databaseName) {
        RDFModel rdf = new RDFModel(tableType, databaseName);
        Model model = rdf.getModel();

        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            while (rs.next()) {
                Resource subject = model.createResource(rs.getString(1));
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    String key = rsmd.getColumnName(i);
                    String columnValue = rs.getString(i);
                    Property predicate = model.createProperty(rdf.getNS(), key);
                    Statement statement = model.createStatement(subject, predicate, columnValue);
                    model.add(statement);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return model;
    }
}
