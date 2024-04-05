package org.example.ontology_based_db_access.ui.query;

import org.example.ontology_based_db_access.TablesManager;
import org.example.ontology_based_db_access.ontology.OWLModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Set;

public class TablesData {

    private JPanel panel;
    private TablesManager tablesManager;

    public TablesData(TablesManager tablesManager, OWLModel owlModel) {
        this.tablesManager = tablesManager;
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(240, 240, 240));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (String tableType : tablesManager.getTableTypes()) {
            Set<String> connectedTables = tablesManager.getConnectedTables(tableType);
            if (connectedTables == null || connectedTables.isEmpty()) {
                continue;
            }

            JLabel typeLabel = new JLabel(tableType);
            typeLabel.setFont(new Font("Arial", Font.BOLD, 18));
            typeLabel.setBorder(new EmptyBorder(10, 0, 5, 0));
            panel.add(typeLabel);

            JPanel tablesPanel = new JPanel(new GridLayout(0, 1, 0, 5));
            tablesPanel.setBorder(new EmptyBorder(5, 0, 10, 0));
            for (String tableName : connectedTables) {
                JLabel tableLabel = new JLabel(tableName + ":");
                tableLabel.setFont(new Font("Arial", Font.PLAIN, 16));
                tableLabel.setBorder(new EmptyBorder(0, 10, 5, 0));
                tableLabel.setText(tableLabel.getText().substring(0, 1).toUpperCase() + tableLabel.getText().substring(1));

                JList<String> columnsList = getTableColumnsList(tableType, tableName);

                JPanel tablePanel = new JPanel(new BorderLayout());
                tablePanel.add(tableLabel, BorderLayout.NORTH);
                tablePanel.add(columnsList, BorderLayout.CENTER);

                tablesPanel.add(tablePanel);
            }
            panel.add(tablesPanel);
            JSeparator separator = new JSeparator();
            separator.setForeground(Color.BLACK);
            panel.add(separator);
        }

        if (owlModel.getOntologyFileName() == null) {
            return;
        }

        JLabel ontologyLabel = new JLabel("Ontology");
        ontologyLabel.setFont(new Font("Arial", Font.BOLD, 18));
        ontologyLabel.setBorder(new EmptyBorder(10, 0, 5, 0));
        JLabel ontologyName = new JLabel(owlModel.getOntologyFileName());
        ontologyName.setFont(new Font("Arial", Font.PLAIN, 14));
        ontologyName.setBorder(new EmptyBorder(0, 10, 5, 0));
        panel.add(ontologyLabel);
        panel.add(ontologyName);

        JSeparator separator = new JSeparator();
        separator.setForeground(Color.BLACK);
        panel.add(separator);
    }

    public JPanel getPanel() {
        return panel;
    }

    private JList<String> getTableColumnsList(String tableType, String tableName) {
        JList<String> list = new JList<>();
        DefaultListModel<String> model = new DefaultListModel<>();
        String[] columns = tablesManager.getColumns(tableType, tableName);
        for (String column : columns) {
            model.addElement("-" + column);
        }
        list.setModel(model);
        list.setBackground(new Color(50, 50, 50));
        list.setForeground(Color.WHITE);
        list.setFont(new Font("Arial", Font.PLAIN, 14));
        list.setFocusable(false);
        return list;
    }

}
