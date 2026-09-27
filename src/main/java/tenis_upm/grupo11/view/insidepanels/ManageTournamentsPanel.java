package tenis_upm.grupo11.view.insidepanels;

import tenis_upm.grupo11.data.Tournament;
import tenis_upm.grupo11.functionality.Manager;
import tenis_upm.grupo11.view.interfaces.IRefreshable;
import tenis_upm.grupo11.view.outsidepanels.HomePanel;
import tenis_upm.grupo11.view.utils.AppColors;
import tenis_upm.grupo11.view.utils.AppFonts;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * The panel responsible for managing tournaments.
 * It allows the user to select, view details, and start tournaments.
 */
public class ManageTournamentsPanel extends JPanel implements IRefreshable {
    private static ManageTournamentsPanel instance;
    private HomePanel parent;
    private JComboBox<Tournament> tournamentDropdown;
    private JTextField nameField;
    private JTextField deadlineField;
    private JButton startButton;
    private JButton matchButton;
    private JButton resultsButton;
    private JButton advanceRoundButton;

    /**
     * Private constructor to create the panel and initialize components.
     */
    private ManageTournamentsPanel() {
        setBackground(AppColors.BACKGROUND);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addTitle(gbc);
        addMainPanel(gbc);
        addButtonsPanel(gbc);
    }

    /**
     * Returns the singleton instance of the ManageTournamentsPanel.
     *
     * @return the instance of ManageTournamentsPanel
     */
    public static ManageTournamentsPanel getInstance() {
        if (instance == null) instance = new ManageTournamentsPanel();
        return instance;
    }

