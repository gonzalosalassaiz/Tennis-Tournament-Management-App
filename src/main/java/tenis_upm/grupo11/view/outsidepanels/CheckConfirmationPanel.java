package tenis_upm.grupo11.view.outsidepanels;

import tenis_upm.grupo11.view.MainFrame;
import tenis_upm.grupo11.view.utils.AppColors;
import tenis_upm.grupo11.view.utils.AppFonts;

import javax.swing.*;
import java.awt.*;

/**
 * This is the panel that shows an email confirmed message
 */
public class CheckConfirmationPanel extends JPanel {
    private static CheckConfirmationPanel instance;

    /**
     * CheckConfirmationPanel's constructor
     */
    private CheckConfirmationPanel() {
        setBackground(AppColors.BACKGROUND);
        setLayout(new GridBagLayout());

        // Add the message panel and button panel
        addMessagePanel();
        createGoToLoginButton();
    }

    /**
     * Singleton proper function to get the only instance of this class
     *
     * @return the only instance of this class
     */
    public static CheckConfirmationPanel getInstance() {
        if (instance == null) instance = new CheckConfirmationPanel();
        return instance;
    }

    /**
     * Method to create and add the message panel to the main panel
     */
    private void addMessagePanel() {
        JPanel messagePanel = new JPanel();
        messagePanel.setBackground(AppColors.PANEL_BACKGROUND);
        messagePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        messagePanel.setLayout(new BorderLayout());
        messagePanel.setPreferredSize(new Dimension(300, 150));

        JLabel messageLabel = createMessageLabel();
        messagePanel.add(messageLabel, BorderLayout.CENTER);

        // Add the message panel to the layout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.CENTER;
        add(messagePanel, gbc);
    }

    /**
     * Method to create the message label with the confirmation text
     *
     * @return the message label
     */
    private JLabel createMessageLabel() {
        JLabel messageLabel = new JLabel("¡Ya estás registrado!", SwingConstants.CENTER);
        messageLabel.setFont(AppFonts.CONFIRMATION_TITLE_FONT);
        messageLabel.setForeground(AppColors.FONT);
        return messageLabel;
    }

    /**
     * Method to create the "Go to log in" button
     *
     * @return the button
     */
    private void createGoToLoginButton() {
        JButton goToLoginButton = new JButton("Volver al login");
        goToLoginButton.setFont(AppFonts.BUTTON_FONT);
        goToLoginButton.setBackground(AppColors.CHECK_VERIFICATION_BUTTON);
        goToLoginButton.setForeground(AppColors.FONT);
        goToLoginButton.setFocusPainted(false);
        goToLoginButton.setBorder(null);
        goToLoginButton.setPreferredSize(new Dimension(200, 40));

        // Action listener for the button
        goToLoginButton.addActionListener(e -> {
            ((MainFrame) SwingUtilities.getWindowAncestor(CheckConfirmationPanel.this))
                    .switchPanel("Login");
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 1;
        add(goToLoginButton, gbc);
    }
}
