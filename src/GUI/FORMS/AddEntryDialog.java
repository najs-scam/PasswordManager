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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AddEntryDialog extends JDialog implements CryptogramInterface {

    public AddEntryDialog(MainFrame mainFrame, String dialogTitle, File passwordFile, Set<String> categories) {
        super(mainFrame, dialogTitle, true);

        MasterPasswordDialog masterPasswordDialog = new MasterPasswordDialog(mainFrame);
        masterPasswordDialog.setVisible(true);
        String masterPassword = masterPasswordDialog.getPassword();

        if (masterPassword == null || masterPassword.isBlank()) {
            JOptionPane.showMessageDialog(mainFrame, "Incorrect master password!", "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
            return;
        }

        try {
            List<String> lines = Files.readAllLines(passwordFile.toPath());
            if (lines.size() < 2) throw new IllegalStateException("File corrupted");

            String encryptedMaster = lines.getFirst();
            int shift = masterPassword.length();

            if (!caesarEncrypt(masterPassword, shift).equals(encryptedMaster)) {
                JOptionPane.showMessageDialog(mainFrame, "Incorrect master password!", "Error", JOptionPane.ERROR_MESSAGE);
                this.dispose();
                return;
            }

            this.setLayout(new BorderLayout());

            JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));

            JTextField titleField = new JTextField();
            JTextField passwordField = new JTextField();
            JTextField loginField = new JTextField();
            JTextField urlField = new JTextField();
            JTextField notesField = new JTextField();

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

            // All i Others na koniec i poczatek
            List<String> categoryList = new ArrayList<>();
            if (!categories.contains("All")) {
                categoryList.add("All");
            }
            for (String cat : categories) {
                if (!cat.equals("All") && !cat.equals("Others")) {
                    categoryList.add(cat);
                }
            }
            if (categories.contains("Others")) {
                categoryList.add("Others");
            }

            JComboBox<String> categoryComboBox = new JComboBox<>(categoryList.toArray(new String[0]));
            categoryComboBox.setSelectedItem("Others");

            formPanel.add(new JLabel("Category:"));
            formPanel.add(categoryComboBox);

            this.add(formPanel, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel();
            JButton addButton = new JButton("Add");
            JButton generateButton = new JButton("Generate");
            JButton cancelButton = new JButton("Cancel");

            buttonPanel.add(addButton);
            buttonPanel.add(generateButton);
            buttonPanel.add(cancelButton);

            this.add(buttonPanel, BorderLayout.SOUTH);

            addButton.addActionListener(e -> {
                String title = titleField.getText();
                String password = passwordField.getText();
                String login = loginField.getText();
                String url = urlField.getText();
                String notes = notesField.getText();
                String category = (String) categoryComboBox.getSelectedItem();

                if (title.isBlank() && password.isBlank()){
                    JOptionPane.showMessageDialog(this, "Please fill Title and Password fields", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                } else if (title.isBlank()) {
                    JOptionPane.showMessageDialog(this, "Please fill Title field", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                } else if (password.isBlank()) {
                    JOptionPane.showMessageDialog(this, "Please fill Password field", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                PasswordObject newEntry = new PasswordObject(title, password, login, url, notes, category);
                new PasswordCreating(passwordFile, mainFrame, newEntry);

                this.dispose();
            });

            generateButton.addActionListener(e -> {
                String g = generatePassword();
                passwordField.setText(g);
            });

            cancelButton.addActionListener(e -> this.dispose());

            this.setSize(400, 320);
            this.setLocationRelativeTo(null);
            this.setVisible(true);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(mainFrame, "Failed to verify password:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        }

    }
    public String generatePassword() {
        StringBuilder rndPassword = new StringBuilder();
        for (int i = 0; i < 15; i++) {
            rndPassword.append((char) ((int) (Math.random() * 58) + 65));
        }
        return rndPassword.toString();
    }
}
