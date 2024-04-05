package org.example.ontology_based_db_access.ontology;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

public class RDFModel {
    private final String BASE_URI = "http://example.com";
    private String prefix;
    private String NS;
    private Model model;

    public RDFModel(String tableType, String tableName) {
        model = ModelFactory.createDefaultModel();
        prefix = tableType + "-" + tableName;
        NS = BASE_URI + "/" + prefix + "#";
        model.setNsPrefix(prefix, NS);
    }

    public String getPrefix() {
        return prefix;
    }

    public String getBaseUri() {
        return BASE_URI;
    }

    public String getNS() {
        return NS;
    }

    public Model getModel() {
        return model;
    }
}
