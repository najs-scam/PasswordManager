package GUI.FORMS;

import javax.swing.*;
import java.awt.*;

public class MasterPasswordDialog extends JDialog {
    private JPasswordField passwordField;
    private String password;

    public MasterPasswordDialog(Frame parent) {
        super(parent, "Enter Master Password", true); // modalne okno (nie klikac frame poki istnieje)
        ImageIcon icon = new ImageIcon("src/resources/logo.png");
        this.setIconImage(icon.getImage());
        this.setLayout(new BorderLayout());

        icon.setImage(icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
        JLabel iconLabel = new JLabel(icon);

        JLabel textLabel = new JLabel("Enter your master password:");
        textLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        topPanel.add(iconLabel);
        topPanel.add(textLabel);
        add(topPanel, BorderLayout.NORTH);

        passwordField = new JPasswordField(20);
        passwordField.setEchoChar('•');
        JPanel centerPanel = new JPanel();
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        centerPanel.add(passwordField);
        add(centerPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        okButton.addActionListener(e -> {
            password = new String(passwordField.getPassword());
            dispose();
        });

        cancelButton.addActionListener(e -> {
            password = null;
            dispose();
        });

        getRootPane().setDefaultButton(okButton);

        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
        setIconImage(icon.getImage());
    }
    public String getPassword() {
        return password;
    }
}
