package org.example.ontology_based_db_access.ui.dbinfo;

import org.example.ontology_based_db_access.databases.DB;
import org.example.ontology_based_db_access.TablesManager;
import org.example.ontology_based_db_access.ui.ComponentBuilder;
import org.example.ontology_based_db_access.ui.linkdata.checkboxes.CheckListItem;
import org.example.ontology_based_db_access.ui.linkdata.checkboxes.CheckListRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;

public class DBInfo {

    private static ArrayList<String> tables = new ArrayList<>();

    private static JTextField filterInput;

    private static JPanel rdfInfoPanel;

    private DB db;
    private TablesManager tablesManager;
    private ComponentBuilder componentBuilder;
    private String dbType;

    private final String STATUS_CONNECTED = "Статус: З'єднано";
    private final String STATUS_NOT_CONNECTED = "Статус: Не з'єднано";
    private final String CATALOG = "Каталог: ";
    private final String USER_NAME = "Ім'я користувача: ";
    private final String SELECT_TABLES = "Виберіть таблиці для перетворення";

    public DBInfo(TablesManager tablesManager, ComponentBuilder componentBuilder, String dbType) {
        this.tablesManager = tablesManager;
        this.componentBuilder = componentBuilder;
        this.dbType = dbType;
        db = tablesManager.getDB(dbType);
        initTables(null);
    }

    private void initTables(String filter) {
        tables.clear();
        for (String tablename : db.getCollections()) {
            if (filter == null || tablename.contains(filter)) {
                tables.add(tablename);
            }
        }
        tables.sort(Comparator.naturalOrder());
    }

    private void filterAction() {
        initTables(filterInput.getText());
        rdfInfoPanel.remove(rdfInfoPanel.getComponentCount() - 1);
        rdfInfoPanel.add(getCheckBoxListScrollPane());
        rdfInfoPanel.revalidate();
        rdfInfoPanel.repaint();
    }

    private CheckListItem[] getCheckListItems() {
        CheckListItem[] listDataArray = new CheckListItem[tables.size()];
        for (int i = 0; i < tables.size(); i++) {
            listDataArray[i] = new CheckListItem(tables.get(i));
            Set<String> connectedTables = tablesManager.getConnectedTables(dbType);
            if (connectedTables != null && connectedTables.contains(tables.get(i)))
                listDataArray[i].setSelected(true);
        }
        return listDataArray;
    }

    private JList getCheckBoxList() {
        JList checkBoxList = new JList(getCheckListItems());
        checkBoxList.setCellRenderer(new CheckListRenderer());
        checkBoxList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        checkBoxList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                JList list = (JList) event.getSource();
                int index = list.locationToIndex(event.getPoint());
                CheckListItem item = (CheckListItem) list.getModel()
                        .getElementAt(index);
                item.setSelected(!item.isSelected());
                list.repaint(list.getCellBounds(index, index));
                if (item.isSelected())
                    tablesManager.addConnectedTable(dbType, item.toString());
                else
                    tablesManager.removeConnectedTable(dbType, item.toString());
            }
        });
        return checkBoxList;
    }

    private JScrollPane getCheckBoxListScrollPane() {
        JList checkBoxList = getCheckBoxList();
        JScrollPane scrollPane = new JScrollPane(checkBoxList);
        scrollPane.setPreferredSize(new Dimension(100, 200));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        return scrollPane;
    }


    public JPanel createInfoPanel() {
        rdfInfoPanel = componentBuilder.createPanel();
        rdfInfoPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel rdfInfoLabel = componentBuilder.createLabel(dbType + " база даних");
        rdfInfoPanel.add(rdfInfoLabel);
        rdfInfoPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        String catalog = db.getCatalog();
        JLabel catalogLabel = componentBuilder.createLabel(CATALOG + catalog);
        catalogLabel.setFont(new Font("Serif", Font.BOLD, 16));
        if (catalog.length() > 0)
            rdfInfoPanel.add(catalogLabel);
        JLabel isConnected = componentBuilder.createLabel(db.isConnected() ? STATUS_CONNECTED : STATUS_NOT_CONNECTED);
        isConnected.setFont(new Font("Serif", Font.BOLD, 16));
        rdfInfoPanel.add(isConnected);

        String username = db.getUserName();
        JLabel usernameLabel = componentBuilder.createLabel(USER_NAME + username);
        usernameLabel.setFont(new Font("Serif", Font.BOLD, 16));

        rdfInfoPanel.add(usernameLabel);

        rdfInfoPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        rdfInfoPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel tablesLabel = componentBuilder.createLabel(SELECT_TABLES);
        tablesLabel.setFont(new Font("Serif", Font.BOLD, 16));
        rdfInfoPanel.add(tablesLabel);

        rdfInfoPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        filterInput = componentBuilder.createTextField("");
        filterInput.addActionListener(e -> filterAction());
        rdfInfoPanel.add(filterInput);

        JScrollPane scrollPane = getCheckBoxListScrollPane();
        rdfInfoPanel.add(scrollPane);
        return rdfInfoPanel;
    }


}
