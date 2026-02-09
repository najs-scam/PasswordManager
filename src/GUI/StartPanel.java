package GUI;

import javax.swing.*;
import java.awt.*;

public class StartPanel extends JPanel {
    private final JButton createNewButton;
    private final JButton selectExistingButton;

    public StartPanel(MainFrame mainFrame) {

        createNewButton = new JButton("<html><center>Create<br>New Password</center></html>");
        selectExistingButton = new JButton("<html><center>Select<br>Existing Password</center></html>");

        Dimension buttonSize = new Dimension(150, 50);
        createNewButton.setPreferredSize(buttonSize);
        selectExistingButton.setPreferredSize(buttonSize);

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        add(Box.createRigidArea(new Dimension(20, 0)));
        add(Box.createHorizontalGlue());
        add(createNewButton);
        add(Box.createRigidArea(new Dimension(20, 0)));

        add(selectExistingButton);
        add(Box.createRigidArea(new Dimension(20, 0)));
        add(Box.createHorizontalGlue());

        selectExistingButton.addActionListener(e -> mainFrame.initSelect());
        createNewButton.addActionListener(e -> mainFrame.initCreate());
    }
}
