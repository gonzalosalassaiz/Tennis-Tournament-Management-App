package tenis_upm.grupo11;

import tenis_upm.grupo11.functionality.Manager;
import tenis_upm.grupo11.view.MainFrame;


import javax.swing.*;


/**
 * This is the main method class, from where starts the program
 */

public class App {
    private static Manager manager;

    public static void main(String[] args) {
        manager = Manager.getInstance();
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}
