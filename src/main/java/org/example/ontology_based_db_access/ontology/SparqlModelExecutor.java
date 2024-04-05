package org.example.ontology_based_db_access.ontology;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;

public class SparqlModelExecutor {
    private Model model;

    public SparqlModelExecutor(Model model) {
        this.model = model;
    }

    public ResultSet executeQuery(String sparqlQuery) {
        Query query = QueryFactory.create(sparqlQuery);
        QueryExecution qe = QueryExecutionFactory.create(query, this.model);
        return qe.execSelect();
    }
}
