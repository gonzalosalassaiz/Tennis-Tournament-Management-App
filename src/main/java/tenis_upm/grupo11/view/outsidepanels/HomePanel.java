package tenis_upm.grupo11.view.outsidepanels;

import tenis_upm.grupo11.functionality.Manager;
import tenis_upm.grupo11.view.MainFrame;
import tenis_upm.grupo11.view.insidepanels.*;
import tenis_upm.grupo11.view.interfaces.IParentView;
import tenis_upm.grupo11.view.interfaces.IRefreshable;
import tenis_upm.grupo11.view.utils.AppColors;
import tenis_upm.grupo11.view.utils.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * This is the home panel of the application
 */
public class HomePanel extends JPanel implements IParentView {
    private static HomePanel instance;
    private JPanel insidePanel;
    private Map<String, JPanel> childPanels;
    private JButton profileButton;
    private JButton logoutButton;

    /**
     * HomePanel's constructor
     */
    private HomePanel() {
        setLayout(new BorderLayout());
        setBackground(AppColors.BACKGROUND);

        add(createTopBar(), BorderLayout.NORTH);

        mapChildPanels();
        insidePanel = FunctionalitiesPanel.getInstance();
        add(insidePanel, BorderLayout.CENTER);
    }

    /**
     * Singleton proper function to get the only instance of this class
     *
     * @return the only instance of this class
     */
    public static HomePanel getInstance() {
        if (instance == null) instance = new HomePanel();
        return instance;
    }

    /**
     * Creates the top bar with a button on the left
     *
     * @return the top bar panel
     */
    private JPanel createTopBar() {
        JPanel topBar = new JPanel();
        topBar.setLayout(new BorderLayout());
        topBar.setBackground(AppColors.BACKGROUND);

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.X_AXIS));
        leftPanel.setBackground(AppColors.BACKGROUND);

        // Create and add the home button
        JButton homeButton = new JButton();
        homeButton.setFocusPainted(false);
        homeButton.setPreferredSize(new Dimension(50, 40));
        homeButton.setIcon(ImageUtils.loadScaledIcon("home.png", 32, 32));
        homeButton.addActionListener(e -> switchPanel("Functionalities"));
        leftPanel.add(homeButton);

        // Create and add the profile button
        profileButton = new JButton();
        profileButton.setFocusPainted(false);
        profileButton.setPreferredSize(new Dimension(50, 40));
        profileButton.setIcon(ImageUtils.loadScaledIcon("profile.png", 32, 32));
        profileButton.addActionListener(e -> switchPanel("Profile"));
        leftPanel.add(profileButton);
        topBar.add(leftPanel, BorderLayout.WEST);

        // Create and add the logout button
        logoutButton = new JButton();
        logoutButton.setFocusPainted(false);
        logoutButton.setPreferredSize(new Dimension(50, 40));
        logoutButton.setIcon(ImageUtils.loadScaledIcon("logout.png", 32, 32));
        logoutButton.addActionListener(e -> {
            Manager.getInstance().getDb().logout();
            ((MainFrame) SwingUtilities.getWindowAncestor(HomePanel.this))
                    .switchPanel("Login");
        });
        topBar.add(logoutButton, BorderLayout.EAST);

        return topBar;
    }

    @Override
    public void mapChildPanels() {
        childPanels = new HashMap<>();
        childPanels.put("Ranking", RankingPanel.getInstance());
        childPanels.put("Match Results", ResultsMatchPanel.getInstance());
        childPanels.put("CreateTournament", CreateTournamentPanel.getInstance());
        childPanels.put("Functionalities", FunctionalitiesPanel.getInstance());
        childPanels.put("ManageTournaments", ManageTournamentsPanel.getInstance());
        childPanels.put("Profile", ProfilePanel.getInstance());
        childPanels.put("RegisterTournament", RegisterTournamentPanel.getInstance());
        childPanels.put("TournamentsStats", MyTournamentsStatsPanel.getInstance());

        FunctionalitiesPanel.getInstance().setParent(this);
        ManageTournamentsPanel.getInstance().setParent(this);
        ResultsMatchPanel.getInstance().setParent(this);
    }

    @Override
    public void switchPanel(String panelCodename) {
        if (childPanels.containsKey(panelCodename)) {
            remove(insidePanel);
            insidePanel = childPanels.get(panelCodename);
            setButtonsVisibility();
            add(insidePanel, BorderLayout.CENTER);
            if (insidePanel instanceof IRefreshable) {
                ((IRefreshable) insidePanel).refresh();
            }
            revalidate();
            repaint();
        }
    }

    /**
     * Sets the visibility for some buttons in the topBar
     */
    private void setButtonsVisibility() {
        profileButton.setVisible(insidePanel instanceof FunctionalitiesPanel);
        logoutButton.setVisible(insidePanel instanceof FunctionalitiesPanel);
    }
}
