package tenis_upm.grupo11.view.insidepanels;

import tenis_upm.grupo11.data.User;
import tenis_upm.grupo11.functionality.Manager;
import tenis_upm.grupo11.view.interfaces.IRefreshable;
import tenis_upm.grupo11.view.utils.*;

import javax.swing.*;
import java.awt.*;

/**
 * This class represents the Profile panel where users can view or edit their profile details.
 * It includes fields for the username, full name, password, email, and phone number.
 */
public class ProfilePanel extends JPanel implements IRefreshable {
    private static ProfilePanel instance;
    private static final int MAX_CHARACTERS = 16;
    private JTextField fullNameField;
    private JTextField userField;
    private JPasswordField passwordField;
    private JTextField emailField;
    private JTextField phoneField;
    private JButton togglePasswordButton;

    /**
     * Private constructor to initialize the ProfilePanel.
     * Sets up the layout and components for the profile form.
     */
    private ProfilePanel() {
        setBackground(AppColors.BACKGROUND);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        // Creates and adds the component groups to the panel
        addTitle(gbc);
        addFields(gbc);
        addButton(gbc);
    }

    /**
     * Returns the singleton instance of the ProfilePanel.
     *
     * @return the ProfilePanel instance
     */
    public static ProfilePanel getInstance() {
        if (instance == null) instance = new ProfilePanel();
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

        JLabel titleLabel = new JLabel("Perfil de Usuario", SwingConstants.CENTER);
        titleLabel.setFont(AppFonts.TITLE_FONT);
        titleLabel.setForeground(AppColors.FONT);
        add(titleLabel, gbc);
    }

