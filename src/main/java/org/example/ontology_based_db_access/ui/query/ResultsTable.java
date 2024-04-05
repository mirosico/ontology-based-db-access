package org.example.ontology_based_db_access.ui.query;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.rdf.model.RDFNode;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class ResultsTable {


    private JTable table;

    public ResultsTable(ResultSet rs) {
        ResultSet results = ResultSetFactory.copyResults(rs);
        List<String> columns = results.getResultVars();
        List<List<String>> rows = new ArrayList<>();

        while (results.hasNext()) {
            QuerySolution solution = results.next();
            List<String> row = new ArrayList();

            for (String column : columns) {
                RDFNode node = solution.get(column);
                String value = (node != null) ? node.toString() : "";
                row.add(value);
            }

            rows.add(row);
        }
        Vector<String> columnNames = new Vector<>(columns);
        TableModel model = new DefaultTableModel(columnNames, rows.size());
        for (int i = 0; i < rows.size(); i++) {
            for (int j = 0; j < columns.size(); j++) {
                String value = rows.get(i).get(j);
                model.setValueAt(value, i, j);
            }
        }
        table = new JTable(model);
    }

    public JScrollPane getScrollPane() {
        return new JScrollPane(table);
    }
}
