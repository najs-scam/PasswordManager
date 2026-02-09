package LOGIC;

import GUI.DefaultPanel;
import GUI.NavPanel;
import MODEL.PasswordObject;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class SearchBar extends JTextField {
    private final DefaultPanel defaultPanel;
    private final NavPanel navPanel;
    private List<PasswordObject> allEntries;

    public SearchBar(DefaultPanel defaultPanel, NavPanel navPanel) {
        super();
        this.defaultPanel = defaultPanel;
        this.navPanel = navPanel;

        // oblusga dokumentu czyli tam gdzie jest tekst przechowywany
        this.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                search();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                search();
            }
            //to nic nie robi
            @Override
            public void changedUpdate(DocumentEvent e) {
                search();
            }
        });
    }

    public void setAllEntries(List<PasswordObject> allEntries) {
        this.allEntries = allEntries;
    }

    private void search() {
        if (allEntries == null) return;

        String searchQuery = getText();
        String selectedCategory = (String) navPanel.getCategoryComboBox().getSelectedItem();

        if (!searchQuery.isBlank()){
            defaultPanel.refreshTable(selectedCategory, searchQuery);
        } else {
            defaultPanel.refreshTable(selectedCategory);
        }
    }
}
