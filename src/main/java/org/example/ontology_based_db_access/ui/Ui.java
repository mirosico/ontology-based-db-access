package org.example.ontology_based_db_access.ui;

import org.example.ontology_based_db_access.QueryProgram;
import org.example.ontology_based_db_access.TablesManager;
import org.example.ontology_based_db_access.databases.DB;
import org.example.ontology_based_db_access.ontology.OWLModel;
import org.example.ontology_based_db_access.ui.connect.ConnectDB;
import org.example.ontology_based_db_access.ui.dbinfo.DBInfo;
import org.example.ontology_based_db_access.ui.linkdata.LinkData;
import org.example.ontology_based_db_access.ui.query.QueryPage;

import javax.swing.*;
import java.awt.*;


public class Ui extends JFrame {

    private final String TITLE = "ONTOLOGY BASED DB ACCESS by yurii.mysko";
    private final String HEADER = "<html><center>ONTOLOGY BASED<br>DB ACCESS</center></html>";
    private final String SUB_HEADER = "<html><center>Виберіть дію з верхнього меню<br>Перед початком роботи підєднайте бази даних</center></html>";
    private final String QUERY_BUTTON_TEXT = "Запит";
    private final String DB_BUTTON_TEXT = "Бази даних";
    private final String LINK_DATA_BUTTON_TEXT = "Зв'язати дані";
    private final String ADD_ONTOLOGY_BUTTON_TEXT = "Додати онтологію";
    private final String ONTOLOGY_ADDED = "Онтологія успішно додана";

    public static JPanel mainContentPanel;
    private TablesManager tablesManager;
    private ComponentBuilder componentBuilder;
    private OWLModel owlModel;
    private QueryProgram queryProgram;


    public static void refresh() {
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    public Ui(TablesManager tablesManager, ComponentBuilder componentBuilder, OWLModel owlModel, QueryProgram queryProgram) {
        this.queryProgram = queryProgram;
        this.componentBuilder = componentBuilder;
        this.tablesManager = tablesManager;
        this.owlModel = owlModel;
        setTitle(TITLE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(false);
        setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        uiBuilder();
        setVisible(true);
    }

    private void queryDataAction() {
        mainContentPanel.removeAll();
        mainContentPanel.add(new QueryPage(tablesManager, queryProgram, owlModel).getMainPanel());
        refresh();
        pack();
    }

    private void connectDbAction(String dbType) {
        mainContentPanel.removeAll();
        DB db = tablesManager.getDB(dbType);
        if (db.isConnected()) {
            mainContentPanel.add(new DBInfo(tablesManager, componentBuilder, dbType).createInfoPanel());
        } else {
            mainContentPanel.add(new ConnectDB(tablesManager, componentBuilder, dbType).createAddDbPanel());
        }
        refresh();
        pack();
    }

    private void linkDataAction() {
        mainContentPanel.removeAll();
        mainContentPanel.add(new LinkData(tablesManager).getLinkDataPanel());
        pack();
    }

    private void addOntologyAction() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            owlModel.createOntModel(filePath);
            JOptionPane.showMessageDialog(this, ONTOLOGY_ADDED, null, JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void uiBuilder() {
        JMenuBar topBar = createTopBar();
        getContentPane().add(topBar, BorderLayout.NORTH);
        mainContentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        JPanel welcomePanel = componentBuilder.createPanel();
        JLabel mainHeader = componentBuilder.createHeader(HEADER);
        welcomePanel.add(Box.createVerticalStrut(50));
        welcomePanel.add(mainHeader);
        JLabel subHeader = componentBuilder.createSubHeader(SUB_HEADER);
        welcomePanel.add(Box.createVerticalStrut(20));
        welcomePanel.add(subHeader);
        mainContentPanel.add(welcomePanel);
        getContentPane().add(mainContentPanel, BorderLayout.CENTER);
        pack();
    }

    private JMenuBar createTopBar() {
        JMenuBar panel = new JMenuBar();
        JButton queryDataButton = new JButton(QUERY_BUTTON_TEXT);
        queryDataButton.addActionListener(e -> queryDataAction());
        panel.add(queryDataButton);

        JPopupMenu popupMenu = componentBuilder.createDropdownPopup(tablesManager.getTableTypes());
        for (Component component : popupMenu.getComponents()) {
            JMenuItem menuItem = (JMenuItem) component;
            String dbType = menuItem.getText().toLowerCase();
            menuItem.addActionListener(e -> connectDbAction(dbType));
        }

        JButton addDbButton = componentBuilder.createDropDownButton(DB_BUTTON_TEXT);
        addDbButton.setComponentPopupMenu(popupMenu);
        panel.add(addDbButton);

        JButton linkDataButton = new JButton(LINK_DATA_BUTTON_TEXT);
        linkDataButton.addActionListener(e -> linkDataAction());
        panel.add(linkDataButton);

        JButton addOntologyButton = new JButton(ADD_ONTOLOGY_BUTTON_TEXT);
        addOntologyButton.addActionListener(e -> addOntologyAction());
        panel.add(addOntologyButton);
        return panel;
    }

    public static void main(String[] args) {
        TablesManager tablesManager = new TablesManager();
        ComponentBuilder componentBuilder = new ComponentBuilder();
        OWLModel owlModel = new OWLModel();
        QueryProgram queryProgram = new QueryProgram(owlModel, tablesManager);
        new Ui(tablesManager, componentBuilder, owlModel, queryProgram);
    }
}
