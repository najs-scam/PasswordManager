package GUI;

import LOGIC.FileManager;
import LOGIC.SearchBar;
import MODEL.PasswordObject;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TopPanel extends JPanel {
    private final File passwordFile;
    private final MainFrame mainFrame;
    private DefaultPanel defaultPanel;
    private NavPanel navPanel;
    private SearchBar searchBar;

    public TopPanel(File passwordFile, MainFrame mainFrame, NavPanel navPanel, DefaultPanel defaultPanel) {
        this.passwordFile = passwordFile;
        this.mainFrame = mainFrame;

        this.setLayout(new BorderLayout(5, 5));

        SearchBar searchBar = new SearchBar(defaultPanel, navPanel);
        FileManager fileManager = new FileManager(passwordFile, defaultPanel.isCorrectPassword());
        searchBar.setAllEntries(fileManager.loadPasswords((String) navPanel.getCategoryComboBox().getSelectedItem()));

        JLabel title = new JLabel();
        title.setText("Password Manager");
        title.setFont(new Font("Monospaced", Font.ITALIC, 20));
        title.setForeground(Color.MAGENTA);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(
                    mainFrame,
                    "Are you sure you want to go back?",
                    "Are you sure?",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (result == JOptionPane.YES_OPTION) {
                mainFrame.lunch();
            }
        });

        this.add(title, BorderLayout.WEST);
        this.add(searchBar, BorderLayout.CENTER);
        this.add(backButton, BorderLayout.EAST);
    }

    public void setDefaultPanel(DefaultPanel defaultPanel) {
        this.defaultPanel = defaultPanel;
    }
    public void setNavPanel(NavPanel navPanel) {
        this.navPanel = navPanel;
    }
}
