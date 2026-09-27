package tenis_upm.grupo11.view;

import tenis_upm.grupo11.view.interfaces.IParentView;
import tenis_upm.grupo11.view.interfaces.IRefreshable;
import tenis_upm.grupo11.view.outsidepanels.CheckConfirmationPanel;
import tenis_upm.grupo11.view.outsidepanels.HomePanel;
import tenis_upm.grupo11.view.outsidepanels.LoginPanel;
import tenis_upm.grupo11.view.outsidepanels.SignUpPanel;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * This is the frame of the application containing the panel seeing
 * at the current moment
 */
public class MainFrame extends JFrame implements IParentView {
    private JPanel currentPanel;
    private Map<String, JPanel> childPanels;

    /**
     * MainFrame's constructor
     */
    public MainFrame() {
        setTitle("Tenis UPM");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setExtendedState(JFrame.NORMAL);
        mapChildPanels();
        currentPanel = LoginPanel.getInstance();
        add(currentPanel);
    }

    @Override
    public void mapChildPanels() {
        childPanels = new HashMap<>();
        childPanels.put("Login", LoginPanel.getInstance());
        childPanels.put("SignUp", SignUpPanel.getInstance());
        childPanels.put("CheckConfirmation", CheckConfirmationPanel.getInstance());
        childPanels.put("Home", HomePanel.getInstance());
    }

    @Override
    public void switchPanel(String panelCodename) {
        if (childPanels.containsKey(panelCodename)) {
            remove(currentPanel);
            currentPanel = childPanels.get(panelCodename);
            add(currentPanel, BorderLayout.CENTER);
            if (currentPanel instanceof IRefreshable) {
                ((IRefreshable) currentPanel).refresh();
            }
            revalidate();
            repaint();
        }
    }
}
