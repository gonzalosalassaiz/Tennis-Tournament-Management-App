package tenis_upm.grupo11.view.insidepanels;

import javafx.util.Pair;
import tenis_upm.grupo11.data.Match;
import tenis_upm.grupo11.functionality.Manager;
import tenis_upm.grupo11.view.interfaces.IRefreshable;
import tenis_upm.grupo11.view.outsidepanels.HomePanel;
import tenis_upm.grupo11.view.utils.AppColors;
import tenis_upm.grupo11.view.utils.AppFonts;
import tenis_upm.grupo11.view.utils.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * El panel para ingresar las puntuaciones de un partido de tenis a 5 sets.
 * Permite al usuario ingresar las puntuaciones y confirmar los resultados.
 */
public class ResultsMatchPanel extends JPanel implements IRefreshable {
    private static ResultsMatchPanel instance;
    private HomePanel parent;
    private JComboBox<Match> matchDropdown;
    private JLabel player1Label;
    private JLabel player2Label;
    private JPanel scorePanel;
    private JSpinner[] player1SetScores;
    private JSpinner[] player2SetScores;
    private JButton confirmButton;
    private JButton backButton;

    /**
     * Constructor que inicializa los componentes del panel.
     */
    private ResultsMatchPanel() {
        setBackground(AppColors.BACKGROUND);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 20, 10, 20);

