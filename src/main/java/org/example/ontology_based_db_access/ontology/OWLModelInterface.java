package org.example.ontology_based_db_access.ontology;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Resource;

import java.util.List;

public interface OWLModelInterface {
    OntModel createOntModel(String path);
    OntModel getModel();
    String getOntologyFileName();
    String getURI();
    List<Resource> getSameAsNodes(String nodeID);
    List<Resource> getIndividualsAndSubClasses(String nodeID);
    List<String> getAllPossibleSameValues(String nodeId);
}

