package org.example.ontology_based_db_access.ui.query;

import org.example.ontology_based_db_access.QueryProgram;
import org.example.ontology_based_db_access.TablesManager;
import org.example.ontology_based_db_access.ontology.OWLModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class QueryPage implements ActionListener {

    private JPanel mainPanel;
    private JPanel topPanel;
    private JTextArea queryTextArea;
    private JButton submitButton;
    private QueryMode queryMode;
    private QueryProgram queryProgram;

    private final String TITLE = "Введіть запит:";
    private final String ERROR = "Error: ";
    private final String RESULT_TITLE = "Результати запиту:";
    private final String ERROR_EMPTY = "Error: запит не введено";
    private final String QUERY_BUTTON = "Надіслати запит";

    public QueryPage(TablesManager tablesManager, QueryProgram queryProgram, OWLModel owlModel) {
        this.queryProgram = queryProgram;
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setLayout(new BorderLayout(20, 15));
        topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JPanel queryPanel = new JPanel(new BorderLayout());
        JLabel queryLabel = new JLabel(TITLE);
        queryLabel.setFont(new Font("Arial", Font.BOLD, 18));
        queryTextArea = new JTextArea();
        queryTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        JScrollPane queryScrollPane = new JScrollPane(queryTextArea);
        queryScrollPane.setPreferredSize(new Dimension(800, 200));
        queryPanel.add(queryLabel, BorderLayout.NORTH);
        queryPanel.add(queryScrollPane, BorderLayout.CENTER);
        topPanel.add(queryPanel, BorderLayout.NORTH);

        JPanel submitPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        submitButton = new JButton(QUERY_BUTTON);
        submitButton.addActionListener(this);
        submitPanel.add(submitButton);
        topPanel.add(submitPanel, BorderLayout.CENTER);

        mainPanel.add(topPanel, BorderLayout.CENTER);

        JPanel tablesData = new TablesData(tablesManager, owlModel).getPanel();
        JPanel tablesDataPanel = new JPanel(new BorderLayout());
        tablesDataPanel.add(tablesData, BorderLayout.NORTH);
        JScrollPane tablesDataScrollPane = new JScrollPane(tablesDataPanel);
        tablesDataScrollPane.setPreferredSize(new Dimension(250, 700));
        mainPanel.add(tablesDataScrollPane, BorderLayout.WEST);

        queryMode = new QueryMode();
        mainPanel.add(queryMode.getPanel(), BorderLayout.EAST);
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            try {
                createResultTable();
            } catch (Exception ex) {
                JLabel errorLabel = new JLabel(ERROR + ex.getMessage());
                errorLabel.setForeground(Color.RED);
                errorLabel.setMaximumSize(new Dimension(500, 200));
                if (errorLabel.getText().length() > 80) {
                    errorLabel.setText(errorLabel.getText().substring(0, 50) + "...");
                }
                showResults(errorLabel);
            }
        }
    }

    private void showResults(Component results) {
        if (topPanel.getComponentCount() > 2) {
            topPanel.remove(2);
        }
        topPanel.add(results, BorderLayout.SOUTH);
        topPanel.revalidate();
        topPanel.repaint();
    }

    private void createResultTable() {
        if (queryTextArea.getText().isEmpty()) {
            JLabel errorLabel = new JLabel(ERROR_EMPTY);
            errorLabel.setForeground(Color.RED);
            showResults(errorLabel);
            return;
        }

        ResultsTable resultsTable = new ResultsTable(queryProgram.executeQuery(queryTextArea.getText(), queryMode.getMode()));
        ExportButton exportButtons = new ExportButton(queryProgram.executeQuery(queryTextArea.getText(), queryMode.getMode()));

        JPanel resultsPanel = new JPanel(new BorderLayout());
        JLabel resultsLabel = new JLabel(RESULT_TITLE);
        resultsLabel.setFont(new Font("Arial", Font.BOLD, 18));
        resultsPanel.add(resultsLabel, BorderLayout.NORTH);
        resultsPanel.add(resultsTable.getScrollPane(), BorderLayout.CENTER);

        JPanel exportPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        exportPanel.add(exportButtons.getExportToCSVButton());

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(resultsPanel, BorderLayout.CENTER);
        bottomPanel.add(exportPanel, BorderLayout.SOUTH);

        showResults(bottomPanel);
    }
}
