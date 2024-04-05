package org.example.ontology_based_db_access.ui.query;

import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFactory;
import org.example.ontology_based_db_access.converters.ResultSetToCSVConverter;
import org.example.ontology_based_db_access.ui.ComponentBuilder;

import javax.swing.*;

public class ExportButton {
    private JButton exportToCSVButton;

    private final String EXPORT_BUTTON = "Export to CSV";
    private final String FILE_NAME = "results";


    public ExportButton(ResultSet rs) {
        ResultSet results = ResultSetFactory.copyResults(rs);
        ComponentBuilder builder = new ComponentBuilder();
        exportToCSVButton = builder.createButton(EXPORT_BUTTON);
        exportToCSVButton.addActionListener(e -> {
            ResultSetToCSVConverter exporter = new ResultSetToCSVConverter(ResultSetFactory.copyResults(results));
            exporter.export(FILE_NAME);
        });
    }

    public JButton getExportToCSVButton() {
        return exportToCSVButton;
    }

}
