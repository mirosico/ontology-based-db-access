package org.example.ontology_based_db_access.databases;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.apache.jena.rdf.model.Model;
import org.bson.Document;
import org.example.ontology_based_db_access.converters.ODBToRdfConverter;

import java.util.ArrayList;
import java.util.Set;


public class MongoDB implements DB {
    private static MongoClient mongoClient;
    private static MongoDatabase database;

    public void connect(String connectionString ,String DB_NAME, String DB_PASS) {
        MongoClientURI uri = new MongoClientURI(connectionString);
        mongoClient = new MongoClient(uri);
        database = mongoClient.getDatabase(DB_NAME);
    }

    public void close() {
        mongoClient.close();
        mongoClient = null;
        database = null;
    }

    public boolean isConnected() {
        return mongoClient != null && database != null;
    }

    public String[] getCollections() {
        if (!isConnected())
            return null;
        ArrayList<String> collections = database.listCollectionNames().into(new ArrayList());
        return collections.toArray(new String[0]);
    }

    public String[] getCollectionProps(String collectionName) {
        if (!isConnected())
            return null;
        FindIterable<Document> iterable = database.getCollection(collectionName).find();
        MongoCursor<Document> cursor = iterable.iterator();
        if (cursor.hasNext()) {
            Document doc = cursor.next();
            Set<String> keys = doc.keySet();
            return keys.toArray(new String[0]);
        }
        return null;
    }

    public String getCatalog() {
        return database.getName();
    }


    public String getUserName() {
        if (!isConnected())
            return null;
        return mongoClient.getCredential().getUserName();
    }

    public Model convertToRDF(String collectionName) {
        if (!isConnected())
            return null;
        ODBToRdfConverter converter = new ODBToRdfConverter();
        MongoCollection<Document> collection = database.getCollection(collectionName);
        return converter.convert(collection.find(), collectionName);
    }
}