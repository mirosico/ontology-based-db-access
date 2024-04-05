package org.example.ontology_based_db_access.converters;

import com.mongodb.client.FindIterable;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.bson.Document;
import org.example.ontology_based_db_access.ontology.RDFModel;

import java.util.Map;
import java.util.Set;

public class ODBToRdfConverter {

    private final String tableType = "odb";
    private final String COLLECTION_ID = "_id";

    public Model convert(FindIterable<Document> documents, String databaseName) {
        RDFModel rdf = new RDFModel(tableType, databaseName);
        Model model = rdf.getModel();
        for (Document document : documents) {
            Set<Map.Entry<String, Object>> entries = document.entrySet();
            for (Map.Entry<String, Object> entry : entries) {
                String key = entry.getKey();
                Object value = entry.getValue();

                Resource subject = model.createResource(document.get(COLLECTION_ID).toString());
                Property predicate = model.createProperty(rdf.getNS(), key);
                RDFNode object = model.createLiteral(value.toString());
                model.add(subject, predicate, object);
            }
        }
        return model;
    }
}