    /**
     * Adds the title label to the panel.
     *
     * @param gbc the grid bag constraints for layout
     */
    private void addTitle(GridBagConstraints gbc) {
        JLabel titleLabel = new JLabel("Gestionar Torneos", SwingConstants.CENTER);
        titleLabel.setFont(AppFonts.TITLE_FONT);
        titleLabel.setForeground(AppColors.FONT);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 0, 10, 0);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        add(titleLabel, gbc);
    }

    /**
     * Adds the main panel that contains the dropdown, details fields, and start button.
     *
     * @param gbc the grid bag constraints for layout
     */
    private void addMainPanel(GridBagConstraints gbc) {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(AppColors.PANEL_BACKGROUND);

        GridBagConstraints panelGbc = new GridBagConstraints();
        panelGbc.insets = new Insets(20, 20, 5, 20);
        panelGbc.fill = GridBagConstraints.HORIZONTAL;
        panelGbc.weightx = 1;

        addTournamentDropdown(mainPanel, panelGbc);
        addDetailsFields(mainPanel, panelGbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(30, 0, 20, 0);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        add(mainPanel, gbc);
    }

    /**
     * Adds the tournament dropdown to the main panel.
     *
     * @param mainPanel the main panel to which the dropdown will be added
     * @param gbc       the grid bag constraints for layout
     */
    private void addTournamentDropdown(JPanel mainPanel, GridBagConstraints gbc) {
        JLabel dropdownLabel = new JLabel("Seleccionar Torneo:");
        dropdownLabel.setFont(AppFonts.LABEL_TEXTFIELD_FONT);
        dropdownLabel.setForeground(AppColors.LABEL_TEXTFIELD);
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(dropdownLabel, gbc);

        tournamentDropdown = new JComboBox<>();
        tournamentDropdown.setFont(AppFonts.TEXTFIELD_FONT);
        tournamentDropdown.setBackground(AppColors.COMPONENT_BACKGROUND);
        tournamentDropdown.setPreferredSize(new Dimension(230, 30));
        tournamentDropdown.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
               
                if (value instanceof Tournament tournament) {
                    label.setText(tournament.getFullName());
                    System.out.println(tournament.getFullName());
                }
                return label;
            }
        });
        tournamentDropdown.addActionListener(e -> fillTournamentDetails());

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        mainPanel.add(tournamentDropdown, gbc);
    }

    /**
     * Adds the name and deadline fields to the main panel.
     *
     * @param mainPanel the main panel to which the fields will be added
     * @param gbc       the grid bag constraints for layout
     */
    private void addDetailsFields(JPanel mainPanel, GridBagConstraints gbc) {
        JLabel nameLabel = new JLabel("Nombre de Torneo:");
        nameLabel.setFont(AppFonts.LABEL_TEXTFIELD_FONT);
        nameLabel.setForeground(AppColors.LABEL_TEXTFIELD);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 20, 5, 20);
        mainPanel.add(nameLabel, gbc);

        nameField = new JTextField();
        nameField.setFont(AppFonts.TEXTFIELD_FONT);
        nameField.setEditable(false);
        nameField.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        gbc.weightx = 1;
        mainPanel.add(nameField, gbc);

        JLabel deadlineLabel = new JLabel("Fecha Límite:");
        deadlineLabel.setFont(AppFonts.LABEL_TEXTFIELD_FONT);
        deadlineLabel.setForeground(AppColors.LABEL_TEXTFIELD);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.insets = new Insets(10, 20, 20, 20);
        mainPanel.add(deadlineLabel, gbc);

        deadlineField = new JTextField();
        deadlineField.setFont(AppFonts.TEXTFIELD_FONT);
        deadlineField.setEditable(false);
        deadlineField.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        gbc.weightx = 1;
        mainPanel.add(deadlineField, gbc);
    }

    /**
     * Adds the buttons section to the bottom.
     *
     * @param gbc the grid bag constraints for layout
     */
    private void addButtonsPanel(GridBagConstraints gbc) {
        JPanel buttonsPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        buttonsPanel.setOpaque(false);

        startButton = new JButton("Iniciar");
        startButton.setFont(AppFonts.BUTTON_FONT);
        startButton.setBackground(AppColors.CREATE_TOURNAMENT_BUTTON);
        startButton.setForeground(AppColors.FONT);
        startButton.setBorder(null);
        startButton.setEnabled(false);
        startButton.setPreferredSize(new Dimension(100, 30));
        startButton.addActionListener(e -> {
            Tournament selectedTournament = (Tournament) tournamentDropdown.getSelectedItem();
            if (selectedTournament != null) {
                Manager.getInstance().getDb().initiateTournament(selectedTournament.getId());
                JOptionPane.showMessageDialog(this, "Iniciando el torneo seleccionado.");
            }
            matchButton.setEnabled(true);
            startButton.setEnabled(false);
        });
        buttonsPanel.add(startButton);

        matchButton = new JButton("Hacer Emparejamientos");
        matchButton.setFont(AppFonts.BUTTON_FONT);
        matchButton.setBackground(AppColors.MANAGE_TOURNAMENT_MATCHMAKING_BUTTON);
        matchButton.setForeground(AppColors.FONT);
        matchButton.setBorder(null);
        matchButton.setEnabled(false);
        matchButton.setPreferredSize(new Dimension(200, 30));
        matchButton.addActionListener(e -> {
            Tournament selectedTournament = (Tournament) tournamentDropdown.getSelectedItem();
            if (selectedTournament != null) {
            	resultsButton.setEnabled(true);
            	matchButton.setEnabled(false);
                Manager.getInstance().getDb().generateMatches(selectedTournament.getId());
                JOptionPane.showMessageDialog(this, "Emparejamientos generados.");
            }
        });
        buttonsPanel.add(matchButton);

        resultsButton = new JButton("Añadir Resultados");
        resultsButton.setFont(AppFonts.BUTTON_FONT);
        resultsButton.setBackground(AppColors.MANAGE_TOURNAMENT_RESULTS_BUTTON);
        resultsButton.setForeground(AppColors.FONT);
        resultsButton.setBorder(null);
        resultsButton.setPreferredSize(new Dimension(200, 30));
        resultsButton.addActionListener(e -> {
            Tournament selectedTournament = (Tournament) tournamentDropdown.getSelectedItem();
            if (selectedTournament != null) {
            System.out.println("Selected tournament is null");
            Manager.getInstance().getDb().getTournamentMatches(selectedTournament.getId());
            parent.switchPanel("Match Results");
            }
        	
        });

        buttonsPanel.add(resultsButton);

        advanceRoundButton = new JButton("Avanzar Ronda");
        advanceRoundButton.setFont(AppFonts.BUTTON_FONT);
        advanceRoundButton.setBackground(AppColors.MANAGE_TOURNAMENT_RESULTS_BUTTON);
        advanceRoundButton.setForeground(AppColors.FONT);
        advanceRoundButton.setBorder(null);
        advanceRoundButton.setPreferredSize(new Dimension(200, 30));
        advanceRoundButton.addActionListener(e -> {
            Tournament selectedTournament = (Tournament) tournamentDropdown.getSelectedItem();
            if (selectedTournament != null) {
            	Manager.getInstance().getDb().nextRound(selectedTournament.getId());
            	if(selectedTournament.getActualRound()!=0 ||  selectedTournament.getActualRound()!=1) {
            		matchButton.setEnabled(true);
            	}
            	advanceRoundButton.setEnabled(false);
                //TODO: Add call to advance to next round (done?)
                JOptionPane.showMessageDialog(this, "Avanzando a la siguiente ronda.");
            }
        });
        buttonsPanel.add(advanceRoundButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(10, 120, 5, 120);
        gbc.anchor = GridBagConstraints.CENTER;
        add(buttonsPanel, gbc);
    }

    /**
     * Fills the tournament details (name and deadline) based on the selected tournament.
     */
    private void fillTournamentDetails() {
        Tournament selectedTournament = (Tournament) tournamentDropdown.getSelectedItem();
        if (selectedTournament != null) {
            nameField.setText(selectedTournament.getName());

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            deadlineField.setText(dateFormat.format(selectedTournament.getDeadline()));
 
            startButton.setEnabled(!selectedTournament.isStarted() && selectedTournament.getActualRound()!=1);
            matchButton.setEnabled(selectedTournament.isStarted() &&
                  Manager.getInstance().getDb().canMatchmaking(selectedTournament.getId()) 
                  && selectedTournament.getActualRound()!=1);
            resultsButton.setEnabled(selectedTournament.isStarted() &&
                    !Manager.getInstance().getDb().canAdvanceRound(selectedTournament.getId()) 
                    && !Manager.getInstance().getDb().canMatchmaking(selectedTournament.getId())
                    && selectedTournament.getActualRound()!=1);
            advanceRoundButton.setEnabled(selectedTournament.isStarted() 
            		&& Manager.getInstance().getDb().canAdvanceRound(selectedTournament.getId())
            		&& selectedTournament.getActualRound()!=1); 
        } else
            refresh();
    }

    /**
     * Adds a list of tournaments to the tournament dropdown.
     *
     * @param tournaments the list of tournaments to add
     */
    public void addTournaments(List<Tournament> tournaments) {
        tournamentDropdown.removeAllItems();
        for (Tournament tournament : tournaments) {
            tournamentDropdown.addItem(tournament);
        }
        refresh();
    }

    public void setParent(HomePanel parent) {
        this.parent = parent;
    }

    /**
     * Refreshes the panel by resetting all fields and the dropdown.
     */
    @Override
    public void refresh() {
        tournamentDropdown.setSelectedIndex(-1);
        nameField.setText("");
        deadlineField.setText("");
        startButton.setEnabled(false);
        matchButton.setEnabled(false);
        resultsButton.setEnabled(false);
        advanceRoundButton.setEnabled(false);
    }
}
