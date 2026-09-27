package tenis_upm.grupo11.view.utils;

import java.awt.Component;
import javax.swing.*;

/**
 * This is a utility class to show pop-ups
 */
public class PopUp {

    /**
     * Show an error pop-up
     *
     * @param component which component want to show the pop-up
     * @param title the title of the pop-up
     * @param body the body text of the pop-up
     */
    public static void error(Component component, String title, String body) {
        JOptionPane.showMessageDialog(component, body, title, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Show an informative pop-up
     *
     * @param component which component want to show the pop-up
     * @param title the title of the pop-up
     * @param body the body text of the pop-up
     */
    public static void info(Component component, String title, String body) {
        JOptionPane.showMessageDialog(component, body, title, JOptionPane.INFORMATION_MESSAGE);
    }
}
