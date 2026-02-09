package GUI.FORMS;

import GUI.MainFrame;
import LOGIC.CryptogramInterface;
import LOGIC.PasswordCreating;
import MODEL.PasswordObject;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.List;

public class EditEntryDialog extends JDialog implements CryptogramInterface {

    public EditEntryDialog(MainFrame mainFrame, String dialogTitle, File passwordFile, Set<String> categories, PasswordObject entryToEdit) {
        super(mainFrame, dialogTitle, true);

        MasterPasswordDialog masterPasswordDialog = new MasterPasswordDialog(mainFrame);
        masterPasswordDialog.setVisible(true);
        String masterPassword = masterPasswordDialog.getPassword();

        if (masterPassword == null || masterPassword.isBlank()) {
            JOptionPane.showMessageDialog(mainFrame, "Incorrect master password!", "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
            return;
        }

        initEditUI(mainFrame, passwordFile, categories, entryToEdit, masterPassword);
    }

    private void initEditUI(MainFrame mainFrame, File passwordFile, Set<String> categories, PasswordObject entryToEdit, String masterPassword) {
        this.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));

        JTextField titleField = new JTextField(entryToEdit.getTitle());
        JTextField passwordField = new JTextField(entryToEdit.getPassword());
        JTextField loginField = new JTextField(entryToEdit.getLogin());
        JTextField urlField = new JTextField(entryToEdit.getUrl());
        JTextField notesField = new JTextField(entryToEdit.getNotes());

        formPanel.add(new JLabel("Title:"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(passwordField);
        formPanel.add(new JLabel("Login:"));
        formPanel.add(loginField);
        formPanel.add(new JLabel("URL:"));
        formPanel.add(urlField);
        formPanel.add(new JLabel("Notes:"));
        formPanel.add(notesField);

        List<String> categoryList = new ArrayList<>();
        if (!categories.contains("All")) categoryList.add("All");
        for (String cat : categories) {
            if (!cat.equals("All") && !cat.equals("Others")) {
                categoryList.add(cat);
            }
        }
        if (categories.contains("Others")) categoryList.add("Others");

        JComboBox<String> categoryComboBox = new JComboBox<>(categoryList.toArray(new String[0]));
        categoryComboBox.setSelectedItem(entryToEdit.getCategory());

        formPanel.add(new JLabel("Category:"));
        formPanel.add(categoryComboBox);
        this.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        this.add(buttonPanel, BorderLayout.SOUTH);

        saveButton.addActionListener(e -> {
            try {
                List<String> lines = Files.readAllLines(passwordFile.toPath());
                List<String> updatedLines = new ArrayList<>();
                updatedLines.add(lines.get(0)); // master
                updatedLines.add(lines.get(1)); // categories

                boolean updated = false;
                int shift = masterPassword.length();

                for (int i = 2; i < lines.size(); i++) {
                    String decrypted = caesarDecrypt(lines.get(i), shift);
                    String[] parts = decrypted.split("::");

                    if (parts.length >= 6 && parts[0].equals(entryToEdit.getTitle()) && !updated) {
                        String newEntry = String.join("::",
                                titleField.getText(),
                                passwordField.getText(),
                                loginField.getText(),
                                urlField.getText(),
                                notesField.getText(),
                                (String) categoryComboBox.getSelectedItem()
                        );
                        updatedLines.add(caesarEncrypt(newEntry, shift));
                        updated = true;
                    } else {
                        updatedLines.add(lines.get(i));
                    }
                }

                if (!updated) {
                    JOptionPane.showMessageDialog(this, "Entry not found!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Files.write(passwordFile.toPath(), updatedLines);
                this.dispose();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Failed to save entry:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> this.dispose());

        this.setSize(400, 320);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}