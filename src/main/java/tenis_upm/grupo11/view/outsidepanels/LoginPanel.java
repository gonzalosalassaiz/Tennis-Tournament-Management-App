package tenis_upm.grupo11.view.outsidepanels;

import tenis_upm.grupo11.functionality.Manager;
import tenis_upm.grupo11.view.MainFrame;
import tenis_upm.grupo11.view.interfaces.IRefreshable;
import tenis_upm.grupo11.view.utils.*;

import javax.swing.*;
import java.awt.*;

/**
 * This is the panel from where the users can log in.
 */
public class LoginPanel extends JPanel implements IRefreshable {
    private static LoginPanel instance;
    private static final int MAX_CHARACTERS = 16;
    private JTextField userField;
    private JPasswordField passwordField;
    private JButton togglePasswordButton;

    /**
     * Private constructor to initialize the LoginPanel.
     */
    private LoginPanel() {
        setBackground(AppColors.BACKGROUND);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        // Creates and adds the component groups to the panel
        addTitle(gbc);
        addUsernameField(gbc);
        addPasswordField(gbc);
        addLoginButton(gbc);
        addSignUpButton(gbc);
        addForgottenPasswordButton(gbc);
    }

    /**
     * Singleton proper function to get the only instance of this class.
     *
     * @return the only instance of this class.
     */
    public static LoginPanel getInstance() {
        if (instance == null) instance = new LoginPanel();
        return instance;
    }

    /**
     * Creates the components from the title component group.
     *
     * @param gbc the grid for the components.
     */
    private void addTitle(GridBagConstraints gbc) {
        gbc.insets = new Insets(20, 10, 20, 10);
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Create and add the title label
        JLabel titleLabel = new JLabel("Login", SwingConstants.CENTER);
        titleLabel.setFont(AppFonts.TITLE_FONT);
        titleLabel.setForeground(AppColors.FONT);
        add(titleLabel, gbc);

        gbc.gridy = 0;
    }

    /**
     * Creates the components from the username component group.
     *
     * @param gbc the grid for the components.
     */
    private void addUsernameField(GridBagConstraints gbc) {
        gbc.insets = new Insets(10, 10, 5, 10);

        // Create and add the username label
        JLabel usernameLabel = new JLabel("Nombre de Usuario:");
        usernameLabel.setFont(AppFonts.LABEL_TEXTFIELD_FONT);
        usernameLabel.setForeground(AppColors.LABEL_TEXTFIELD);
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(usernameLabel, gbc);

        // Create and add the username field
        userField = new JTextField(20);
        userField.setPreferredSize(new Dimension(250, 30));
        userField.setFont(AppFonts.TEXTFIELD_FONT);
        userField.setBackground(AppColors.TEXTFIELD);
        userField.setForeground(AppColors.FONT);
        userField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        userField.setDocument(new LimitedDocument(MAX_CHARACTERS));
        gbc.gridx = 1;
        add(userField, gbc);

        // Create and add the username character count
        JLabel userCharCount = new JLabel("0/" + MAX_CHARACTERS);
        userCharCount.setFont(AppFonts.CHAR_COUNT_FONT);
        userCharCount.setForeground(AppColors.CHAR_COUNT_TEXT);
        gbc.insets = new Insets(0, 10, 5, 10);
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(userCharCount, gbc);

        userField.getDocument().addDocumentListener(
                new CharCountUpdater(userField, userCharCount, MAX_CHARACTERS));
    }

    /**
     * Creates the components from the password component group.
     *
     * @param gbc the grid for the components.
     */
    private void addPasswordField(GridBagConstraints gbc) {
        gbc.insets = new Insets(10, 10, 5, 10);

        // Create and add the password label
        JLabel passwordLabel = new JLabel("Contraseña:");
        passwordLabel.setFont(AppFonts.LABEL_TEXTFIELD_FONT);
        passwordLabel.setForeground(AppColors.LABEL_TEXTFIELD);
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(passwordLabel, gbc);

        // Create and add the password field
        passwordField = new JPasswordField(20);
        passwordField.setPreferredSize(new Dimension(250, 30));
        passwordField.setFont(AppFonts.TEXTFIELD_FONT);
        passwordField.setBackground(AppColors.TEXTFIELD);
        passwordField.setForeground(AppColors.FONT);
        passwordField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        passwordField.setDocument(new LimitedDocument(MAX_CHARACTERS));
        gbc.gridx = 1;
        add(passwordField, gbc);

        // Create and add the password visibility button
        createTogglePasswordButton();
        gbc.gridx = 2;
        add(togglePasswordButton, gbc);

        // Create and add the password character count
        JLabel passCharCount = new JLabel("0/" + MAX_CHARACTERS);
        passCharCount.setFont(AppFonts.CHAR_COUNT_FONT);
        passCharCount.setForeground(AppColors.CHAR_COUNT_TEXT);
        gbc.insets = new Insets(0, 10, 5, 10);
        gbc.gridx = 1;
        gbc.gridy = 4;
        add(passCharCount, gbc);

        passwordField.getDocument().addDocumentListener(
                new CharCountUpdater(passwordField, passCharCount, MAX_CHARACTERS));
    }

