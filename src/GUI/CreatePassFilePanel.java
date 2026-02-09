package GUI;

import LOGIC.FileCreator;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class CreatePassFilePanel extends JPanel {
    private final JTextField nameField;
    private final JPasswordField passField;
    private final JPasswordField confirmField;
    private final MainFrame mainFrame;
    public CreatePassFilePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        // Inicjalizacja komponentów
        JButton submitButton = new JButton("Submit");
        JButton cancelButton = new JButton("Cancel");

        nameField = new JTextField();
        passField = new JPasswordField();
        confirmField = new JPasswordField();

        setTextFieldHeight(nameField, 25);
        setTextFieldHeight(passField, 25);
        setTextFieldHeight(confirmField, 25);

        JLabel nameLabel = new JLabel("Name");
        JLabel passwordLabel = new JLabel("Password");
        JLabel confirmPasswordLabel = new JLabel("Confirm Password");

        JPanel leftPanel = new JPanel();
        JPanel rightPanel = new JPanel();
        JPanel bottomPanel = new JPanel();

        this.setLayout(new BorderLayout());

        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        leftPanel.add(Box.createVerticalGlue());
        leftPanel.add(nameLabel);
        leftPanel.add(nameField);
        leftPanel.add(Box.createVerticalStrut(10));

        leftPanel.add(passwordLabel);
        leftPanel.add(passField);
        leftPanel.add(Box.createVerticalStrut(10));

        leftPanel.add(confirmPasswordLabel);
        leftPanel.add(confirmField);
        leftPanel.add(Box.createVerticalGlue());

        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        bottomPanel.add(Box.createHorizontalGlue());
        bottomPanel.add(submitButton);
        bottomPanel.add(Box.createHorizontalGlue());
        bottomPanel.add(cancelButton);
        bottomPanel.add(Box.createHorizontalGlue());

        this.add(leftPanel, BorderLayout.CENTER);
        this.add(rightPanel, BorderLayout.EAST);
        this.add(bottomPanel, BorderLayout.SOUTH);

        submitButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            } else if (passField.getPassword().length == 0) {
                JOptionPane.showMessageDialog(this, "Password cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            } else if (confirmField.getPassword().length == 0) {
                JOptionPane.showMessageDialog(this, "Confirm Password cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            } else if (!Arrays.equals(passField.getPassword(), confirmField.getPassword())) {
                /// BO getPassword zwraca tablice char
                JOptionPane.showMessageDialog(this, "Password confirmation must match the password.", "Warning",JOptionPane.WARNING_MESSAGE);
                this.getPassField().setText("");
                this.getConfirmField().setText("");
                return;
            }

            String password = String.valueOf(passField.getPassword());
            String confirmPassword = String.valueOf(confirmField.getPassword());
            FileCreator fileCreator = new FileCreator(name, password, confirmPassword, this);
            mainFrame.lunch();
        });

        cancelButton.addActionListener(e -> {
            this.mainFrame.lunch();
        });

    }
    public JTextField getNameField() {
        return nameField;
    }

    public JPasswordField getPassField() {
        return passField;
    }

    public JPasswordField getConfirmField() {
        return confirmField;
    }

    public void setTextFieldHeight(JTextField field, int height){
        Dimension original = field.getPreferredSize();
        Dimension fixed = new Dimension(original.width, height);
        field.setPreferredSize(fixed);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
        field.setMinimumSize(fixed);
    }
}