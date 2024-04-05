package org.example.ontology_based_db_access.ui.linkdata;

import org.example.ontology_based_db_access.TablesManager;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.util.Set;


public class MyTableCellEditor extends AbstractCellEditor implements TableCellEditor {
    private JComboBox<String> comboBox;

    public MyTableCellEditor() {
        comboBox = new JComboBox<>();
    }

    @Override
    public Object getCellEditorValue() {
        return comboBox.getSelectedItem();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        TablesManager tablesManager = new TablesManager();
        String tableType = table.getModel().getValueAt(row, 0).toString();
        Set<String> tables = tablesManager.getConnectedTables(tableType);

        if (column == 0) {
            comboBox.removeAllItems();
            comboBox.addItem(tablesManager.getTableTypes()[0]);
            comboBox.addItem(tablesManager.getTableTypes()[1]);
            comboBox.addItem(tablesManager.getTableTypes()[2]);
        } else if (column == 1) {
            comboBox.removeAllItems();
            if (tables != null) {
                for (String table1 : tables) {
                    comboBox.addItem(table1);
                }
            }
        } else if (column == 2) {
            comboBox.removeAllItems();
            String[] fields;
            try {
                fields = tablesManager.getColumns(tableType, table.getModel().getValueAt(row, 1).toString());
            } catch (Exception e) {
                fields = new String[0];
            }
            for (String field : fields) {
                comboBox.addItem(field);
            }
        }
        return comboBox;
    }
}
