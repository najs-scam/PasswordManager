package GUI;

import LOGIC.CategoryManager;
import LOGIC.CryptogramInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class NavPanel extends JPanel implements CategoryManager {
    private final JComboBox categoryComboBox;
    private final JButton addCategoryButton;
    private final JButton deleteCategoryButton;
    private final Set<String> categories;
    private final File passwordFile;
    private final MainFrame mainFrame;
    private final DefaultPanel defaultPanel;
    private TopPanel topPanel;

    public NavPanel(File passwordFile, MainFrame mainFrame, DefaultPanel defaultPanel) {
        this.passwordFile = passwordFile;
        this.mainFrame = mainFrame;
        this.defaultPanel = defaultPanel;

        this.setLayout(new BorderLayout(10,10));
        JPanel top = new JPanel();
        top.add(new JLabel("Categories"));
        categories = getCategoriesFromFile();

        List<String> sortedCategories = categories.stream()
                .filter(c -> !c.equals("All") && !c.equals("Others"))
                .sorted()
                .collect(Collectors.toCollection(ArrayList::new));

        if (categories.contains("All"))  sortedCategories.add(0, "All");
        if (categories.contains("Others")) sortedCategories.add("Others");

        categoryComboBox = new JComboBox<>(
                new DefaultComboBoxModel<>(sortedCategories.toArray(new String[0]))
        );

        top.add(categoryComboBox);
        this.add(top, BorderLayout.NORTH);

        JPanel bottom = new JPanel();
        bottom.setLayout(new BorderLayout());
        addCategoryButton = new JButton("Add Category");
        deleteCategoryButton = new JButton("Delete Category");
        bottom.add(addCategoryButton, BorderLayout.NORTH);
        bottom.add(deleteCategoryButton, BorderLayout.SOUTH);
        this.add(bottom, BorderLayout.SOUTH);
        categoryComboBox.setSelectedItem("All");
        defaultPanel.refreshTable("All");

        addCategoryButton.addActionListener(e -> {
            String newCategory = JOptionPane.showInputDialog("Please enter a new category");
            addNewCategory(newCategory, passwordFile, categoryComboBox, mainFrame);
        });

        deleteCategoryButton.addActionListener(e -> {
            String categoryToRemove = (String) categoryComboBox.getSelectedItem();
            deleteCategory(categoryToRemove, passwordFile, categoryComboBox, mainFrame);
            defaultPanel.refreshTable((String) categoryComboBox.getSelectedItem());
        });

        categoryComboBox.addActionListener(e -> {
            String selected = (String) categoryComboBox.getSelectedItem();
            defaultPanel.refreshTable(selected);
        });
    }

    public Set<String> getCategoriesFromFile() {
        Set<String> categories = new TreeSet<>();

        try {
            java.util.List<String> lines = java.nio.file.Files.readAllLines(passwordFile.toPath());
            if (lines.size() < 2) {
                return categories;
            }
            int shift = lines.get(0).length();
            if (!defaultPanel.isCorrectPassword()) shift = 0;

            String encryptedLine = lines.get(1);
            String decryptedLine = caesarDecrypt(encryptedLine, shift);

            System.out.println("Decrypted categories: " + decryptedLine);
            String[] categoriesArray = decryptedLine.split("\\s+");
            if (!defaultPanel.isCorrectPassword()) {
                String split = caesarEncrypt(" ", lines.getFirst().length());
                categoriesArray = decryptedLine.split(split);
            }
            for (String category : categoriesArray) {
                if(!category.isBlank()){
                    categories.add(category);
                }
            }
        } catch (IOException e){
            JOptionPane.showMessageDialog(mainFrame,
                    "Failed to read password file:\n" + e.getMessage(),
                    "File Error", JOptionPane.ERROR_MESSAGE);
        }
        return categories;
    }
    public JComboBox getCategoryComboBox() {
        return categoryComboBox;
    }

    public DefaultPanel getDefaultPanel() {
        return defaultPanel;
    }
}
