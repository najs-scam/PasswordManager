package LOGIC;
import GUI.*;

import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class FileCreator implements CryptogramInterface {
    private String fileName;
    private String mainPassword;
    private String confirmPassword;
    private final CreatePassFilePanel panel;

    public FileCreator(String fileName, String mainPassword, String confirmPassword, CreatePassFilePanel panel) {
        this.fileName = fileName;
        this.mainPassword = mainPassword;
        this.confirmPassword = confirmPassword;
        this.panel = panel;
        try{

            String folderName = "Passwords";
            java.io.File directory = new java.io.File(folderName);
            if (!directory.exists()) {
                directory.mkdir();
            }
            String filename = folderName + "/" + fileName + ".pass";
            java.io.File file = new java.io.File(filename);

            if (file.createNewFile()){
                String encryptedMain = caesarEncrypt(mainPassword, mainPassword.length());

                try(FileWriter writer = new FileWriter(filename)){
                    writer.write(encryptedMain + "\r\n");

                    Set<String> defaultCategories = new LinkedHashSet<>();
                    defaultCategories.add("All");
                    defaultCategories.add("Email");
                    defaultCategories.add("Bank");
                    defaultCategories.add("Others");

                    String categoryLine = String.join(" ", defaultCategories);

                    String encryptedCategories = caesarEncrypt(categoryLine, mainPassword.length());

                    writer.write(encryptedCategories + "\r\n");

                }
                JOptionPane.showMessageDialog(panel, "File was created\n" + file.getAbsolutePath());
                ///  wyswetla explorator plikow by pokazac ze plik powstawl
                java.awt.Desktop.getDesktop().open(file.getParentFile());
            } else {
                JOptionPane.showMessageDialog(panel, "File already exists.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(panel,
                    "Error creating or writing to file:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE
            );
        }
    }

}
