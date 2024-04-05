package org.example.ontology_based_db_access.ui;

import javax.swing.*;
import java.awt.*;

public class ComponentBuilder {

    public JTextField createTextField(String text) {
        JTextField textField = new JTextField();
        textField.setPreferredSize(new Dimension(300, 30));
        textField.setMaximumSize(new Dimension(300, 30));
        textField.setMinimumSize(new Dimension(300, 30));
        textField.setAlignmentX(Component.CENTER_ALIGNMENT);
        textField.setAlignmentY(Component.CENTER_ALIGNMENT);
        textField.setVisible(true);
        textField.setText(text);
        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textField.selectAll();
            }
        });
        return textField;
    }


    public JTextField createPasswordField(String text) {
        JPasswordField textField = new JPasswordField();
        textField.setEchoChar('*');
        textField.setPreferredSize(new Dimension(300, 30));
        textField.setMaximumSize(new Dimension(300, 30));
        textField.setMinimumSize(new Dimension(300, 30));
        textField.setAlignmentX(Component.CENTER_ALIGNMENT);
        textField.setAlignmentY(Component.CENTER_ALIGNMENT);
        textField.setVisible(true);
        textField.setText(text);
        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textField.selectAll();
            }
        });
        return textField;
    }

    public JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(200, 30));
        button.setMaximumSize(new Dimension(200, 30));
        button.setMinimumSize(new Dimension(200, 30));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setAlignmentY(Component.CENTER_ALIGNMENT);
        button.setVisible(true);
        return button;
    }

    public JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setAlignmentY(Component.CENTER_ALIGNMENT);
        label.setVisible(true);
        return label;
    }

    public JCheckBox createCheckBox(String text) {
        JCheckBox checkBox = new JCheckBox(text);
        checkBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        checkBox.setAlignmentY(Component.CENTER_ALIGNMENT);
        checkBox.setVisible(true);
        return checkBox;
    }


    public JPanel createPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(400, 400));
        panel.setMaximumSize(new Dimension(400, 400));
        panel.setMinimumSize(new Dimension(400, 400));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setAlignmentY(Component.CENTER_ALIGNMENT);
        panel.setVisible(true);
        return panel;
    }

    public JPopupMenu createDropdownPopup(String[] options) {
        JPopupMenu popupMenu = new JPopupMenu();
        for (String option : options) {
            JMenuItem menuItem = new JMenuItem(option);
            popupMenu.add(menuItem);
        }
        return popupMenu;
    }

    public JButton createDropDownButton(String label) {
        return new JButton(label);
    }


    public JLabel createHeader(String text) {
        JLabel mainHeader = new JLabel(text);
        mainHeader.setFont(new Font("Arial", Font.BOLD, 40));
        mainHeader.setForeground(new Color(30, 144, 255));
        mainHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
        return mainHeader;
    }

    public JLabel createSubHeader(String text) {
        JLabel subHeader = new JLabel(text);
        subHeader.setFont(new Font("Arial", Font.PLAIN, 20));
        subHeader.setForeground(new Color(119, 136, 153));
        subHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
        return subHeader;
    }


}