        addTitle(gbc);
        System.out.println("add match dropdown");
        addMatchDropdown(gbc);
        addScoreFields(gbc);
        addButtonsPanel(gbc);
    }

    /**
     * Returns the singleton instance of the ResultsMatchPanel.
     *
     * @return the instance of ResultsMatchPanel
     */
    public static ResultsMatchPanel getInstance() {
        if (instance == null) instance = new ResultsMatchPanel();
        return instance;
    }

    /**
     * Añade el título al panel.
     *
     * @param gbc las restricciones para el layout
     */ 
    private void addTitle(GridBagConstraints gbc) {
        JLabel titleLabel = new JLabel("Resultados del Partido", SwingConstants.CENTER);
        titleLabel.setFont(AppFonts.TITLE_FONT);
        titleLabel.setForeground(AppColors.FONT);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(0, 10, 20, 10);
        add(titleLabel, gbc);
    }

    /**
     * Añade el combo box para seleccionar el partido.
     *
     * @param gbc las restricciones para el layout
     */
    private void addMatchDropdown(GridBagConstraints gbc) {
        JLabel dropdownLabel = new JLabel("Seleccionar Partido:", SwingConstants.CENTER);
        dropdownLabel.setFont(AppFonts.LABEL_TEXTFIELD_FONT);
        dropdownLabel.setForeground(AppColors.FONT);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        add(dropdownLabel, gbc);

        matchDropdown = new JComboBox<>();
        matchDropdown.setFont(AppFonts.TEXTFIELD_FONT);
        matchDropdown.setBackground(AppColors.COMPONENT_BACKGROUND);
        matchDropdown.setPreferredSize(new Dimension(200, 30));
        System.out.println("set renderer");
        matchDropdown.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                System.out.println("Renderer called with value: " + value + ", index: " + index);
                if (value instanceof Match match) {
                    label.setText(match.getFirstPlayerUsername() + " vs " + match.getSecondPlayerUsername());
                    System.out.println(match.getFirstPlayerUsername() + " vs " + match.getSecondPlayerUsername());
                }
                return label;
            }
        });
        matchDropdown.addActionListener(e -> {
            Match match = (Match) matchDropdown.getSelectedItem();

            confirmButton.setEnabled(match != null);
            if(match != null) {
                player1Label.setText(match.getFirstPlayerUsername() + ":");
                player2Label.setText(match.getSecondPlayerUsername() + ":");
            }
            else {
                player1Label.setText("Jugador 1:");
                player2Label.setText("Jugador 2:");
            }
            revalidate();
            repaint();
        });

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        add(matchDropdown, gbc);
    }

    /**
     * Añade los campos para ingresar las puntuaciones de cada set.
     *
     * @param gbc las restricciones para el layout
     */
    private void addScoreFields(GridBagConstraints gbc) {
        JPanel scoreFieldsContainer = new JPanel();
        scoreFieldsContainer.setLayout(new GridBagLayout());
        scoreFieldsContainer.setBackground(AppColors.PANEL_BACKGROUND);
        scoreFieldsContainer.setBorder(BorderFactory.createEmptyBorder());

        player1SetScores = new JSpinner[5];
        player2SetScores = new JSpinner[5];

        // Encabezados para los sets
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        scoreFieldsContainer.add(new JLabel(""), gbc);

        for (int i = 1; i <= 5; i++) {
            gbc.gridx = i;
            gbc.gridy = 0;
            JLabel setHeader = new JLabel("Set " + i, SwingConstants.CENTER);
            setHeader.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
            scoreFieldsContainer.add(setHeader, gbc);
        }

        // Filas para Jugador 1
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        player1Label = new JLabel("Jugador 1:");
        player1Label.setPreferredSize(new Dimension(80, 30));
        scoreFieldsContainer.add(player1Label, gbc);

        for (int i = 0; i < 5; i++) {
            gbc.gridx = i + 1;
            gbc.gridy = 1;
            player1SetScores[i] = createScoreSpinner(i);
            scoreFieldsContainer.add(player1SetScores[i], gbc);
        }

        // Filas para Jugador 2
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        player2Label = new JLabel("Jugador 2:");
        player2Label.setPreferredSize(new Dimension(80, 30));
        scoreFieldsContainer.add(player2Label, gbc);

        for (int i = 0; i < 5; i++) {
            gbc.gridx = i + 1;
            gbc.gridy = 2;
            player2SetScores[i] = createScoreSpinner(i);
            scoreFieldsContainer.add(player2SetScores[i], gbc);
        }

        // Añadir el contenedor de puntuaciones al layout principal
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        add(scoreFieldsContainer, gbc);
    }

    /**
     * Crea un *JSpinner* para ingresar la puntuación.
     *
     * @param index la posición del spinner
     * @return un *JSpinner* configurado para ingresar la puntuación
     */
    private JSpinner createScoreSpinner(int index) {
        JSpinner spinner = new JSpinner();
        spinner.setModel(new SpinnerNumberModel(0, 0, 7, 1));
        spinner.setFont(AppFonts.TEXTFIELD_FONT);
        spinner.setPreferredSize(new Dimension(60, 30));

        spinner.setEnabled(!(index == 3 || index == 4));
        spinner.addChangeListener(e -> evaluateSpinnersVisibility());
        return spinner;
    }

    /**
     * Evaluates the visibility of the spinners
     */
    private void evaluateSpinnersVisibility() {
        int setsWonPlayerOne = 0;
        int setsWonPlayerTwo = 0;

        int i;
        for (i = 0; i < player1SetScores.length; i++) {
            if(!player1SetScores[i].isEnabled())
                break;

            int scorePlayerOne = (int) player1SetScores[i].getModel().getValue();
            int scorePlayerTwo = (int) player2SetScores[i].getModel().getValue();

            if(scorePlayerOne > scorePlayerTwo) setsWonPlayerOne++;
            else if(scorePlayerTwo > scorePlayerOne) setsWonPlayerTwo++;
        }

        if((i == 3 || i == 4) && (setsWonPlayerOne + setsWonPlayerTwo == i) &&
                (setsWonPlayerOne < 3 && setsWonPlayerTwo < 3)) {
            player1SetScores[i].setEnabled(true);
            player2SetScores[i].setEnabled(true);
        }
    }

    /**
     * Añade el subpanel con los botones "Atrás" y "Confirmar".
     *
     * @param gbc las restricciones para el layout
     */
    private void addButtonsPanel(GridBagConstraints gbc) {
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setBackground(AppColors.BACKGROUND);
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonsPanel.setPreferredSize(new Dimension(400, 50));

        backButton = new JButton();
        backButton.setFont(AppFonts.BUTTON_FONT);
        backButton.setBackground(AppColors.BACK_BUTTON);
        backButton.setForeground(AppColors.FONT);
        backButton.setBorder(null);
        backButton.setPreferredSize(new Dimension(100, 30));
        backButton.setIcon(ImageUtils.loadScaledIcon("back.png", 30, 30));
        backButton.addActionListener(e -> parent.switchPanel("ManageTournaments"));
        buttonsPanel.add(backButton);

        confirmButton = new JButton("Confirmar");
        confirmButton.setFont(AppFonts.BUTTON_FONT);
        confirmButton.setBackground(AppColors.SIGNUP_CONFIRM_BUTTON);
        confirmButton.setForeground(AppColors.FONT);
        confirmButton.setBorder(null);
        confirmButton.setPreferredSize(new Dimension(100, 30));
        confirmButton.addActionListener(e -> fillMatchResults());
        buttonsPanel.add(confirmButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        add(buttonsPanel, gbc);
    }

    private void fillMatchResults() {
        Map<Integer, Pair<Integer, Integer>> points = new HashMap<>();
        Match match = (Match)matchDropdown.getSelectedItem();
        for (int i = 0; i < player1SetScores.length; i++) {
            if ((i == 3 || i == 4) && !player1SetScores[i].isEnabled())
                break;
            Integer scorePlayer1 = (Integer) player1SetScores[i].getModel().getValue();
            Integer scorePlayer2 = (Integer) player2SetScores[i].getModel().getValue();

            if (Objects.equals(scorePlayer1, scorePlayer2) && scorePlayer1 == 7) {
                JOptionPane.showMessageDialog(this, "No puede haber un set 7-7.");
                return;
            }

            points.put(i + 1, new Pair<>(scorePlayer1, scorePlayer2));
        }
       
        ((Match) Objects.requireNonNull(matchDropdown.getSelectedItem())).setPoints(points);
         Manager.getInstance().getDb().updateResults(match);
        //TODO: Add call to advance to save match with filled scores
        JOptionPane.showMessageDialog(this, "Partido guardado.");
        refresh();
    }

    /**
     * Adds a list of matches to the matches dropdown.
     *
     * @param matches the list of matches to add
     */
    public void addMatches(List<Match> matches) {
        matchDropdown.removeAllItems();
        int i=0;
        for (Match match : matches) {
            matchDropdown.addItem(match);
            System.out.println("Count: " + matchDropdown.getItemAt(i));
            i++;
        }
        System.out.println("Count: " + matchDropdown.getItemCount());
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
        matchDropdown.setSelectedIndex(-1);
        player1Label.setText("Jugador 1:");
        player2Label.setText("Jugador 2:");
        for (JSpinner player1SetScore : player1SetScores) player1SetScore.setValue(0);
        for (JSpinner player2SetScore : player2SetScores) player2SetScore.setValue(0);
        confirmButton.setEnabled(false);
    }
}
