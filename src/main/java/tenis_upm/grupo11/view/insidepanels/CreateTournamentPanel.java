package tenis_upm.grupo11.view.insidepanels;

import tenis_upm.grupo11.data.TournamentType;
import tenis_upm.grupo11.functionality.Manager;
import tenis_upm.grupo11.view.interfaces.IRefreshable;
import tenis_upm.grupo11.view.utils.*;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;
import java.sql.Date;

/**
 * Panel for creating a tournament in the application.
 */
public class CreateTournamentPanel extends JPanel implements IRefreshable {
    private static CreateTournamentPanel instance;
    private JComboBox<String> nameDropdown;
    private JSpinner deadlineSpinner;
    private JSpinner yearSpinner;
    private JButton createButton;

    /**
     * Private constructor for the CreateTournamentPanel class.
     * Initializes the layout and adds the various components to the panel.
     */
    private CreateTournamentPanel() {
        setBackground(AppColors.BACKGROUND);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        addTitle(gbc);
        addFormPanel(gbc);
        addButtonsPanel(gbc);
    }

    /**
     * Retrieves the singleton instance of CreateTournamentPanel.
     *
     * @return the singleton instance of CreateTournamentPanel.
     */
    public static CreateTournamentPanel getInstance() {
        if (instance == null) instance = new CreateTournamentPanel();
        return instance;
    }

    /**
     * Adds the title label to the panel.
     *
     * @param gbc GridBagConstraints to manage layout.
     */
    private void addTitle(GridBagConstraints gbc) {
        gbc.insets = new Insets(20, 10, 20, 10);
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel titleLabel = new JLabel("Crear un Torneo", SwingConstants.CENTER);
        titleLabel.setFont(AppFonts.TITLE_FONT);
        titleLabel.setForeground(AppColors.FONT);
        add(titleLabel, gbc);
    }

