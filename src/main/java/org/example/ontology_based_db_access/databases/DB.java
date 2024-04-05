package org.example.ontology_based_db_access.databases;

import org.apache.jena.rdf.model.Model;

public interface DB {
    void connect(String DB_URL, String DB_USER, String DB_PASS);
    void close();
    boolean isConnected();
    String[] getCollections();
    String[] getCollectionProps(String collectionName);
    String getCatalog();
    String getUserName();
    Model convertToRDF(String data);
}
