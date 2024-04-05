package org.example.ontology_based_db_access.ui.query;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class QueryMode {

    private JPanel panel;
    private String mode;

    public String getMode() {
        return mode;
    }

    private final String TITLE = "Виберіть типу запиту";

    private final String[] MODES = new String[] {
        "INTERSECT",
        "UNION"
    };

    public QueryMode() {
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(240, 240, 240));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel typeLabel = new JLabel(TITLE);
        typeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        typeLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        panel.add(typeLabel);

        ButtonGroup group = new ButtonGroup();
        JRadioButton radioButton1 = new JRadioButton(MODES[0]);
        JRadioButton radioButton2 = new JRadioButton(MODES[1]);
        group.add(radioButton1);
        group.add(radioButton2);
        group.setSelected(radioButton1.getModel(), true);
        mode = radioButton1.getText();

        radioButton1.addActionListener(e -> {
            mode = MODES[0];
        });
        radioButton2.addActionListener(e -> {
            mode = MODES[1];
        });

        panel.add(radioButton1);
        panel.add(radioButton2);
    }

    public JPanel getPanel() {
        return panel;
    }
}
