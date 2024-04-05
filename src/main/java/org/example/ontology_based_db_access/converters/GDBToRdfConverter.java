package org.example.ontology_based_db_access.converters;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.example.ontology_based_db_access.ontology.RDFModel;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Value;


public class GDBToRdfConverter  {
    private final String tableType = "gdb";

    public Model convert(Result result, String nodeName) {
        RDFModel rdf = new RDFModel(tableType, nodeName);
        Model model = rdf.getModel();

        while (result.hasNext()) {
            Record record = result.next();
            org.neo4j.driver.types.Node node = record.get("n").asNode();
            for (String key : node.keys()) {
                Value value = node.get(key);
                Resource subject = model.createResource(node.elementId());
                Property predicate = model.createProperty(rdf.getNS(), key);
                String stringValue = value.toString().replaceAll("\"", "");
                model.add(subject, predicate, stringValue);
            }
        }
        return model;
    }

}
