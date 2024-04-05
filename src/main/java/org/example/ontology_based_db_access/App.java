package org.example.ontology_based_db_access;

import org.example.ontology_based_db_access.ontology.OWLModel;
import org.example.ontology_based_db_access.ui.ComponentBuilder;
import org.example.ontology_based_db_access.ui.Ui;

import javax.swing.*;


public class App extends JFrame {
    public static void main(String[] args) {
        TablesManager tablesManager = new TablesManager();
        ComponentBuilder componentBuilder = new ComponentBuilder();
        OWLModel owlModel = new OWLModel();
        QueryProgram queryProgram = new QueryProgram(owlModel, tablesManager);
        new Ui(tablesManager, componentBuilder, owlModel, queryProgram);
    }
}
