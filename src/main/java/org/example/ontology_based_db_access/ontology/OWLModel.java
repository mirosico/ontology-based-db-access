package org.example.ontology_based_db_access.ontology;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.rdf.model.*;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.vocabulary.OWL;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OWLModel implements OWLModelInterface {

    private OntModel model;
    private String NS;
    private String ontologyFileName;


    public OntModel createOntModel(String path) {
        try {
            model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
            model.read(path);
            NS = getURI();
            ontologyFileName = getFileNameFromPath(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return model;
    }

    public String getOntologyFileName() {
        return ontologyFileName;
    }

    private String getFileNameFromPath(String path) {
        String[] parts = path.split("/");
        return parts[parts.length - 1];
    }

    public OntModel getModel() {
        return model;
    }

    public String getURI() {
        return model.getNsPrefixURI("");
    }


    public List<Resource> getSameAsNodes(String nodeID) {
        List<Resource> sameAsNodes = new ArrayList<>();
        Property sameAs = model.createProperty(OWL.sameAs.getURI());
        Property equivalentClass = model.createProperty(OWL.equivalentClass.getURI());
        Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
        InfModel infModel = ModelFactory.createInfModel(reasoner, model);
        Resource node = infModel.getResource(NS + nodeID);
        StmtIterator stmtIterator = infModel.listStatements(node, sameAs, (RDFNode) null);
        while (stmtIterator.hasNext()) {
            Statement stmt = stmtIterator.next();
            RDFNode obj = stmt.getObject();
            if (obj.isResource() && !obj.asResource().toString().equals(node.toString())) {
                sameAsNodes.add(obj.asResource());
            }
        }
        stmtIterator = infModel.listStatements(node, equivalentClass, (RDFNode) null);
        while (stmtIterator.hasNext()) {
            Statement stmt = stmtIterator.next();
            RDFNode obj = stmt.getObject();
            if (obj.isResource() && !obj.asResource().toString().equals(node.toString())) {
                sameAsNodes.add(obj.asResource());
            }
        }
        return sameAsNodes;
    }


    public List<Resource> getIndividualsAndSubClasses(String nodeID) {
        List<Resource> individualsAndSubClasses = new ArrayList<>();

        Resource node = model.getResource(NS + nodeID);

        OntClass nodeClass;
        if (node.canAs(OntClass.class)) {
            nodeClass = node.as(OntClass.class);
        } else {
            return null;
        }

        Iterator<? extends OntResource> individualsIterator = nodeClass.listInstances();

        while (individualsIterator.hasNext()) {
            OntResource individual = individualsIterator.next();
            individualsAndSubClasses.add(individual);
        }

        Iterator<? extends OntClass> subclassesIterator = nodeClass.listSubClasses();

        while (subclassesIterator.hasNext()) {
            OntClass subclass = subclassesIterator.next();
            individualsAndSubClasses.add(subclass);
        }

        return individualsAndSubClasses;
    }

    private String uriToId(Resource resource) {
        return resource.toString().replace(NS, "");
    }

    public List<String> getAllPossibleSameValues(String nodeId) {
        List<String> possibleValues = new ArrayList<>();
        List<String> topLevelNodes = new ArrayList<>();
        topLevelNodes.add(nodeId);
        List<Resource> sameAsNodes = getSameAsNodes(nodeId);
        for (Resource sameAsNode : sameAsNodes) {
            topLevelNodes.add(uriToId(sameAsNode));
        }
        for (String topLevelNode : topLevelNodes) {
            possibleValues.add(topLevelNode);
            List<Resource> individualsAndSubClasses = getIndividualsAndSubClasses(topLevelNode);
            if (individualsAndSubClasses == null) {
                continue;
            }
            for (Resource individualOrSubClass : individualsAndSubClasses) {
                possibleValues.add(uriToId(individualOrSubClass));
            }
        }
        return possibleValues;
    }


}
