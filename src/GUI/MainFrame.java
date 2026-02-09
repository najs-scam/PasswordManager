package GUI;

import GUI.FORMS.MasterPasswordDialog;
import LOGIC.CryptogramInterface;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class MainFrame extends JFrame implements CryptogramInterface {
    private static final String MAIN_PASSWORD = "123";
    public MainFrame() {
        this.setIconImage(new ImageIcon("src/resources/logo.png").getImage());
        lunch();
    }
    public void initSelect() {
        this.getContentPane().removeAll();
        this.setSize(700, 500);
        this.setLocationRelativeTo(null);

        boolean validFileChosen = false;

        while (!validFileChosen) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File("Passwords"));
            fileChooser.setFileFilter(new FileNameExtensionFilter("Password files (.pass)", "pass"));

            int returnVal = fileChooser.showOpenDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile().getAbsoluteFile();

                if (!file.getName().toLowerCase().endsWith(".pass")) {
                    JOptionPane.showMessageDialog(this,
                            "Invalid file type selected.\nPlease select a file with the '.pass' extension.",
                            "Invalid File", JOptionPane.ERROR_MESSAGE);
                } else {
                    String encrypted;
                    try {
                        encrypted = java.nio.file.Files.lines(file.toPath())
                                .findFirst()
                                .orElse("");
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(this,
                                "Could not read the password file.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        continue;
                    }

                    MasterPasswordDialog masterPasswordDialog = new MasterPasswordDialog(this);
                    masterPasswordDialog.setVisible(true);

                    String inputPassword = masterPasswordDialog.getPassword();
                    if (inputPassword == null || inputPassword.isBlank()) {
                        continue;
                    }

                    String decrypted = caesarDecrypt(encrypted, inputPassword.length());
                    boolean correctPassword = decrypted.equals(inputPassword);
                    System.out.println(correctPassword);
                    if (decrypted.equals(inputPassword)) {
                        System.out.println("Correct master password.");
                        validFileChosen = true;
                        initManager(file, correctPassword);
                    } else {
                        System.out.println("Incorrect master password.");
                        validFileChosen = true;
                        initManager(file, correctPassword);
                    }
                }
            } else if (returnVal == JFileChooser.CANCEL_OPTION) {
                lunch();
                break;
            }
        }

        this.revalidate();
        this.repaint();
    }
    public void initManager( File passwordFile, boolean correctPassword) {
        ManagerPanel managerPanel = new ManagerPanel(this, passwordFile, correctPassword);
        this.getContentPane().add(managerPanel);
    }
    public void initCreate() {
        this.getContentPane().removeAll();

        JPanel createPanel = new CreatePassFilePanel(this);
        this.add(createPanel);

        this.revalidate();
        this.repaint();
    }
    public void lunch(){
        this.getContentPane().removeAll();

        this.setSize(400, 300);
        this.setLocationRelativeTo(null);
        this.setTitle("Password Manager");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(true);

        StartPanel startPanel = new StartPanel(this);
        this.add(startPanel);

        this.setVisible(true);
    }
}

