package LOGIC;

import GUI.MainFrame;
import MODEL.PasswordObject;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class PasswordCreating implements CryptogramInterface {
    private final File passwordFile;

    public PasswordCreating(File passwordFile, MainFrame mainFrame, PasswordObject passwordObject) {
        this.passwordFile = passwordFile;
        try {
            List<String> lines = Files.readAllLines(passwordFile.toPath());
            if (lines.size() < 2) {
                JOptionPane.showMessageDialog(mainFrame,
                        "Password file format is invalid.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String encryptedMaster = lines.get(0);
            int shift = encryptedMaster.length();

            String newEntryPlain = String.join("::",
                    passwordObject.getTitle(),
                    passwordObject.getPassword(),
                    passwordObject.getLogin(),
                    passwordObject.getUrl(),
                    passwordObject.getNotes(),
                    passwordObject.getCategory()
            );

            String encryptedEntry = caesarEncrypt(newEntryPlain, shift);

            lines.add(encryptedEntry);
            Files.write(passwordFile.toPath(), lines);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainFrame,
                    "Failed to add password:\n" + e.getMessage(),
                    "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
