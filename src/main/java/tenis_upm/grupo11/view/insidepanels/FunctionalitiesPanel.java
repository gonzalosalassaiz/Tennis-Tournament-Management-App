package tenis_upm.grupo11.view.insidepanels;

import tenis_upm.grupo11.data.User;
import tenis_upm.grupo11.functionality.Manager;
import tenis_upm.grupo11.view.interfaces.IRefreshable;
import tenis_upm.grupo11.view.outsidepanels.HomePanel;
import tenis_upm.grupo11.view.utils.AppColors;
import tenis_upm.grupo11.view.utils.AppFonts;
import tenis_upm.grupo11.view.utils.ImageUtils;

import javax.swing.*;
import java.awt.*;

/**
 * This panel contains buttons leading to functionalities of the app
 */
public class FunctionalitiesPanel extends JPanel implements IRefreshable {
    private static FunctionalitiesPanel instance;
    private HomePanel parent;
    private JButton rankingButton;
    private JButton manageTournamentsButton;
    private JButton registerTournamentButton;
    private JButton createTournamentButton;
    private JButton tournamentsStatsButton;
    private JButton addResults;
    /**
     * FunctionalitiesPanel's constructor
     */
    private FunctionalitiesPanel() {
        setLayout(new GridBagLayout());
        setBackground(AppColors.BACKGROUND);
        createButtons();
    }

    /**
     * Singleton proper function to get the only instance of this class
     *
     * @return the only instance of this class
     */
    public static FunctionalitiesPanel getInstance() {
        if (instance == null) instance = new FunctionalitiesPanel();
        return instance;
    }

    /**
     * Creates the buttons inside the panel
     */
    private void createButtons() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Create and add the rankingButton
        rankingButton = createCustomButton("Ranking", "ranking.png");
        rankingButton.addActionListener(e -> {
            Manager.getInstance().getDb().computeRanking();
            parent.switchPanel("Ranking");
        });

        // Create allTournamentsButton
        manageTournamentsButton = createCustomButton(
                "<html><center>Gestionar<br>Torneos</center></html>",
                "tournament.png"
        );
        manageTournamentsButton.addActionListener(e -> {
        	Manager.getInstance().getDb().showTournamentsToInitiate();
        	parent.switchPanel("ManageTournaments");
        });

        // Create registerTournamentButton
        registerTournamentButton = createCustomButton(
                "<html><center>Registrarse en<br>un Torneo</center></html>",
                "register-tournament.png"
        );
        registerTournamentButton.addActionListener(e -> {
            Manager.getInstance().getDb().showRegisterTournaments();
            parent.switchPanel("RegisterTournament");
        });
        add(registerTournamentButton, gbc);

        gbc.gridx = 1;

        // Create the createTournamentButton
        createTournamentButton = createCustomButton(
                "<html><center>Crear un<br>Torneo</center></html>",
                "create-tournament.png"
        );
        createTournamentButton.addActionListener(e ->
                parent.switchPanel("CreateTournament"));

        // Create the tournamentsStatsButton
        tournamentsStatsButton = createCustomButton(
                "<html><center>Mis Estadísticas<br> de Torneos</center></html>",
                "stats.png");
        tournamentsStatsButton.addActionListener(e -> {
            Manager.getInstance().getDb().loadTournamentStats();
            parent.switchPanel("TournamentsStats");
        });
    }

    /**
     * Creates a custom button
     *
     * @param text      the text for the button
     * @param imageName the image name for the icon
     * @return the button
     */
    private JButton createCustomButton(String text, String imageName) {
        JButton button = new JButton(text);
        button.setFont(AppFonts.BUTTON_FONT);
        button.setFocusPainted(false);
        button.setForeground(AppColors.FONT);
        button.setBackground(AppColors.HOME_BUTTON);
        button.setPreferredSize(new Dimension(200, 100));
        button.setIcon(ImageUtils.loadScaledIcon(imageName, 32, 32));
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        button.setIconTextGap(10);

        return button;
    }

    public void setParent(HomePanel parent) {
        this.parent = parent;
    }

    @Override
    public void refresh() {
        User currentUser = Manager.getInstance().getDb().getActualUser();
        boolean isAdmin = currentUser != null && currentUser.isAdmin();

        rankingButton.setVisible(true);
        manageTournamentsButton.setVisible(isAdmin);
        registerTournamentButton.setVisible(!isAdmin);
        createTournamentButton.setVisible(isAdmin);
        tournamentsStatsButton.setVisible(!isAdmin);

        removeAll();

        JButton[] buttons = {
                rankingButton,
                manageTournamentsButton,
                registerTournamentButton,
                createTournamentButton,
                tournamentsStatsButton
        };

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 20, 20, 20);

        for (JButton button : buttons) {
            if (button.isVisible()) {
                add(button, gbc);

                gbc.gridx++;
                if (gbc.gridx > 1) {
                    gbc.gridx = 0;
                    gbc.gridy++;
                }
            }
        }

        revalidate();
        repaint();
    }
}
