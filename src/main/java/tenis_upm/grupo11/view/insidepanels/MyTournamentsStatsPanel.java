package tenis_upm.grupo11.view.insidepanels;

import tenis_upm.grupo11.data.PlayerTournamentStats;
import tenis_upm.grupo11.view.utils.AppColors;
import tenis_upm.grupo11.view.utils.AppFonts;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class MyTournamentsStatsPanel extends JPanel {
    private static MyTournamentsStatsPanel instance;
    private JTable statsTable;
    private DefaultTableModel tableModel;

    private MyTournamentsStatsPanel() {
        setBackground(AppColors.BACKGROUND);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        // Initialize components
        initializeTable();

        // Add components
        addTitleLabel(gbc);
        addStatsTable(gbc);
    }

    public static MyTournamentsStatsPanel getInstance() {
        if (instance == null) {
            instance = new MyTournamentsStatsPanel();
        }
        return instance;
    }

    private void addTitleLabel(GridBagConstraints gbc) {
        JLabel titleLabel = new JLabel("Mis Estadísticas de Torneos", SwingConstants.CENTER);
        titleLabel.setFont(AppFonts.SUBTITLE_FONT);
        titleLabel.setForeground(AppColors.FONT);

        gbc.insets = new Insets(10, 0, 20, 0);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        add(titleLabel, gbc);
    }

    private void addStatsTable(GridBagConstraints gbc) {
        JScrollPane scrollPane = new JScrollPane(statsTable);
        scrollPane.setPreferredSize(new Dimension(620, 300));
        scrollPane.setBackground(AppColors.PANEL_BACKGROUND);
        scrollPane.getViewport().setBackground(AppColors.COMPONENT_BACKGROUND);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(scrollPane, gbc);
    }

    private void initializeTable() {
        String[] columnNames = {
                "Torneo",
                "Posición",
                "Sets (W)",
                "Juegos (W)",
                "Juegos (L)"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        statsTable = new JTable(tableModel);
        statsTable.setFont(new Font("Arial", Font.PLAIN, 16));
        statsTable.setForeground(Color.BLACK);
        statsTable.setBackground(AppColors.COMPONENT_BACKGROUND);
        statsTable.setRowHeight(30);

        // Set custom cell renderer for background color
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setBackground(AppColors.COMPONENT_BACKGROUND);
        cellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        cellRenderer.setOpaque(true);
        statsTable.setDefaultRenderer(Object.class, cellRenderer);

        statsTable.getColumnModel().getColumn(0).setPreferredWidth(220);
        statsTable.getColumnModel().getColumn(1).setPreferredWidth(85);
        statsTable.getColumnModel().getColumn(2).setPreferredWidth(115);
        statsTable.getColumnModel().getColumn(3).setPreferredWidth(115);
        statsTable.getColumnModel().getColumn(4).setPreferredWidth(115);

        JTableHeader tableHeader = statsTable.getTableHeader();
        tableHeader.setFont(AppFonts.LABEL_TEXTFIELD_FONT);
        tableHeader.setBackground(AppColors.PANEL_BACKGROUND);
        tableHeader.setForeground(Color.BLACK);
        tableHeader.setBorder(BorderFactory.createLineBorder(AppColors.PANEL_BACKGROUND));
    }

    public void loadStats(List<PlayerTournamentStats> statsList) {
        tableModel.setRowCount(0);

        for (PlayerTournamentStats stats : statsList) {
            Object[] rowData = {
                    stats.getTournamentFullName(),
                    stats.getPosition() + "º",
                    stats.getSetWins(),
                    stats.getGameWins(),
                    stats.getGameLosses()
            };
            tableModel.addRow(rowData);
        }
    }
}