    /**
     * Adds all the input fields to the panel in a 2xN format.
     *
     * @param gbc the GridBagConstraints used for positioning
     */
    private void addFields(GridBagConstraints gbc) {
        gbc.insets = new Insets(10, 10, 5, 10);
        gbc.gridwidth = 1;

        fullNameField = createTextField();
        fullNameField.setEnabled(false);
        addField(gbc, "Nombre Completo:", 1, fullNameField);

        userField = createTextFieldWithLimit();
        addFieldWithCharCount(gbc, "Nombre de Usuario:", 2, userField);

        passwordField = createPasswordFieldWithLimit();
        addPasswordFieldWithToggle(gbc, "Contraseña:", 4, passwordField);

        emailField = createTextField();
        emailField.setEnabled(false);
        addField(gbc, "Email:", 6, emailField);

        phoneField = createTextField();
        addField(gbc, "Teléfono:", 7, phoneField);
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
     * Creates and returns a custom password field with character limit.
     *
     * @return the customized password field.
     */
    private JPasswordField createPasswordFieldWithLimit() {
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(AppFonts.TEXTFIELD_FONT);
        passwordField.setForeground(AppColors.FONT);
        passwordField.setBackground(AppColors.TEXTFIELD);
        passwordField.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        passwordField.setDocument(new LimitedDocument(MAX_CHARACTERS));
        passwordField.setEchoChar('*');
        return passwordField;
    }

    /**
     * Adds an individual field with label and input to the panel.
     *
     * @param gbc       the GridBagConstraints used for positioning
     * @param label     the text for the label
     * @param row       the row number for this field
     * @param textField the JTextField to add
     */
    private void addField(GridBagConstraints gbc, String label, int row, JTextField textField) {
        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setFont(AppFonts.LABEL_TEXTFIELD_FONT);
        fieldLabel.setForeground(AppColors.LABEL_TEXTFIELD);
        gbc.gridx = 0;
        gbc.gridy = row;
        add(fieldLabel, gbc);

        textField.setPreferredSize(new Dimension(200, 30));
        textField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        gbc.gridx = 1;
        add(textField, gbc);
    }

    /**
     * Adds a field with a character count label to the panel.
     *
     * @param gbc       the GridBagConstraints used for positioning
     * @param label     the text for the label
     * @param row       the row number for this field
     * @param textField the JTextField to add
     */
    private void addFieldWithCharCount(GridBagConstraints gbc, String label, int row, JTextField textField) {
        addField(gbc, label, row, textField);

        // Increment row for the character count label
        gbc.gridy += 1;
        gbc.gridx = 1;

        JLabel charCountLabel = new JLabel("0/" + MAX_CHARACTERS);
        charCountLabel.setFont(AppFonts.CHAR_COUNT_FONT);
        charCountLabel.setForeground(AppColors.CHAR_COUNT_TEXT);
        charCountLabel.setHorizontalAlignment(SwingConstants.LEFT);
        gbc.insets = new Insets(0, 10, 5, 10); // Adjust insets for proper spacing
        add(charCountLabel, gbc);

        textField.getDocument().addDocumentListener(
                new CharCountUpdater(textField, charCountLabel, MAX_CHARACTERS));
    }

    /**
     * Adds a password field with a toggle button for visibility.
     *
     * @param gbc           the GridBagConstraints used for positioning
     * @param label         the text for the label
     * @param row           the row number for this field
     * @param passwordField the JPasswordField to add
     */
    private void addPasswordFieldWithToggle(GridBagConstraints gbc, String label, int row, JPasswordField passwordField) {
        addFieldWithCharCount(gbc, label, row, passwordField);

        togglePasswordButton = new JButton();
        togglePasswordButton.setPreferredSize(new Dimension(30, 30));
        togglePasswordButton.setFocusPainted(false);
        togglePasswordButton.addActionListener(e ->
                setPasswordVisibility(passwordField.getEchoChar() == '•'));
        setPasswordVisibility(false);

        gbc.insets = new Insets(0, 10, 5, 10);
        gbc.gridx = 2;
        gbc.gridy = row;
        add(togglePasswordButton, gbc);
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
     * Adds the save button to the panel.
     *
     * @param gbc the GridBagConstraints used for positioning
     */
    private void addButton(GridBagConstraints gbc) {
        gbc.insets = new Insets(20, 10, 10, 10);
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 8;

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(AppColors.BACKGROUND);

        // Creates and adds the save button
        JButton saveButton = new JButton("Guardar");
        saveButton.setFont(AppFonts.BUTTON_FONT);
        saveButton.setBackground(AppColors.SIGNUP_CONFIRM_BUTTON);
        saveButton.setForeground(AppColors.FONT);
        saveButton.setFocusPainted(false);
        saveButton.setBorder(null);
        saveButton.setPreferredSize(new Dimension(150, 40));
        saveButton.addActionListener(e -> saveProfile());

        buttonPanel.add(saveButton);
        add(buttonPanel, gbc);
    }

    /**
     * Handles saving the profile information.
     */
    private void saveProfile() {
        String username = userField.getText();
        String password = new String(passwordField.getPassword());
        String phone = phoneField.getText();

        if (username.isEmpty() || password.isEmpty() || phone.isEmpty()) {
            PopUp.error(this, "Campo/s inválidos",
                    "Todos los campos han de estar rellenados.");
            return;
        }

        User user = User.clone(Manager.getInstance().getDb().getActualUser());
        user.setUsername(username);
        user.setPassword(password);
        user.setPhone(phone);

        int success = Manager.getInstance().getDb().modifyUser(user);

        if (success == 0) {
            PopUp.info(this, "Cambios existosos",
                    "Se han podido aplicar los cambios.");
            refresh();
        }
        else {
            String title = switch (success) {
                case 1 -> "Campo/s inválidos";
                default -> "Error inesperado";
            };

            String body = switch (success) {
                case 1 -> "El nombre de usuario ya está en uso. Intente de nuevo.";
                case 2 -> "Error al consultar la base de datos. Intente de nuevo.";
                default -> "El registro ha fallado. Intente de nuevo.";
            };
            PopUp.error(this, title, body);
        }
    }

    public void addUser(User user) {
        fullNameField.setText(user.getName());
        userField.setText(user.getUsername());
        passwordField.setText(user.getPassword());
        emailField.setText(user.getEmail());
        phoneField.setText(user.getPhone());
    }

    @Override
    public void refresh() {
        addUser(Manager.getInstance().getDb().getActualUser());
        setPasswordVisibility(false);
    }
}
