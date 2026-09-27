package tenis_upm.grupo11.view.insidepanels;

import tenis_upm.grupo11.data.Tournament;
import tenis_upm.grupo11.functionality.Manager;
import tenis_upm.grupo11.view.interfaces.IRefreshable;
import tenis_upm.grupo11.view.utils.AppColors;
import tenis_upm.grupo11.view.utils.AppFonts;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.sql.Date;
import java.util.List;

/**
 * Panel for registering into a tournament in the application.
 */
public class RegisterTournamentPanel extends JPanel implements IRefreshable {
    private static RegisterTournamentPanel instance;
    private JComboBox<Tournament> tournamentDropdown;
    private JButton registerButton;

    /**
     * Constructor for RegisterTournamentPanel
     */
    private RegisterTournamentPanel() {
        setBackground(AppColors.BACKGROUND);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;

        addSubPanel(gbc);
    }

    /**
     * Singleton proper function to get the only instance of this class
     *
     * @return the only instance of this class
     */
    public static RegisterTournamentPanel getInstance() {
        if (instance == null) instance = new RegisterTournamentPanel();
        return instance;
    }

    /**
     * Adds a sub-panel to the center of the main panel
     *
     * @param gbc the grid bag constraints for the layout
     */
    private void addSubPanel(GridBagConstraints gbc) {
        JPanel subPanel = new JPanel(new GridBagLayout());
        subPanel.setBackground(AppColors.PANEL_BACKGROUND);
        subPanel.setPreferredSize(new Dimension(300, 200));

        GridBagConstraints subGbc = new GridBagConstraints();
        subGbc.fill = GridBagConstraints.HORIZONTAL;
        subGbc.anchor = GridBagConstraints.CENTER;

        // Add components to the sub-panel
        addTitleLabel(subPanel, subGbc);
        addTournamentDropdown(subPanel, subGbc);
        addRegisterButton(subPanel, subGbc);

        // Add the sub-panel to the main panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(subPanel, gbc);
    }

    /**
     * Adds the title label to the sub-panel
     *
     * @param subPanel the sub-panel
     * @param subGbc   the grid bag constraints for the sub-panel
     */
    private void addTitleLabel(JPanel subPanel, GridBagConstraints subGbc) {
        JLabel titleLabel = new JLabel("Selecciona un torneo", SwingConstants.CENTER);
        titleLabel.setFont(AppFonts.SUBTITLE_FONT);
        titleLabel.setForeground(AppColors.FONT);

        subGbc.insets = new Insets(10, 10, 10, 10);
        subGbc.gridx = 0;
        subGbc.gridy = 0;
        subGbc.gridwidth = 2; // Center align
        subPanel.add(titleLabel, subGbc);
    }

    /**
     * Adds the tournament dropdown to the sub-panel
     *
     * @param subPanel the sub-panel
     * @param subGbc   the grid bag constraints for the sub-panel
     */
    private void addTournamentDropdown(JPanel subPanel, GridBagConstraints subGbc) {
        tournamentDropdown = new JComboBox<>();
        tournamentDropdown.setFont(AppFonts.TEXTFIELD_FONT);
        tournamentDropdown.setBackground(AppColors.COMPONENT_BACKGROUND);
        tournamentDropdown.setForeground(Color.BLACK);
        // Custom renderer to display name and year of the tournament
        tournamentDropdown.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Tournament tournament) {
                    label.setText(tournament.getFullName());
                }
                label.setHorizontalAlignment(SwingConstants.LEFT);
                label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
                return label;
            }
        });

        subGbc.insets = new Insets(10, 10, 10, 10);
        subGbc.gridx = 0;
        subGbc.gridy = 1;
        subGbc.gridwidth = 2;
        subPanel.add(tournamentDropdown, subGbc);

        tournamentDropdown.addActionListener(e -> checkTournamentDeadline());
    }

    /**
     * Adds the register button to the sub-panel
     *
     * @param subPanel the sub-panel
     * @param subGbc   the grid bag constraints for the sub-panel
     */
    private void addRegisterButton(JPanel subPanel, GridBagConstraints subGbc) {
        registerButton = new JButton("Inscribirse");
        registerButton.setFont(AppFonts.BUTTON_FONT);
        registerButton.setBackground(AppColors.REGISTER_TOURNAMENT_BUTTON);
        registerButton.setForeground(AppColors.FONT);
        registerButton.setFocusPainted(false);
        registerButton.setPreferredSize(new Dimension(100, 30));
        registerButton.setEnabled(false);
        registerButton.addActionListener(e -> {
            Tournament selectedTournament = (Tournament) tournamentDropdown.getSelectedItem();
            if (selectedTournament != null) {
                if (Manager.getInstance().getDb().createRegistration(selectedTournament.getId()))
                    JOptionPane.showMessageDialog(this, "Ya estás inscrito en este torneo.");
                else JOptionPane.showMessageDialog(this, "Te has inscrito en el torneo seleccionado.");
            }
        });

        subGbc.insets = new Insets(10, 10, 10, 10);
        subGbc.gridx = 1;
        subGbc.gridy = 2;
        subGbc.gridwidth = 1;
        subPanel.add(registerButton, subGbc);
    }

    /**
     * Adds a list of tournaments to the dropdown.
     *
     * @param tournamentList the list of tournaments to display
     */
    public void addTournaments(List<Tournament> tournamentList) {
    	 tournamentDropdown.removeAllItems();
        for (Tournament tournament : tournamentList)
            tournamentDropdown.addItem(tournament);
    }

    /**
     * Checks if the selected tournament's deadline has passed and disables the register button.
     */
    private void checkTournamentDeadline() {
        if (tournamentDropdown.getSelectedIndex() == -1)
            registerButton.setEnabled(false);

        Tournament selectedTournament = (Tournament) tournamentDropdown.getSelectedItem();
        if (selectedTournament != null) {
            Date deadline = selectedTournament.getDeadline();
            LocalDate deadlineLocalDate = deadline.toLocalDate();
            LocalDate currentDate = LocalDate.now();

            // If the tournament deadline has passed, disable the register button
            registerButton.setEnabled(!deadlineLocalDate.isBefore(currentDate) && selectedTournament.getActualRound()!=0);
        }
    }

    @Override
    public void refresh() {
        tournamentDropdown.setSelectedIndex(-1);
    }
}
