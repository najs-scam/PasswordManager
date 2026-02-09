package LOGIC;

import MODEL.PasswordObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileManager extends DefaultTableModel implements CryptogramInterface {
    private final File passwordFile;
    private boolean correctPassword;

    public FileManager(File passwordFile, boolean correctPassword) {
        this.passwordFile = passwordFile;
        this.correctPassword = correctPassword;
    }

    public List<PasswordObject> loadPasswords(String selectedCategory) {
        List<PasswordObject> passwords = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(passwordFile.toPath());
            if (lines.size() < 3) return passwords;

            String encryptedMaster = lines.getFirst();
            int shift = correctPassword ? encryptedMaster.length() : 0;

            for (int i = 2; i < lines.size(); i++) {
                String line = lines.get(i);
                String[] parts;

                if (correctPassword) {
                    String decrypted = caesarDecrypt(line, shift);
                    parts = decrypted.split("::");
                } else {
                    // og speparator przy zlym hasle
                    String split = caesarEncrypt("::", lines.getFirst().length());
                    parts = line.split(split);
                }

                if (parts.length == 6) {
                    String title = parts[0];
                    String password = parts[1];
                    String login = parts[2];
                    String url = parts[3];
                    String notes = parts[4];
                    String category = parts[5];

                    if (selectedCategory.equals("All") || selectedCategory.equals(category)) {
                        passwords.add(new PasswordObject(title, password, login, url, notes, category));
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return passwords;
    }
}
