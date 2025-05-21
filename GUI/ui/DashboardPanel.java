// --- com/deepmanage/ui/DashboardPanel.java ---
package ui;

import dto.TransactionDisplayData;
import exception.AlException;
import exception.QueryException;
import service.FinancialHealthAlService;
import service.TransactionQueryService;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class DashboardPanel extends JPanel {

    private final String currentUserId;
    private final FinancialHealthAlService healthAlService;
    private final TransactionQueryService transactionQueryService;

    private JLabel totalSavingsLabel;
    private JLabel monthlySpendingLabel;
    private JLabel nextSavingsGoalLabel;
    private JPanel spendingChartPanel; // For simulated chart
    private JList<String> recentTransactionsList;
    private DefaultListModel<String> recentTransactionsListModel;
    private JTextArea quickTipsArea;


    public DashboardPanel(String userId, FinancialHealthAlService healthService, TransactionQueryService tqService) {
        this.currentUserId = userId;
        this.healthAlService = healthService;
        this.transactionQueryService = tqService;

        initComponents();
        loadDashboardData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15)); // Gaps between regions
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);

        // --- Top Panel: Key Metrics ---
        JPanel keyMetricsPanel = createKeyMetricsPanel();
        add(keyMetricsPanel, BorderLayout.NORTH);

        // --- Center Panel: Chart and Tips ---
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 15, 0)); // 1 row, 2 columns for chart & tips
        centerPanel.setOpaque(false); // Transparent to show main background

        spendingChartPanel = createSpendingChartPanel();
        centerPanel.add(spendingChartPanel);

        JPanel quickTipsPanel = createQuickTipsPanel();
        centerPanel.add(quickTipsPanel);

        add(centerPanel, BorderLayout.CENTER);

        // --- Bottom Panel: Recent Transactions ---
        JPanel recentTransactionsPanel = createRecentTransactionsPanel();
        add(recentTransactionsPanel, BorderLayout.SOUTH);
    }

    private JPanel createKeyMetricsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 15, 0)); // 1 row, 3 metrics, with gaps
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0,0,15,0)); // Bottom margin

        totalSavingsLabel = createMetricLabel("¥0.00", "Total Savings");
        monthlySpendingLabel = createMetricLabel("¥0.00", "This Month's Spending");
        nextSavingsGoalLabel = createMetricLabel("N/A (0%)", "Next Savings Goal");

        panel.add(createMetricBox(totalSavingsLabel, "Total Savings"));
        panel.add(createMetricBox(monthlySpendingLabel, "This Month's Spending"));
        panel.add(createMetricBox(nextSavingsGoalLabel, "Next Savings Goal"));
        return panel;
    }

    private JLabel createMetricLabel(String value, String title) {
        JLabel label = new JLabel(value, SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 20));
        label.setForeground(new Color(0x2C3E50)); // Dark blue-gray text
        return label;
    }
    private JPanel createMetricBox(JLabel valueLabel, String title) {
        JPanel box = new JPanel(new BorderLayout());
        box.setBackground(new Color(0xECF0F1)); // Light gray background for the box
        box.setBorder(new CompoundBorder(
                new LineBorder(new Color(0xBDC3C7), 1), // Light border
                new EmptyBorder(15, 15, 15, 15) // Padding
        ));
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        titleLabel.setForeground(Color.DARK_GRAY);
        box.add(valueLabel, BorderLayout.CENTER);
        box.add(titleLabel, BorderLayout.SOUTH);
        return box;
    }


    private JPanel createSpendingChartPanel() {
        JPanel panel = new JPanel(); // Will use custom painting
        panel.setBackground(new Color(0xECF0F1));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0xBDC3C7)),
                " Monthly Spending Breakdown ",
                javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("SansSerif", Font.BOLD, 14),
                new Color(0x2C3E50)
        ));
        panel.setPreferredSize(new Dimension(300, 250)); // Initial preferred size
        // Custom painting will be done in overridden paintComponent method
        return panel;
    }


    private JPanel createQuickTipsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0xECF0F1));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0xBDC3C7)),
                " Quick Financial Tips & Alerts ",
                javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("SansSerif", Font.BOLD, 14),
                new Color(0x2C3E50)
        ));

        quickTipsArea = new JTextArea();
        quickTipsArea.setEditable(false);
        quickTipsArea.setLineWrap(true);
        quickTipsArea.setWrapStyleWord(true);
        quickTipsArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        quickTipsArea.setOpaque(false); // Transparent background
        quickTipsArea.setBorder(new EmptyBorder(10,10,10,10));

        // Sample Tips
        quickTipsArea.setText(
                "Tip: Review your subscriptions monthly! Unused ones add up.\n\n" +
                        "Alert: Upcoming 'Double 11' sales. Plan your budget!\n\n" +
                        "Tip: Automate a small portion of your income into savings each payday."
        );

        JScrollPane scrollPane = new JScrollPane(quickTipsArea);
        scrollPane.setBorder(null); // Remove scrollpane border
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }


    private JPanel createRecentTransactionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(), // No outer line border for this title
                " Recent Transactions ",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("SansSerif", Font.BOLD, 14),
                new Color(0x2C3E50)
        ));
        panel.setPreferredSize(new Dimension(getWidth(), 150));


        recentTransactionsListModel = new DefaultListModel<>();
        recentTransactionsList = new JList<>(recentTransactionsListModel);
        recentTransactionsList.setFont(new Font("SansSerif", Font.PLAIN, 13));
        recentTransactionsList.setBackground(new Color(0xECF0F1));
        recentTransactionsList.setBorder(new EmptyBorder(5,10,5,10));


        JScrollPane scrollPane = new JScrollPane(recentTransactionsList);
        scrollPane.setBorder(new LineBorder(new Color(0xBDC3C7), 1));
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void loadDashboardData() {
        // Use SwingWorker to load data in the background
        new SwingWorker<Void, Void>() {
            Map<String, BigDecimal> budgetData = Collections.emptyMap();
            List<TransactionDisplayData> transactions = Collections.emptyList();
            String errorMessage = null;

            @Override
            protected Void doInBackground() {
                try {
                    // Fetch budget data (can be repurposed for spending chart)
                    budgetData = healthAlService.recommendBudget(currentUserId);
                    // Fetch recent transactions
                    transactions = transactionQueryService.getTransactionsForReview(currentUserId);
                } catch (AlException | QueryException e) {
                    errorMessage = e.getMessage();
                    System.err.println("Error loading dashboard data: " + e.getMessage());
                }
                return null;
            }

            @Override
            protected void done() {
                if (errorMessage != null) {
                    DeepManageApp.showErrorDialog(DashboardPanel.this, "Could not load dashboard data: " + errorMessage);
                }

                NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.CHINA); // For ¥ symbol

                // --- Update Key Metrics (Mocked/Calculated) ---
                BigDecimal totalSavings = new BigDecimal("25750.80"); // Mock value
                totalSavingsLabel.setText(currencyFormat.format(totalSavings));

                // Calculate this month's spending from transactions (example - more complex logic needed for real app)
                BigDecimal currentMonthSpending = transactions.stream()
                        .filter(t -> YearMonth.from(t.getDate()).equals(YearMonth.now())) // Filter for current month
                        .filter(t -> t.getAmount() != null && t.getAmount().compareTo(BigDecimal.ZERO) < 0) // Assuming negative for spending
                        .map(t -> t.getAmount().abs())
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                if (currentMonthSpending.compareTo(BigDecimal.ZERO) == 0) { // If no data, use mock
                    currentMonthSpending = new BigDecimal("3520.50");
                }
                monthlySpendingLabel.setText(currencyFormat.format(currentMonthSpending));

                // Mock savings goal
                nextSavingsGoalLabel.setText("New Laptop (60%)");


                // --- Update Recent Transactions List ---
                recentTransactionsListModel.clear();
                transactions.stream()
                        .sorted(Comparator.comparing(TransactionDisplayData::getDate).reversed()) // Most recent first
                        .limit(5) // Show top 5
                        .forEach(tx -> {
                            String displayText = String.format("<html><b>%s</b>: %s %s <font color='gray'>(%s)</font></html>",
                                    tx.getDescription(),
                                    currencyFormat.format(tx.getAmount()),
                                    tx.getAiSuggestedCategory(),
                                    tx.getDate().format(DateTimeFormatter.ofPattern("MM-dd")));
                            recentTransactionsListModel.addElement(displayText);
                        });
                if(recentTransactionsListModel.isEmpty()){
                    recentTransactionsListModel.addElement("No recent transactions to display.");
                }


                // --- Update Spending Chart (Simulated) ---
                final Map<String, BigDecimal> finalBudgetData = budgetData;
                spendingChartPanel.removeAll(); // Clear previous custom painted components if any
                spendingChartPanel.setLayout(new BorderLayout()); // For the title label

                JLabel chartTitleLabel = new JLabel("Spending by Category (Simulated)", SwingConstants.CENTER);
                chartTitleLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
                chartTitleLabel.setForeground(new Color(0x34495E));
                chartTitleLabel.setBorder(new EmptyBorder(5,0,10,0));
                spendingChartPanel.add(chartTitleLabel, BorderLayout.NORTH);


                // Create a panel for the bars themselves
                JPanel barsPanel = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        if (finalBudgetData == null || finalBudgetData.isEmpty()) {
                            g.setColor(Color.DARK_GRAY);
                            g.drawString("No spending data available for chart.", getWidth()/2 - 70, getHeight()/2);
                            return;
                        }

                        Graphics2D g2d = (Graphics2D) g;
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                        List<Map.Entry<String, BigDecimal>> topCategories = finalBudgetData.entrySet().stream()
                                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                                .limit(4) // Show top 4 categories
                                .collect(Collectors.toList());

                        if (topCategories.isEmpty()) return;

                        double maxValue = topCategories.stream().mapToDouble(e -> e.getValue().doubleValue()).max().orElse(1.0);
                        int barWidth = (getWidth() - (topCategories.size() + 1) * 15 - 40) / topCategories.size(); // Adjusted padding
                        int chartHeight = getHeight() - 60; // Space for labels and title
                        int x = 30; // Initial x position with left padding

                        Color[] colors = {new Color(0x3498DB), new Color(0x2ECC71), new Color(0xE74C3C), new Color(0xF1C40F)};

                        for (int i = 0; i < topCategories.size(); i++) {
                            Map.Entry<String, BigDecimal> entry = topCategories.get(i);
                            double value = entry.getValue().doubleValue();
                            int barHeight = (int) ((value / maxValue) * chartHeight);

                            g2d.setColor(colors[i % colors.length]);
                            g2d.fillRect(x, getHeight() - 30 - barHeight, barWidth, barHeight); // Adjusted y for bottom labels

                            g2d.setColor(Color.BLACK);
                            String categoryName = entry.getKey();
                            if(categoryName.length() > barWidth/6) categoryName = categoryName.substring(0, Math.min(categoryName.length(), barWidth/6)) + ".."; // Truncate

                            g2d.drawString(categoryName, x + barWidth / 2 - g2d.getFontMetrics().stringWidth(categoryName) / 2, getHeight() - 10);
                            x += barWidth + 15; // Gap between bars
                        }
                    }
                };
                barsPanel.setOpaque(false); // Let parent panel background show through
                spendingChartPanel.add(barsPanel, BorderLayout.CENTER);
                spendingChartPanel.revalidate();
                spendingChartPanel.repaint();
            }
        }.execute();
    }
}