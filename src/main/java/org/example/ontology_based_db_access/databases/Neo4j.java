package org.example.ontology_based_db_access.databases;

import org.apache.jena.rdf.model.Model;
import org.example.ontology_based_db_access.converters.GDBToRdfConverter;
import org.neo4j.driver.Record;
import org.neo4j.driver.*;
import org.neo4j.driver.types.Node;

import java.util.ArrayList;
import java.util.Map;

public class Neo4j implements DB {
    private static Driver driver;
    private static Session session;
    private static String username;

    public void connect(String uri, String user, String password) {
        username = user;
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
        session = driver.session();
    }

    public boolean isConnected() {
        return session != null && session.isOpen();
    }

    public String[] getCollections() {
        if (!isConnected())
            return null;
        ArrayList<String> nodes = new ArrayList<>();
        Result result = session.run("CALL db.labels()");

        while (result.hasNext()) {
            Record record = result.next();
            String label = record.get("label").asString();
            nodes.add(label);
        }
        return nodes.toArray(new String[0]);
    }

    public String[] getCollectionProps(String nodeName) {
        if (!isConnected())
            return null;
        try {
            ArrayList<String> properties = new ArrayList<>();
            String query = "MATCH (n:" + nodeName + ") RETURN n";
            Result result = session.run(query);

            if (result.hasNext()) {
                Record record = result.next();
                Node node = record.get("n").asNode();
                for (Map.Entry<String, Object> entry : node.asMap().entrySet()) {
                    properties.add(entry.getKey());
                }
            }
            return properties.toArray(new String[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getCatalog() {
        return "";
    }

    public String getUserName() {
       return username;
    }

    public void close() {
        driver.close();
        driver = null;
        session = null;
    }

    public Model convertToRDF(String nodeName) {
        GDBToRdfConverter converter = new GDBToRdfConverter();
        String query = "MATCH (n:" + nodeName + ") RETURN n";
        Result result = session.run(query);
        return converter.convert(result, nodeName);
    }

}