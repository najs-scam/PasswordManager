package GUI;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ManagerPanel extends JPanel {
    private File passwordFile;
    public ManagerPanel(MainFrame mainFrame, File passwordFile, boolean correctPassword) {
        this.setLayout(new BorderLayout());

        ///  DEFAULT PANEL
        DefaultPanel defaultPanel = new DefaultPanel(passwordFile, mainFrame, correctPassword);
        this.add(defaultPanel, BorderLayout.CENTER);

        /// NAV PANEL
        NavPanel navPanel = new NavPanel(passwordFile, mainFrame, defaultPanel);
        this.add(navPanel, BorderLayout.WEST);

        /// TOP PANEL
        TopPanel topPanel = new TopPanel(passwordFile, mainFrame, navPanel, defaultPanel);
        this.add(topPanel, BorderLayout.NORTH);

        defaultPanel.setNavPanel(navPanel);
    }
}
