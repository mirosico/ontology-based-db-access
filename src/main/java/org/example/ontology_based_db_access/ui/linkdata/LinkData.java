package org.example.ontology_based_db_access.ui.linkdata;

import org.example.ontology_based_db_access.TablesManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class LinkData {

    private static JPanel mainPanel;

    private static JPanel linkedDataList;

    private TablesManager tablesManager;

    private final String[] columnNames = {"Тип таблиці", "Таблиця", "Поле"};
    private final String DELETE_BUTTON = "Видалити";
    private final String ADD_BUTTON = "Додати поле";
    private final String LINK_BUTTON = "Зв'язати дані";




    public LinkData(TablesManager tablesManager) {
        this.tablesManager = tablesManager;
        mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        mainPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        mainPanel.setPreferredSize(new Dimension((int) screenSize.getWidth(), (int) screenSize.getHeight()));
        mainPanel.setMaximumSize(new Dimension((int) screenSize.getWidth(), (int) screenSize.getHeight()));

        JPanel table = createTable();
        mainPanel.add(table);

        linkedDataList = createLinkedDataList();
        mainPanel.add(linkedDataList);
    }

    public JPanel getLinkDataPanel() {
        return mainPanel;
    }

    private String[] getResultsColumns(int max) {
        String[] columnNames = new String[max];
        for (int i = 0; i < max; i++) {
            columnNames[i] = "Поле " + (i + 1);
        }
        return columnNames;
    }


    private JPanel createLinkedDataList() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(500, 500));
        panel.setMaximumSize(new Dimension(500, 500));
        panel.setMinimumSize(new Dimension(500, 500));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setAlignmentY(Component.TOP_ALIGNMENT);

        int max = 0;
        Object[][] resultData = {};
        for (ArrayList<String> strings : tablesManager.getLinkedTablesData()) {
            if (strings.size() > max)
                max = strings.size();
            resultData = Arrays.copyOf(resultData, resultData.length + 1);
            ArrayList<String> row = new ArrayList<>();
            for (int i = 0; i < strings.size(); i++) {
                String string = strings.get(i);
                row.add(string);
            }
            resultData[resultData.length - 1] = row.toArray();
        }

        DefaultTableModel model = new DefaultTableModel(resultData, getResultsColumns(max));
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane);

        JButton remove = new JButton(DELETE_BUTTON);
        if (tablesManager.getLinkedTablesData().size() == 0)
            remove.setEnabled(false);
        panel.add(remove);
        remove.addActionListener(e -> {
            int[] selectedRows = table.getSelectedRows();
            for (int i = selectedRows.length - 1; i >= 0; i--) {
                model.removeRow(selectedRows[i]);
                tablesManager.removeLinkedTablesData(selectedRows[i]);
            }
        });
        return panel;
    }


    private JPanel createTable() {
        JPanel panel = new JPanel();
        Object[][] data = {};
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        JTable table = new JTable(model);

        table.getColumnModel().getColumn(0).setCellEditor(new MyTableCellEditor());
        table.getColumnModel().getColumn(1).setCellEditor(new MyTableCellEditor());
        table.getColumnModel().getColumn(2).setCellEditor(new MyTableCellEditor());

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane);

        JButton addRow = new JButton(ADD_BUTTON);
        addRow.addActionListener(e -> {
            String defaultTableType = tablesManager.getTableTypes()[0];
            model.addRow(new Object[]{
                    defaultTableType,
                    null,
                    null,
            });
        });
        panel.add(addRow);
        panel.setPreferredSize(new Dimension(500, 500));
        panel.setMaximumSize(new Dimension(500, 500));
        panel.setMinimumSize(new Dimension(500, 500));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setAlignmentY(Component.TOP_ALIGNMENT);

        JButton save = new JButton(LINK_BUTTON);
        panel.add(save);
        save.addActionListener(e -> {
            ArrayList<String> res = new ArrayList<>();
            if (table.getRowCount() == 0 || table.getRowCount() > 3)
                return;
            for (int i = 0; i < table.getRowCount(); i++) {
                String[] row = new String[3];
                row[0] = table.getValueAt(i, 0).toString();
                row[1] = table.getValueAt(i, 1).toString();
                row[2] = table.getValueAt(i, 2).toString();
                String joinedRow = String.join(".", row);
                res.add(joinedRow);
            }
            tablesManager.addLinkedTablesData(res);
            for (int i = table.getRowCount() - 1; i >= 0; i--) {
                model.removeRow(i);
            }

            linkedDataList = createLinkedDataList();
            mainPanel.remove(1);
            mainPanel.add(linkedDataList);
            mainPanel.revalidate();
            mainPanel.repaint();
        });
        return panel;
    }
}