    /**
     * Adds the form panel containing input fields for the tournament details.
     *
     * @param gbc GridBagConstraints to manage layout.
     */
    private void addFormPanel(GridBagConstraints gbc) {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(AppColors.PANEL_BACKGROUND);

        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.fill = GridBagConstraints.HORIZONTAL;
        formGbc.insets = new Insets(10, 20, 10, 20);

        JLabel nameLabel = new JLabel("Nombre:");
        nameLabel.setFont(AppFonts.LABEL_TEXTFIELD_FONT);
        nameLabel.setForeground(AppColors.LABEL_TEXTFIELD);
        formGbc.gridx = 0;
        formGbc.gridy = 0;
        formPanel.add(nameLabel, formGbc);

        nameDropdown = new JComboBox<>();
        for (TournamentType type : TournamentType.values()) {
            nameDropdown.addItem(type.toFormattedString());
        }
        nameDropdown.setFont(AppFonts.TEXTFIELD_FONT);
        nameDropdown.setBackground(AppColors.COMPONENT_BACKGROUND);
        nameDropdown.setForeground(Color.BLACK);
        nameDropdown.setPreferredSize(new Dimension(200, 30));
        formGbc.gridx = 1;
        formPanel.add(nameDropdown, formGbc);

        JLabel deadlineLabel = new JLabel("Fecha Inscripción Límite:");
        deadlineLabel.setFont(AppFonts.LABEL_TEXTFIELD_FONT);
        deadlineLabel.setForeground(AppColors.LABEL_TEXTFIELD);
        formGbc.gridx = 0;
        formGbc.gridy = 1;
        formPanel.add(deadlineLabel, formGbc);

        deadlineSpinner = new JSpinner(new SpinnerDateModel(new Date(System.currentTimeMillis()), null, null, Calendar.DAY_OF_MONTH));
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(deadlineSpinner, "dd/MM/yyyy");
        deadlineSpinner.setEditor(dateEditor);
        deadlineSpinner.setFont(AppFonts.TEXTFIELD_FONT);
        deadlineSpinner.setPreferredSize(new Dimension(150, 30));
        formGbc.gridx = 1;
        formPanel.add(deadlineSpinner, formGbc);

        JLabel yearLabel = new JLabel("Año:");
        yearLabel.setFont(AppFonts.LABEL_TEXTFIELD_FONT);
        yearLabel.setForeground(AppColors.LABEL_TEXTFIELD);
        formGbc.gridx = 0;
        formGbc.gridy = 2;
        formPanel.add(yearLabel, formGbc);

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        yearSpinner = new JSpinner(new SpinnerNumberModel(currentYear, currentYear, 2100, 1));
        yearSpinner.setFont(AppFonts.TEXTFIELD_FONT);
        yearSpinner.setPreferredSize(new Dimension(100, 30));
        formGbc.gridx = 1;
        formPanel.add(yearSpinner, formGbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        add(formPanel, gbc);
    }

    /**
     * Adds the panel containing the back and create buttons.
     *
     * @param gbc GridBagConstraints to manage layout.
     */
    private void addButtonsPanel(GridBagConstraints gbc) {
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setOpaque(false);
        buttonsPanel.setLayout(new GridBagLayout());

        GridBagConstraints buttonGbc = new GridBagConstraints();
        buttonGbc.fill = GridBagConstraints.HORIZONTAL;
        buttonGbc.anchor = GridBagConstraints.CENTER;

        buttonGbc.insets = new Insets(10, 10, 10, 10);
        addCreateButton(buttonsPanel, buttonGbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        add(buttonsPanel, gbc);
    }

    /**
     * Adds the create button to the buttons panel.
     *
     * @param buttonsPanel the panel to add the create button to.
     * @param buttonGbc    GridBagConstraints to manage layout.
     */
    private void addCreateButton(JPanel buttonsPanel, GridBagConstraints buttonGbc) {
        createButton = new JButton("Crear Torneo");
        createButton.setFont(AppFonts.BUTTON_FONT);
        createButton.setBackground(AppColors.CREATE_TOURNAMENT_BUTTON);
        createButton.setForeground(AppColors.FONT);
        createButton.setFocusPainted(false);
        createButton.setBorderPainted(false);
        createButton.setPreferredSize(new Dimension(250, 40));
        createButton.addActionListener(e -> {
            createTournamentAction();
        });

        buttonGbc.gridx = 1;
        buttonGbc.gridy = 0;
        buttonGbc.gridwidth = 1;
        buttonsPanel.add(createButton, buttonGbc);
    }

    /**
     * Handles the action of creating a tournament when the create button is clicked.
     * It validates the input fields and creates a new tournament if the data is valid.
     */
    private void createTournamentAction() {
        String selectedName = (String) nameDropdown.getSelectedItem();
        java.util.Date utilDate = (java.util.Date) deadlineSpinner.getValue();
        java.sql.Date deadline = new java.sql.Date(utilDate.getTime());
        int year = (Integer) yearSpinner.getValue();

        boolean valid = true;
        StringBuilder errorMessage = new StringBuilder("Campos inválidos:\n");

        if (selectedName == null || selectedName.isEmpty()) {
            valid = false;
            errorMessage.append("- Nombre está vacío.\n");
        }
        if (year < Calendar.getInstance().get(Calendar.YEAR)) {
            valid = false;
            errorMessage.append("- No puede ser un año anterior.\n");
        }
        if (deadline.before(new Date(System.currentTimeMillis()))) {
            valid = false;
            errorMessage.append("- La fecha límite tiene que ser un día futuro.\n");
        }

        if (!valid) {
            JOptionPane.showMessageDialog(
                    this, errorMessage.toString(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean creado = Manager.getInstance().getDb().createTournament(selectedName, year, deadline);

        if(creado) JOptionPane.showMessageDialog(
                this, "Torneo creado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        else JOptionPane.showMessageDialog(
                this, "Este torneo ya existe.", "Error", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Refreshes the form fields to their default values.
     */
    @Override
    public void refresh() {
        nameDropdown.setSelectedIndex(0);
        deadlineSpinner.setValue(new Date(System.currentTimeMillis()));
        yearSpinner.setValue(Calendar.getInstance().get(Calendar.YEAR));
    }
}
