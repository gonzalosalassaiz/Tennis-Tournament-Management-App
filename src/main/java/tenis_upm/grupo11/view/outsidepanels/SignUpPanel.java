package tenis_upm.grupo11.view.outsidepanels;

import tenis_upm.grupo11.functionality.Manager;
import tenis_upm.grupo11.view.MainFrame;
import tenis_upm.grupo11.view.interfaces.IRefreshable;
import tenis_upm.grupo11.view.utils.*;

import javax.swing.*;
import java.awt.*;

/**
 * This class represents the Sign-Up panel where users can register by providing necessary details.
 * It includes fields for the username, password, email, and phone number, and provides validation
 * for input before allowing registration.
 */
public class SignUpPanel extends JPanel implements IRefreshable {
    private static SignUpPanel instance;
    private static final int MAX_CHARACTERS = 16;
    private JTextField fullNameField;
    private JTextField userField;
    private JTextField passwordField;
    private JTextField confirmPasswordField;
    private JTextField emailField;
    private JTextField phoneField;
    private JCheckBox adminCheckBox;

    /**
     * Private constructor to initialize the SignUpPanel.
     * Sets up the layout and components for the sign-up form.
     */
    private SignUpPanel() {
        setBackground(AppColors.BACKGROUND);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        // Creates and adds the component groups to the panel
        addTitle(gbc);
        addFullNameField(gbc);
        addUsernameField(gbc);
        addPasswordField(gbc);
        addConfirmPasswordField(gbc);
        addEmailField(gbc);
        addPhoneField(gbc);
        addAdminModeField(gbc);
        addButtons(gbc);
    }

    /**
     * Returns the singleton instance of the SignUpPanel.
     *
     * @return the SignUpPanel instance
     */
    public static SignUpPanel getInstance() {
        if (instance == null) instance = new SignUpPanel();
        return instance;
    }

    /**
     * Adds the title label to the panel.
     *
     * @param gbc the GridBagConstraints used for positioning
     */
    private void addTitle(GridBagConstraints gbc) {
        gbc.insets = new Insets(5, 10, 20, 10);
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel titleLabel = new JLabel("Registro", SwingConstants.CENTER);
        titleLabel.setFont(AppFonts.TITLE_FONT);
        titleLabel.setForeground(AppColors.FONT);
        add(titleLabel, gbc);
    }

    /**
     * Adds the username input field and associated character count to the panel.
     *
     * @param gbc the GridBagConstraints used for positioning
     */
    private void addFullNameField(GridBagConstraints gbc) {
        gbc.insets = new Insets(10, 10, 5, 10);

        JLabel usernameLabel = new JLabel("Nombre Completo:");
        usernameLabel.setFont(AppFonts.LABEL_TEXTFIELD_FONT);
        usernameLabel.setForeground(AppColors.LABEL_TEXTFIELD);
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(usernameLabel, gbc);

        fullNameField = createTextField();
        gbc.gridx = 1;
        add(fullNameField, gbc);
    }

    private void addUsernameField(GridBagConstraints gbc) {
        gbc.insets = new Insets(10, 10, 5, 10);

        JLabel usernameLabel = new JLabel("Nombre de Usuario:");
        usernameLabel.setFont(AppFonts.LABEL_TEXTFIELD_FONT);
        usernameLabel.setForeground(AppColors.LABEL_TEXTFIELD);
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(usernameLabel, gbc);

        userField = createTextFieldWithLimit();
        gbc.gridx = 1;
        add(userField, gbc);

        JLabel userCharCount = new JLabel("0/" + MAX_CHARACTERS);
        userCharCount.setFont(AppFonts.CHAR_COUNT_FONT);
        userCharCount.setForeground(AppColors.CHAR_COUNT_TEXT);
        gbc.insets = new Insets(0, 10, 5, 10);
        gbc.gridx = 1;
        gbc.gridy = 4;
        add(userCharCount, gbc);
        userField.getDocument().addDocumentListener(
                new CharCountUpdater(userField, userCharCount, MAX_CHARACTERS));
    }

