package GUI;

import GUI.FORMS.AddEntryDialog;
import GUI.FORMS.EditEntryDialog;
import GUI.FORMS.MasterPasswordDialog;
import LOGIC.*;
import MODEL.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import java.util.List;

public class DefaultPanel extends JPanel implements CategoriesGetter, CryptogramInterface {
    private final JTable table;
    private final JScrollPane scrollPane;
    private final DefaultTableModel model;
    private final File passwordFile;
    private NavPanel navPanel;
    private TopPanel topPanel;
    private MainFrame mainFrame;
    private List<PasswordObject> visibleEntries;
    private boolean correctPassword;

    public DefaultPanel(File passwordFile, MainFrame mainFrame, boolean correctPassword) {
        this.passwordFile = passwordFile;
        this.mainFrame = mainFrame;
        this.correctPassword = correctPassword;

        // Nagłówki kolumn
        String[] columnNames = {"Title", "Password", "Login", "URL", "Notes"};
        model = new DefaultTableModel(columnNames, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        scrollPane = new JScrollPane(table);
        this.setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);

        // Ustawienie renderera dla kolumny "Password"
        table.getColumnModel().getColumn(1).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JPasswordField passwordField = new JPasswordField();
            passwordField.setText(value != null ? value.toString() : "");
            passwordField.setEchoChar('•');
            passwordField.setBorder(null);
            passwordField.setEditable(false);
            passwordField.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            passwordField.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
            return passwordField;
        });


        // Tworzenie menu kontekstowego
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem addItem = new JMenuItem("Add Entry");
        JMenuItem showPasswordItem = new JMenuItem("Show Entry");
        JMenuItem editItem = new JMenuItem("Edit Entry");
        JMenuItem deleteItem = new JMenuItem("Delete Entry");

        popupMenu.add(addItem);
        popupMenu.add(showPasswordItem);
        popupMenu.add(editItem);
        popupMenu.add(deleteItem);

        /// Obsługa akcji
        addItem.addActionListener(e -> {
            try{
                List<String> lines = Files.readAllLines(passwordFile.toPath());
                if (lines.size() < 2) throw new IllegalStateException("File corrupted");
                AddEntryDialog addEntryDialog = new AddEntryDialog(
                        mainFrame, "Add Entry", passwordFile, getCategoriesFromFileQuick()
                );
                refreshTable((String) navPanel.getCategoryComboBox().getSelectedItem());
            } catch (Exception ex){
                JOptionPane.showMessageDialog(mainFrame, "Failed to add new Entry:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

        });

        editItem.addActionListener(e -> {
            int row = table.getSelectedRow();
            try {
                List<String> lines = Files.readAllLines(passwordFile.toPath());
                if (lines.size() < 2) throw new IllegalStateException("File corrupted");
                if (row != -1) {
                    PasswordObject selected = getSelectedPasswordObjectFromFile(row);
                    if (selected != null) {
                        new EditEntryDialog(mainFrame, "Edit Entry", passwordFile, getCategoriesFromFileQuick(), selected);
                        refreshTable((String) navPanel.getCategoryComboBox().getSelectedItem());
                    }
                }
            } catch (Exception ex){
                JOptionPane.showMessageDialog(mainFrame, "Failed to edit Entry:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteItem.addActionListener(e -> {
            deleteEntry();
        });

        showPasswordItem.addActionListener(e -> {
            showEntry();
        });


        // Listener wspólny dla scrollPane i table
        MouseAdapter mouseListener = new MouseAdapter() {
            private void showPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = table.rowAtPoint(e.getPoint());
                    if (row >= 0 && row < table.getRowCount()) {
                        table.setRowSelectionInterval(row, row);
                        editItem.setEnabled(true);
                        deleteItem.setEnabled(true);
                        showPasswordItem.setEnabled(true);
                    } else {
                        table.clearSelection();
                        editItem.setEnabled(false);
                        deleteItem.setEnabled(false);
                        showPasswordItem.setEnabled(false);
                    }
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }

            public void mousePressed(MouseEvent e) { showPopup(e); }
            public void mouseReleased(MouseEvent e) { showPopup(e); }
        };

        // Dodanie do obu komponentów
        table.addMouseListener(mouseListener);
        scrollPane.addMouseListener(mouseListener);
    }
    public void refreshTable(String selectedCategory) {
        FileManager fileManager = new FileManager(passwordFile, correctPassword);
        visibleEntries = fileManager.loadPasswords(selectedCategory);

        model.setRowCount(0);

        for (PasswordObject entry : visibleEntries) {
            model.addRow(new Object[]{
                    entry.getTitle(),
                    entry.getPassword(),
                    entry.getLogin(),
                    entry.getUrl(),
                    entry.getNotes()
            });
        }
    }
    public void refreshTable(String selectedCategory, String searchQuery) {
        FileManager fileManager = new FileManager(passwordFile, correctPassword);
        List<PasswordObject> entries = fileManager.loadPasswords(selectedCategory);

        visibleEntries = entries.stream()
                .filter(entry -> entry.getTitle().toLowerCase().contains(searchQuery.toLowerCase()))
                .toList();

        model.setRowCount(0);
        for (PasswordObject entry : visibleEntries) {
            model.addRow(new Object[]{
                    entry.getTitle(),
                    entry.getPassword(),
                    entry.getLogin(),
                    entry.getUrl(),
                    entry.getNotes()
            });
        }
    }

    @Override
    public Set<String> getCategoriesFromFileQuick() {
        Set<String> categories = new TreeSet<>();
        try {
            List<String> lines = Files.readAllLines(passwordFile.toPath());
            if (lines.size() < 2) return categories;
            int shift = lines.getFirst().length();
            String decrypted = caesarDecrypt(lines.get(1), shift);
            for (String cat : decrypted.split("\\s+")) {
                if (!cat.isBlank()) categories.add(cat);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return categories;
    }

    public void showEntry(){
        int row = table.getSelectedRow();
        if (row != -1) {
            MasterPasswordDialog masterPasswordDialog = new MasterPasswordDialog(mainFrame);
            masterPasswordDialog.setVisible(true);

            String input = masterPasswordDialog.getPassword();

            if (input == null || input.isBlank()) return;

            try {
                List<String> lines = Files.readAllLines(passwordFile.toPath());
                if (lines.size() < 2) throw new IllegalStateException("File corrupted");

                String encryptedMaster = lines.get(0);
                if (!caesarEncrypt(input, input.length()).equals(encryptedMaster)) {
                    JOptionPane.showMessageDialog(this, "Incorrect master password!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int shift = input.length();


                String tableTitle = table.getValueAt(row, 0).toString();

                String foundLine = null;
                for (int i = 2; i < lines.size(); i++) {
                    String decrypted = caesarDecrypt(lines.get(i), shift);
                    String[] parts = decrypted.split("::");
                    if (parts.length >= 6) {
                        String title = parts[0];
                        if (title.equals(tableTitle)) {
                            foundLine = decrypted;
                            break;
                        }
                    }
                }

                if (foundLine == null) {
                    JOptionPane.showMessageDialog(this, "Entry not found in file!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String[] parts = foundLine.split("::");

                String message = String.format("""
                    TITLE: %s
                    LOGIN: %s
                    PASSWORD: >>> %s <<<
                    URL: %s
                    NOTES: %s
                    CATEGORY: %s
                    """, parts[0], parts[2], parts[1], parts[3], parts[4], parts[5]);

                JTextArea area = new JTextArea(message);
                area.setEditable(false);
                area.setFont(new Font("Monospaced", Font.PLAIN, 14));
                JOptionPane.showMessageDialog(null, new JScrollPane(area), "Entry Details", JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Failed to decrypt entry:\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    public void deleteEntry(){
        int row = table.getSelectedRow();
        if (row != -1) {
            String title = table.getValueAt(row, 0).toString();

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete the entry titled: \"" + title + "\"?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION);

            if (confirm != JOptionPane.YES_OPTION) return;

            MasterPasswordDialog masterPasswordDialog = new MasterPasswordDialog(mainFrame);
            masterPasswordDialog.setVisible(true);

            String input = masterPasswordDialog.getPassword();

            if (input == null || input.isBlank()){
                JOptionPane.showMessageDialog(mainFrame, "Master password is Empty!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                List<String> lines = Files.readAllLines(passwordFile.toPath());
                if (lines.size() < 2) throw new IllegalStateException("File corrupted");

                String encryptedMaster = lines.getFirst();
                if (!caesarEncrypt(input, input.length()).equals(encryptedMaster)) {
                    JOptionPane.showMessageDialog(mainFrame, "Incorrect master password!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int shift = input.length();


                List<String> updatedLines = new ArrayList<>();
                updatedLines.add(lines.getFirst());
                updatedLines.add(lines.get(1));

                boolean removed = false;

                for (int i = 2; i < lines.size(); i++) {
                    String decrypted = caesarDecrypt(lines.get(i), shift);
                    String[] parts = decrypted.split("::");
                    if (parts.length >= 6 && parts[0].equals(title) && !removed) {
                        removed = true;
                        continue;
                    }
                    updatedLines.add(lines.get(i));
                }

                if (!removed) {
                    JOptionPane.showMessageDialog(this, "Entry not found!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                ///  Nadpsiuje plik z usunietym entry
                Files.write(passwordFile.toPath(), updatedLines);
                model.removeRow(row);

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Failed to delete entry:\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        refreshTable((String) navPanel.getCategoryComboBox().getSelectedItem());
    }

    private PasswordObject getSelectedPasswordObjectFromFile(int row) {
        try {
            String selectedCategory = (String) navPanel.getCategoryComboBox().getSelectedItem();
            String selectedTitle = (String) table.getValueAt(row, 0);

            List<String> lines = Files.readAllLines(passwordFile.toPath());
            if (lines.size() < 3) return null;

            String masterPassword = lines.getFirst();
            int shift = masterPassword.length();


            for (int i = 2; i < lines.size(); i++) {
                String decryptedLine = caesarDecrypt(lines.get(i), shift);
                String[] parts = decryptedLine.split("::");
                if (parts.length < 6) continue;

                String title = parts[0];
                String password = parts[1];
                String login = parts[2];
                String url = parts[3];
                String notes = parts[4];
                String category = parts[5];

                boolean matchCategory = selectedCategory.equals("All") || category.equals(selectedCategory);
                boolean matchTitle = title.equals(selectedTitle);

                if (matchCategory && matchTitle) {
                    return new PasswordObject(title, password, login, url, notes, category);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public TopPanel getTopPanel() {
        return topPanel;
    }

    public void setTopPanel(TopPanel topPanel) {
        this.topPanel = topPanel;
    }

    public NavPanel getNavPanel() {
        return navPanel;
    }

    public void setNavPanel(NavPanel navPanel) {
        this.navPanel = navPanel;
    }
    public boolean isCorrectPassword() {
        return correctPassword;
    }
}