    /**
     * Creates the login button.
     *
     * @param gbc the grid for the button.
     */
    private void addLoginButton(GridBagConstraints gbc) {
        gbc.insets = new Insets(10, 10, 5, 10);
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 5;

        JButton loginButton = new JButton("Login");
        loginButton.setFont(AppFonts.BUTTON_FONT);
        loginButton.setBackground(AppColors.LOGIN_LOGIN_BUTTON);
        loginButton.setForeground(AppColors.FONT);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        loginButton.setPreferredSize(new Dimension(150, 40));
        loginButton.addActionListener(e -> verifyLogin());
        add(loginButton, gbc);
    }

    /**
     * Creates the sign-up button.
     *
     * @param gbc the grid for the components.
     */
    private void addSignUpButton(GridBagConstraints gbc) {
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 6;

        JButton signUpButton = new JButton("Registrarse");
        signUpButton.setFont(AppFonts.BUTTON_FONT);
        signUpButton.setBackground(AppColors.LOGIN_SIGNUP_BUTTON);
        signUpButton.setForeground(AppColors.FONT);
        signUpButton.setFocusPainted(false);
        signUpButton.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        signUpButton.setPreferredSize(new Dimension(150, 40));
        signUpButton.addActionListener(e -> {
            ((MainFrame) SwingUtilities.getWindowAncestor(LoginPanel.this))
                    .switchPanel("SignUp");
        });
        add(signUpButton, gbc);
    }

    /**
     * Creates the forgotten password button.
     *
     * @param gbc the grid for the components.
     */
    private void addForgottenPasswordButton(GridBagConstraints gbc) {
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 7;

        JButton forgottenPasswordButton = new JButton("Contraseña Olvidada");
        forgottenPasswordButton.setFont(AppFonts.BUTTON_FONT);
        forgottenPasswordButton.setBackground(AppColors.LOGIN_RECOVERY_BUTTON);
        forgottenPasswordButton.setForeground(AppColors.FONT);
        forgottenPasswordButton.setFocusPainted(false);
        forgottenPasswordButton.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        forgottenPasswordButton.setPreferredSize(new Dimension(150, 40));
        forgottenPasswordButton.addActionListener(e -> sendRecuperationEmail());
        add(forgottenPasswordButton, gbc);
    }

    /**
     * Creates the button to toggle password visibility.
     */
    private void createTogglePasswordButton() {
        togglePasswordButton = new JButton();
        togglePasswordButton.setPreferredSize(new Dimension(30, 30));
        togglePasswordButton.setFocusPainted(false);
        togglePasswordButton.addActionListener(e ->
            setPasswordVisibility(passwordField.getEchoChar() == '•'));
        setPasswordVisibility(false);
    }

    /**
     * Sets the components to hide or show the password depending on the desired visibility
     *
     * @param visibility true if wanted to hide the password, false otherwise
     */
    private void setPasswordVisibility(boolean visibility) {
        passwordField.setEchoChar(visibility ? (char) 0 : '•');
        togglePasswordButton.setIcon(ImageUtils.loadScaledIcon(
                visibility ? "visible.png" : "not-visible.png", 24, 24));
    }

    /**
     * Verifies the login information and switches to the home panel if successful.
     */
    private void verifyLogin() {
        if (Manager.getInstance().getDb().login(userField.getText(), new String(passwordField.getPassword())))
            ((MainFrame) SwingUtilities.getWindowAncestor(LoginPanel.this))
                    .switchPanel("Home");
        else {
            PopUp.error(this, "Login inválido",
                    "El usuario y/o contraseña no son válidos.");
            refresh();
        }
    }

    /**
     * Sends a recuperation email for the introduced user
     */
    private void sendRecuperationEmail() {
        String userName = userField.getText();

        if (userName.isEmpty())
            PopUp.error(this, "Nombre de usuario requerido",
                    "No puedo estar vacío su nombre de usuario.");
        else {
            if (Manager.getInstance().getDb().forgotPassword(userName))
                PopUp.info(this, "Constraseña olvidada",
                        "Enviado email de recuperación.");
            else
                refresh();
        }
    }

    @Override
    public void refresh() {
        userField.setText("");
        passwordField.setText("");
        setPasswordVisibility(false);
    }
}