    /**
     * Adds the password input field and associated character count to the panel.
     *
     * @param gbc the GridBagConstraints used for positioning
     */
    private void addPasswordField(GridBagConstraints gbc) {
        gbc.insets = new Insets(10, 10, 5, 10);

        JLabel passwordLabel = new JLabel("Contraseña:");
        passwordLabel.setFont(AppFonts.LABEL_TEXTFIELD_FONT);
        passwordLabel.setForeground(AppColors.LABEL_TEXTFIELD);
        gbc.gridx = 0;
        gbc.gridy = 5;
        add(passwordLabel, gbc);

        passwordField = createTextFieldWithLimit();
        gbc.gridx = 1;
        add(passwordField, gbc);

        JLabel passCharCount = new JLabel("0/" + MAX_CHARACTERS);
        passCharCount.setFont(AppFonts.CHAR_COUNT_FONT);
        passCharCount.setForeground(AppColors.CHAR_COUNT_TEXT);
        gbc.insets = new Insets(0, 10, 5, 10);
        gbc.gridx = 1;
        gbc.gridy = 6;
        add(passCharCount, gbc);
        passwordField.getDocument().addDocumentListener(
                new CharCountUpdater(passwordField, passCharCount, MAX_CHARACTERS));
    }

    /**
     * Adds the confirm password input field and associated character count to the panel.
     *
     * @param gbc the GridBagConstraints used for positioning
     */
    private void addConfirmPasswordField(GridBagConstraints gbc) {
        gbc.insets = new Insets(10, 10, 5, 10);

        JLabel confirmPasswordLabel = new JLabel("Repite Contraseña:");
        confirmPasswordLabel.setFont(AppFonts.LABEL_TEXTFIELD_FONT);
        confirmPasswordLabel.setForeground(AppColors.LABEL_TEXTFIELD);
        gbc.gridx = 0;
        gbc.gridy = 7;
        add(confirmPasswordLabel, gbc);

        confirmPasswordField = createTextFieldWithLimit();
        gbc.gridx = 1;
        add(confirmPasswordField, gbc);

        JLabel confirmPassCharCount = new JLabel("0/" + MAX_CHARACTERS);
        confirmPassCharCount.setFont(AppFonts.CHAR_COUNT_FONT);
        confirmPassCharCount.setForeground(AppColors.CHAR_COUNT_TEXT);
        gbc.insets = new Insets(0, 10, 5, 10);
        gbc.gridx = 1;
        gbc.gridy = 8;
        add(confirmPassCharCount, gbc);
        confirmPasswordField.getDocument().addDocumentListener(
                new CharCountUpdater(confirmPasswordField, confirmPassCharCount, MAX_CHARACTERS));
    }

    /**
     * Adds the email input field to the panel.
     *
     * @param gbc the GridBagConstraints used for positioning
     */
    private void addEmailField(GridBagConstraints gbc) {
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(AppFonts.LABEL_TEXTFIELD_FONT);
        emailLabel.setForeground(AppColors.LABEL_TEXTFIELD);
        gbc.gridx = 0;
        gbc.gridy = 9;
        add(emailLabel, gbc);

        emailField = createTextField();
        gbc.gridx = 1;
        add(emailField, gbc);
    }

    /**
     * Adds the phone number input field to the panel.
     *
     * @param gbc the GridBagConstraints used for positioning
     */
    private void addPhoneField(GridBagConstraints gbc) {
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel phoneLabel = new JLabel("Teléfono:");
        phoneLabel.setFont(AppFonts.LABEL_TEXTFIELD_FONT);
        phoneLabel.setForeground(AppColors.LABEL_TEXTFIELD);
        gbc.gridx = 0;
        gbc.gridy = 10;
        add(phoneLabel, gbc);

        phoneField = createTextField();
        gbc.gridx = 1;
        add(phoneField, gbc);
    }

    /**
     * Adds the admin mode input field (checkbox) to the panel.
     *
     * @param gbc the GridBagConstraints used for positioning
     */
    private void addAdminModeField(GridBagConstraints gbc) {
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel adminModeLabel = new JLabel("Modo Admin:");
        adminModeLabel.setFont(AppFonts.LABEL_TEXTFIELD_FONT);
        adminModeLabel.setForeground(AppColors.LABEL_TEXTFIELD);
        gbc.gridx = 0;
        gbc.gridy = 11;
        add(adminModeLabel, gbc);

        adminCheckBox = new JCheckBox();
        adminCheckBox.setBackground(AppColors.BACKGROUND);
        gbc.gridx = 1;
        add(adminCheckBox, gbc);
    }

