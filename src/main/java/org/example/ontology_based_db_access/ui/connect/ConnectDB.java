package org.example.ontology_based_db_access.ui.connect;

import org.example.ontology_based_db_access.databases.DB;
import org.example.ontology_based_db_access.TablesManager;
import org.example.ontology_based_db_access.ui.ComponentBuilder;
import org.example.ontology_based_db_access.ui.Ui;
import org.example.ontology_based_db_access.ui.dbinfo.DBInfo;

import javax.swing.*;
import java.awt.*;
import java.util.Map;


public class ConnectDB {

    private JTextField dbUrl;
    private JTextField dbUsername;
    private JTextField dbPassword;
    private final Map<String, String[]> connectConstants = Map.of(
            "rdb", new String[]{"jdbc:mysql:..."},
            "odb", new String[]{"mongodb+srv:", "Cluster0", ""},
            "gdb", new String[]{"neo4j+s:..."}
    );
    private final String CONNECTED = "З'єднано з базою даних";
    private final String NOT_CONNECTED = "Не вдалося з'єднатися з базою даних";
    private final String CONNECT_BUTTON = "З'єднатися";

    private TablesManager tablesManager;
    private ComponentBuilder componentBuilder;
    private String dbType;

    public ConnectDB(TablesManager tablesManager, ComponentBuilder componentBuilder, String dbType) {
        this.tablesManager = tablesManager;
        this.componentBuilder = componentBuilder;
        this.dbType = dbType;
    }

    private void addDbAction() {
        DB db = tablesManager.getDB(dbType);
        db.connect(dbUrl.getText(), dbUsername.getText(), dbPassword.getText());
        if (db.isConnected()) {
            JOptionPane.showMessageDialog(null, CONNECTED);
            Ui.mainContentPanel.removeAll();
            Ui.mainContentPanel.add(new DBInfo(tablesManager, componentBuilder, dbType).createInfoPanel());
            Ui.refresh();
        } else {
            JOptionPane.showMessageDialog(null, NOT_CONNECTED);
        }
    }

    public JPanel createAddDbPanel() {
        JPanel addDbPanel = componentBuilder.createPanel();
        JLabel addDbLabel = componentBuilder.createLabel("Підключіть " + dbType + " базу даних");
        dbUrl = componentBuilder.createTextField(connectConstants.get(dbType)[0]);
        dbUsername = componentBuilder.createTextField(connectConstants.get(dbType)[1]);
        dbPassword = componentBuilder.createPasswordField(connectConstants.get(dbType)[2]);

        JButton addDbButton = componentBuilder.createButton(CONNECT_BUTTON);
        addDbPanel.add(addDbLabel);
        addDbPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        addDbPanel.add(dbUrl);
        addDbPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        addDbPanel.add(dbUsername);
        addDbPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        addDbPanel.add(dbPassword);
        addDbPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        addDbPanel.add(addDbButton);
        addDbButton.addActionListener(e -> addDbAction());

        return addDbPanel;
    }

}
