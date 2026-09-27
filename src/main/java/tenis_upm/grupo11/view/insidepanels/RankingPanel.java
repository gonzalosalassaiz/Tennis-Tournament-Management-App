package tenis_upm.grupo11.view.insidepanels;

import javafx.util.Pair;
import tenis_upm.grupo11.view.utils.AppColors;
import tenis_upm.grupo11.view.utils.AppFonts;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

/**
 * This panel displays the ranking of users.
 */
public class RankingPanel extends JPanel {
    private static RankingPanel instance;
    private DefaultTableModel tableModel;

    /**
     * Private constructor to initialize the RankingPanel.
     */
    private RankingPanel() {
        setBackground(AppColors.BACKGROUND);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weightx = 1;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.weighty = 0;
        add(Box.createVerticalStrut(20), gbc);

        // Add the title, back button, and table components to the panel
        addTitle(gbc);
        addTable(gbc);

        // Add a glue (empty space) at the bottom to manage alignment
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.weighty = 1;
        add(Box.createVerticalGlue(), gbc);
    }

    /**
     * Singleton method to get the only instance of this class.
     *
     * @return the instance of RankingPanel.
     */
    public static RankingPanel getInstance() {
        if (instance == null) instance = new RankingPanel();
        return instance;
    }

    /**
     * Adds the title to the panel.
     *
     * @param gbc the GridBagConstraints for layout.
     */
    private void addTitle(GridBagConstraints gbc) {
        gbc.insets = new Insets(10, 10, 5, 10);
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.anchor = GridBagConstraints.NORTH;

        JLabel titleLabel = new JLabel("Ranking", SwingConstants.CENTER);
        titleLabel.setFont(AppFonts.TITLE_FONT);
        titleLabel.setForeground(AppColors.FONT);
        add(titleLabel, gbc);
    }

    /**
     * Adds the table to the panel.
     *
     * @param gbc the GridBagConstraints for layout.
     */
    private void addTable(GridBagConstraints gbc) {
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;

        // Create the table model
        String[] columnNames = {"#", "Username", "Points"};
        tableModel = new DefaultTableModel(columnNames, 0);

        // Create the table and configure its appearance
        JTable rankingTable = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        rankingTable.setFont(new Font("Arial", Font.PLAIN, 16));
        rankingTable.setForeground(Color.BLACK);
        rankingTable.setRowHeight(25);
        rankingTable.setBorder(BorderFactory.createEmptyBorder());

        // Set custom cell renderer for background color
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setBackground(AppColors.COMPONENT_BACKGROUND);
        cellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        cellRenderer.setOpaque(true);
        rankingTable.setDefaultRenderer(Object.class, cellRenderer);

        // Adjust column widths
        rankingTable.getColumnModel().getColumn(0).setPreferredWidth(70);
        rankingTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        rankingTable.getColumnModel().getColumn(2).setPreferredWidth(100);

        JTableHeader tableHeader = rankingTable.getTableHeader();
        tableHeader.setFont(AppFonts.TABLE_ROW_FONT);
        tableHeader.setBackground(AppColors.PANEL_BACKGROUND);
        tableHeader.setForeground(Color.BLACK);
        tableHeader.setBorder(BorderFactory.createLineBorder(AppColors.PANEL_BACKGROUND));

        JScrollPane scrollPane = new JScrollPane(rankingTable);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        scrollPane.setBackground(AppColors.PANEL_BACKGROUND);
        scrollPane.getViewport().setBackground(AppColors.COMPONENT_BACKGROUND);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        add(scrollPane, gbc);
    }

    /**
     * Updates the table with the given list of users.
     *
     * @param userList the list of users to display.
     */
    public void addUsers(List<Pair<String, Integer>> userList) {
        tableModel.setRowCount(0);
        for (int i = 0; i < userList.size(); i++) {
            Pair<String, Integer> user = userList.get(i);
            tableModel.addRow(
                    new Object[]{i + 1, user.getKey(), user.getValue()});
        }
    }
}