    /**
     * Adds the buttons (Back and Confirm Registration) to the panel.
     *
     * @param gbc the GridBagConstraints used for positioning
     */
    private void addButtons(GridBagConstraints gbc) {
        gbc.insets = new Insets(20, 10, 10, 10);
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 12;

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(AppColors.BACKGROUND);

        //Creates and adds the backup button
        JButton backButton = new JButton();
        backButton.setFont(AppFonts.BUTTON_FONT);
        backButton.setBackground(AppColors.BACK_BUTTON);
        backButton.setForeground(AppColors.FONT);
        backButton.setFocusPainted(false);
        backButton.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        backButton.setPreferredSize(new Dimension(80, 40));
        backButton.setIcon(ImageUtils.loadScaledIcon("back.png", 50, 30));
        backButton.addActionListener(e ->
                ((MainFrame) SwingUtilities.getWindowAncestor(SignUpPanel.this))
                        .switchPanel("Login"));

        //Creates and adds the confirm registration button
        JButton confirmRegistrationButton = new JButton("Registrarse");
        confirmRegistrationButton.setFont(AppFonts.BUTTON_FONT);
        confirmRegistrationButton.setBackground(AppColors.SIGNUP_CONFIRM_BUTTON);
        confirmRegistrationButton.setForeground(AppColors.FONT);
        confirmRegistrationButton.setFocusPainted(false);
        confirmRegistrationButton.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        confirmRegistrationButton.setPreferredSize(new Dimension(400, 40));
        confirmRegistrationButton.addActionListener(e -> verifySignUp());

        buttonPanel.add(backButton);
        buttonPanel.add(confirmRegistrationButton);
        add(buttonPanel, gbc);
    }

    /**
     * Creates and returns a custom text field.
     *
     * @return the customized text field
     */
    private JTextField createTextField() {
        JTextField textField = new JTextField();
        textField.setFont(AppFonts.TEXTFIELD_FONT);
        textField.setForeground(AppColors.FONT);
        textField.setBackground(AppColors.TEXTFIELD);
        textField.setPreferredSize(new Dimension(150, 30));
        textField.setBorder(null);
        return textField;
    }

    /**
     * Creates and returns a custom text field with character limit.
     *
     * @return the customized character-limited text field.
     */
    private JTextField createTextFieldWithLimit() {
        JTextField textField = createTextField();
        textField.setDocument(new LimitedDocument(MAX_CHARACTERS));
        return textField;
    }

    /**
     * Verifies the user input for registration and shows an error message if any fields are invalid.
     * If all fields are valid, attempts to create the user and switches to a confirmation screen.
     */
    private void verifySignUp() {
        String fullName = fullNameField.getText();
        String userName = userField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();

        if (fullName.isEmpty() || userName.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ||
                email.isEmpty() || phone.isEmpty()) {
            PopUp.error(this, "Campo/s inválidos",
                    "Todos los campos han de estar rellenados.");
            return;
        } else if (!password.equals(confirmPassword)) {
            PopUp.error(this, "Campo/s inválidos",
                    "Las contraseñas no coinciden.");
            return;
        }

        int success = Manager.getInstance().getDb().createUser(
                userName,
                fullName,
                phone,
                email,
                password,
                adminCheckBox.isSelected()
        );

        if (success == 0) {
            ((MainFrame) SwingUtilities.getWindowAncestor(SignUpPanel.this))
                    .switchPanel("CheckConfirmation");
        } else {
            String title = switch (success) {
                case 1, 2 -> "Campo/s inválidos";
                default -> "Error inesperado";
            };

            String body = switch (success) {
                case 1 -> "Correo electrónico con formato erróneo. Intente de nuevo.";
                case 2 -> "El nombre de usuario ya está en uso. Intente de nuevo.";
                case 3, 6 -> "Error al consultar la base de datos. Intente de nuevo.";
                case 4 -> "El correo no ha sido verificado. Intente de nuevo.";
                case 5 -> "Error al crear el usuario. Intente de nuevo.";
                default -> "El registro ha fallado. Intente de nuevo.";
            };

            PopUp.error(this, title, body);
        }
    }

    @Override
    public void refresh() {
        fullNameField.setText("");
        userField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        emailField.setText("");
        phoneField.setText("");
        adminCheckBox.setSelected(false);
    }
}
