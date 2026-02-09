package LOGIC;

import GUI.FORMS.MasterPasswordDialog;
import GUI.MainFrame;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public interface CategoryManager extends CryptogramInterface{
    default void addNewCategory(String newCategory, File passwordFile, JComboBox categoryComboBox, MainFrame mainFrame) {
        if (newCategory == null || newCategory.isBlank())
            return;

        if (newCategory.contains(" ")){
            JOptionPane.showMessageDialog(mainFrame, "Category name cannot contain spaces.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        MasterPasswordDialog masterPasswordDialog = new MasterPasswordDialog(mainFrame);
        masterPasswordDialog.setVisible(true);

        String inputPassword = masterPasswordDialog.getPassword();
        if (inputPassword == null || inputPassword.isBlank())
            return;

        try {
            List<String> lines = Files.readAllLines(passwordFile.toPath());
            if (lines.isEmpty()) return;

            int shift = lines.getFirst().length();

            String encryptedMaster = lines.getFirst();
            if (!caesarEncrypt(inputPassword, inputPassword.length()).equals(encryptedMaster)) {
                JOptionPane.showMessageDialog(mainFrame, "Incorrect master password!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Set<String> updatedCategories = new TreeSet<>();
            if (lines.size() >= 2) {
                String decryptedCategories = caesarDecrypt(lines.get(1), shift);
                String[] existing = decryptedCategories.trim().split("\\s+");
                updatedCategories.addAll(Arrays.asList(existing));
            }

            updatedCategories.add(newCategory.trim());

            String newDecryptedLine = String.join(" ", updatedCategories);
            String encryptedCategories = caesarEncrypt(newDecryptedLine, shift);

            lines = new ArrayList<>(lines);
            if (lines.size() >= 2) {
                lines.set(1, encryptedCategories);
            } else {
                lines.add(encryptedCategories);
            }

            Files.write(passwordFile.toPath(), lines);

            List<String> sortedWithAllAndOthers = new ArrayList<>();
            sortedWithAllAndOthers.add("All");

            List<String> middleCategories = new ArrayList<>();
            for (String cat : updatedCategories) {
                if (!cat.equals("All") && !cat.equals("Others")) {
                    middleCategories.add(cat);
                }
            }

            Collections.sort(middleCategories);
            sortedWithAllAndOthers.addAll(middleCategories);

            if (updatedCategories.contains("Others")) {
                sortedWithAllAndOthers.add("Others");
            }

            categoryComboBox.setModel(new DefaultComboBoxModel<>(
                    sortedWithAllAndOthers.toArray(new String[0])
            ));

        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainFrame,
                    "Failed to update categories:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    default void deleteCategory(String categoryToRemove, File passwordFile, JComboBox categoryComboBox, MainFrame mainFrame) {
        // Blokuje usuwanie  All i Others
        if ("All".equals(categoryToRemove) || "Others".equals(categoryToRemove)) {
            JOptionPane.showMessageDialog(mainFrame,
                    "Category \"" + categoryToRemove + "\" cannot be deleted.",
                    "Operation not allowed",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            List<String> lines = Files.readAllLines(passwordFile.toPath());
            if (lines.size() < 2) return;

            String encryptedMaster = lines.get(0);
            int shift = encryptedMaster.length();

            int confirm = JOptionPane.showConfirmDialog(
                    mainFrame,
                    "Are you sure you want to delete the category \"" + categoryToRemove + "\"?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            MasterPasswordDialog masterPasswordDialog = new MasterPasswordDialog(mainFrame);
            masterPasswordDialog.setVisible(true);

            String inputMaster = masterPasswordDialog.getPassword();

            if (inputMaster == null || inputMaster.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame,
                        "Deletion cancelled: master password not provided.",
                        "Cancelled",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String encryptedInputMaster = caesarEncrypt(inputMaster, shift);
            if (!encryptedInputMaster.equals(encryptedMaster)) {
                JOptionPane.showMessageDialog(mainFrame,
                        "Incorrect master password. Deletion aborted.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            String decryptedCategories = caesarDecrypt(lines.get(1), shift);
            Set<String> updatedCategories = new TreeSet<>(Arrays.asList(decryptedCategories.trim().split("\\s+")));

            updatedCategories.remove(categoryToRemove);

            String newDecryptedLine = String.join(" ", updatedCategories);
            String encryptedCategories = caesarEncrypt(newDecryptedLine, shift);
            lines.set(1, encryptedCategories);

            List<String> filteredEntries = new ArrayList<>();
            for (int i = 2; i < lines.size(); i++) {
                String decryptedLine = caesarDecrypt(lines.get(i), shift);
                String[] parts = decryptedLine.split("::");
                if (parts.length < 6) {
                    continue;
                }
                String entryCategory = parts[5];
                if (!entryCategory.equals(categoryToRemove)) {
                    filteredEntries.add(lines.get(i));
                }
            }

            List<String> newFileLines = new ArrayList<>();
            newFileLines.add(lines.get(0));
            newFileLines.add(lines.get(1));
            newFileLines.addAll(filteredEntries);

            Files.write(passwordFile.toPath(), newFileLines);

            List<String> sortedWithAllAndOthers = new ArrayList<>();
            sortedWithAllAndOthers.add("All");

            List<String> middleCategories = new ArrayList<>();
            for (String cat : updatedCategories) {
                if (!cat.equals("All") && !cat.equals("Others")) {
                    middleCategories.add(cat);
                }
            }

            Collections.sort(middleCategories);
            sortedWithAllAndOthers.addAll(middleCategories);

            if (updatedCategories.contains("Others")) {
                sortedWithAllAndOthers.add("Others");
            }

            categoryComboBox.setModel(new DefaultComboBoxModel<>(
                    sortedWithAllAndOthers.toArray(new String[0])
            ));

        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainFrame,
                    "Failed to remove category:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

}
