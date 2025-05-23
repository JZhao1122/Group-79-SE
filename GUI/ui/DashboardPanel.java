package ui;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import dto.TransactionData;
import service.FinancialTransactionService;

public class DashboardPanel extends JPanel {

    private final FinancialTransactionService financialTransactionService;
    private final String currentUserId;
    private JLabel totalSpendingLabel;
    private JLabel savingsGoalLabel;
    private JPanel pieChartPanelContainer; // Panel to hold the pie chart
    private JPanel lineChartPanelContainer; // Panel to hold the line chart
    private final DeepManageApp appReference; // Reference to main app, if needed for fetching initial goal for example

    public DashboardPanel(FinancialTransactionService financialTransactionService, String userId, DeepManageApp app) {
        this.financialTransactionService = financialTransactionService;
        this.currentUserId = userId;
        this.appReference = app; // Store the reference
        initComponents();
        // Optionally, set initial savings goal display from appReference if it holds a persistent value
        setSavingsGoalDisplay(appReference.getCurrentGlobalSavingsGoal()); 
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);

        // --- Top Panel: Summary Text (Left) and Pie Chart (Right) ---
        JPanel topContentPanel = new JPanel(new GridBagLayout()); // Changed to GridBagLayout
        topContentPanel.setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();

        // Left side: Summary Text
        JPanel summaryTextPanel = new JPanel(new GridLayout(2, 1, 0, 5)); 
        summaryTextPanel.setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);
        summaryTextPanel.setBorder(BorderFactory.createTitledBorder("Summary"));

        totalSpendingLabel = new JLabel("Total Spending: N/A"); // Translated
        totalSpendingLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        savingsGoalLabel = new JLabel("Savings Goal: N/A"); // Initial text, will be updated
        savingsGoalLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        
        summaryTextPanel.add(totalSpendingLabel);
        summaryTextPanel.add(savingsGoalLabel);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3; // Summary panel takes 30% of horizontal space
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 15); // Right margin for summary panel
        topContentPanel.add(summaryTextPanel, gbc);

        // Right side: Pie Chart Container
        pieChartPanelContainer = new JPanel(new BorderLayout());
        pieChartPanelContainer.setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);
        pieChartPanelContainer.setBorder(BorderFactory.createTitledBorder("Spending by Category"));
        JLabel pieChartPlaceholder = new JLabel("Pie chart will be displayed after data import", SwingConstants.CENTER);
        pieChartPanelContainer.add(pieChartPlaceholder, BorderLayout.CENTER);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.7; // Pie chart panel takes 70% of horizontal space
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 0); // No margin needed here
        topContentPanel.add(pieChartPanelContainer, gbc);

        add(topContentPanel, BorderLayout.NORTH);

        // --- Center Panel: Line Chart ---
        lineChartPanelContainer = new JPanel(new BorderLayout());
        lineChartPanelContainer.setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);
        lineChartPanelContainer.setBorder(BorderFactory.createTitledBorder("Daily Spending Trend"));
        JLabel lineChartPlaceholder = new JLabel("Line chart will be displayed after data import", SwingConstants.CENTER);
        lineChartPanelContainer.add(lineChartPlaceholder, BorderLayout.CENTER);
        add(lineChartPanelContainer, BorderLayout.CENTER);

        // --- Bottom Panel: Refresh Button ---
        JButton refreshButton = new JButton("Refresh Data");
        refreshButton.addActionListener(e -> loadAndDisplayCharts());
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        southPanel.setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);
        southPanel.add(refreshButton);
        add(southPanel, BorderLayout.SOUTH);

        System.out.println("[DashboardPanel] Initialization complete. Waiting for data refresh or updates.");
    }

    public void loadAndDisplayCharts() {
        System.out.println("[DashboardPanel] loadAndDisplayCharts called. Loading data...");
        List<TransactionData> transactions = null;
        try {
            transactions = financialTransactionService.getAllTransactions(currentUserId);
            System.out.println("[DashboardPanel] Retrieved " + (transactions != null ? transactions.size() : 0) + " transactions.");
            // Debug output: print each transaction
            if (transactions != null && !transactions.isEmpty()) {
                System.out.println("[DashboardPanel] Transaction details:");
                for (TransactionData t : transactions) {
                    System.out.println("[DEBUG] Transaction: " + t.getDescription() + " - ¥" + t.getAmount() + " - Category: " + t.getCategory() + " - Date: " + t.getDate());
                }
            } else {
                System.out.println("[DashboardPanel] No transaction data to display.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading transaction data: " + e.getMessage(), "Data Error", JOptionPane.ERROR_MESSAGE);
            totalSpendingLabel.setText("Total Spending: Error");
            return;
        }

        if (transactions == null || transactions.isEmpty()) {
            System.out.println("[DashboardPanel] No transactions found.");
            totalSpendingLabel.setText("Total Spending: ¥0.00");
            // Clear chart areas
            pieChartPanelContainer.removeAll();
            pieChartPanelContainer.add(new JLabel("No data available for pie chart", SwingConstants.CENTER));
            pieChartPanelContainer.revalidate();
            pieChartPanelContainer.repaint();

            lineChartPanelContainer.removeAll();
            lineChartPanelContainer.add(new JLabel("No data available for line chart", SwingConstants.CENTER));
            lineChartPanelContainer.revalidate();
            lineChartPanelContainer.repaint();
            return;
        }
        
        // Calculate and display Total Spending
        BigDecimal totalSpent = BigDecimal.ZERO;
        for (TransactionData t : transactions) {
            if (t.getAmount() != null) { // Ensure Amount is not null
                totalSpent = totalSpent.add(t.getAmount());
                System.out.println("[DEBUG] Adding to total: " + t.getAmount() + " New total: " + totalSpent);
            }
        }
        System.out.println("[DashboardPanel] Total spending calculated: " + totalSpent);
        totalSpendingLabel.setText(String.format("Total Spending: ¥%,.2f", totalSpent));

        // Savings goal is updated by setSavingsGoalDisplay via DeepManageApp
        
        System.out.println("[DashboardPanel] Transaction data loaded. Generating charts...");
        createPieChart(transactions); // Create pie chart
        createLineChart(transactions); // Create line chart
    }
    
    // Public method to be called by DeepManageApp to update the savings goal display
    public void setSavingsGoalDisplay(BigDecimal goalAmount) {
        if (goalAmount != null) {
            savingsGoalLabel.setText(String.format("Savings Goal: ¥%,.2f", goalAmount));
        } else {
            savingsGoalLabel.setText("Savings Goal: N/A");
        }
        System.out.println("[DashboardPanel] Savings goal display updated to: " + (goalAmount != null ? goalAmount.toPlainString() : "N/A"));
    }
    
    private void createPieChart(List<TransactionData> transactions) {
        // Group by category and sum amounts
        Map<String, BigDecimal> categoryTotals = new HashMap<>();
        for (TransactionData t : transactions) {
            if (t.getAmount() != null) {
                String category = t.getCategory();
                // If category is null or empty, use "Uncategorized"
                if (category == null || category.trim().isEmpty()) {
                    category = "Uncategorized";
                }
                
                BigDecimal currentAmount = categoryTotals.getOrDefault(category, BigDecimal.ZERO);
                categoryTotals.put(category, currentAmount.add(t.getAmount()));
            }
        }
        
        System.out.println("[DashboardPanel] Category grouping results:");
        for (Map.Entry<String, BigDecimal> entry : categoryTotals.entrySet()) {
            System.out.println("Category: " + entry.getKey() + ", Amount: ¥" + entry.getValue());
        }
        
        // Create custom pie chart panel
        JPanel customPieChart = new SimplePieChart(categoryTotals, totalSpendingLabel.getFont());
        customPieChart.setPreferredSize(new Dimension(400, 300));
        
        // Update UI
        pieChartPanelContainer.removeAll();
        pieChartPanelContainer.add(customPieChart, BorderLayout.CENTER);
        
        // Force redraw
        pieChartPanelContainer.revalidate();
        pieChartPanelContainer.repaint();
        customPieChart.revalidate();
        customPieChart.repaint();
        this.revalidate();
        this.repaint();
        
        System.out.println("[DashboardPanel] Pie chart created and displayed.");
    }
    
    private void createLineChart(List<TransactionData> transactions) {
        // Group by date and sum amounts
        Map<LocalDate, BigDecimal> dailyTotals = new HashMap<>();
        LocalDate minDate = null;
        LocalDate maxDate = null;
        
        for (TransactionData t : transactions) {
            if (t.getAmount() != null && t.getDate() != null) {
                LocalDate date = t.getDate();
                
                // Update min and max dates
                if (minDate == null || date.isBefore(minDate)) {
                    minDate = date;
                }
                if (maxDate == null || date.isAfter(maxDate)) {
                    maxDate = date;
                }
                
                BigDecimal currentAmount = dailyTotals.getOrDefault(date, BigDecimal.ZERO);
                dailyTotals.put(date, currentAmount.add(t.getAmount()));
            }
        }
        
        // If no valid date data, show message and return
        if (minDate == null || maxDate == null) {
            lineChartPanelContainer.removeAll();
            lineChartPanelContainer.add(new JLabel("No valid date data available", SwingConstants.CENTER));
            lineChartPanelContainer.revalidate();
            lineChartPanelContainer.repaint();
            return;
        }
        
        System.out.println("[DashboardPanel] Date range: " + minDate + " to " + maxDate);
        System.out.println("[DashboardPanel] Date grouping results:");
        
        // Sort by date and print debug info
        TreeMap<LocalDate, BigDecimal> sortedDailyTotals = new TreeMap<>(dailyTotals);
        for (Map.Entry<LocalDate, BigDecimal> entry : sortedDailyTotals.entrySet()) {
            System.out.println("Date: " + entry.getKey() + ", Amount: ¥" + entry.getValue());
        }
        
        // Create custom line chart panel
        JPanel customLineChart = new SimpleLineChart(sortedDailyTotals, totalSpendingLabel.getFont());
        customLineChart.setPreferredSize(new Dimension(600, 300));
        
        // Update UI
        lineChartPanelContainer.removeAll();
        lineChartPanelContainer.add(customLineChart, BorderLayout.CENTER);
        
        // Force redraw
        lineChartPanelContainer.revalidate();
        lineChartPanelContainer.repaint();
        customLineChart.revalidate();
        customLineChart.repaint();
        
        System.out.println("[DashboardPanel] Line chart created and displayed.");
    }
    
    // Simple pie chart implementation
    private static class SimplePieChart extends JPanel {
        private final Map<String, BigDecimal> data;
        private final Map<String, Color> colorMap = new HashMap<>();
        private final Font font;
        
        public SimplePieChart(Map<String, BigDecimal> data, Font font) {
            this.data = data;
            this.font = new Font(font.getName(), Font.PLAIN, 12); // Use smaller font
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            // Use predefined colors instead of random colors
            Color[] predefinedColors = {
                new Color(70, 130, 180),   // Steel Blue
                new Color(255, 99, 71),    // Tomato
                new Color(60, 179, 113),   // Medium Sea Green
                new Color(238, 130, 238),  // Violet
                new Color(255, 165, 0),    // Orange
                new Color(106, 90, 205),   // Slate Blue
                new Color(255, 215, 0),    // Gold
                new Color(30, 144, 255),   // Dodger Blue
                new Color(250, 128, 114),  // Salmon
                new Color(154, 205, 50)    // Yellow Green
            };
            
            int i = 0;
            for (String category : data.keySet()) {
                colorMap.put(category, predefinedColors[i % predefinedColors.length]);
                i++;
            }
            
            System.out.println("[SimplePieChart] Created pie chart with " + data.size() + " categories");
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int width = getWidth();
            int height = getHeight();
            int pieSize = Math.min(width, height) - 80; // Leave space for legend
            
            if (pieSize < 50) {
                g2d.drawString("Window too small to display chart", 10, 20);
                return; // Too small, don't draw
            }
            
            int centerX = width / 3;
            int centerY = height / 2;
            
            // Calculate total amount
            BigDecimal total = BigDecimal.ZERO;
            for (BigDecimal amount : data.values()) {
                total = total.add(amount);
            }
            
            // If total is zero, show message
            if (total.compareTo(BigDecimal.ZERO) == 0) {
                g2d.drawString("No transaction data", centerX - 50, centerY);
                return;
            }
            
            System.out.println("[SimplePieChart.paint] Starting to draw pie chart, total amount: " + total);
            
            // Draw pie chart
            int startAngle = 0;
            int i = 0;
            for (Map.Entry<String, BigDecimal> entry : data.entrySet()) {
                String category = entry.getKey();
                BigDecimal amount = entry.getValue();
                
                // Calculate angle for this category
                double proportion = amount.doubleValue() / total.doubleValue();
                int angle = (int) (proportion * 360);
                
                if (angle > 0) { // Only draw if angle is greater than 0
                    // Fill sector
                    g2d.setColor(colorMap.get(category));
                    g2d.fillArc(centerX - pieSize/2, centerY - pieSize/2, pieSize, pieSize, startAngle, angle);
                    System.out.println("[SimplePieChart.paint] Drawing category: " + category + ", angle: " + angle + "°, starting at " + startAngle + "°");
                    
                    // Update start angle
                    startAngle += angle;
                    
                    // Draw legend
                    int legendY = 30 + i * 20;
                    g2d.fillRect(width * 2/3, legendY, 15, 15);
                    g2d.setColor(Color.BLACK);
                    g2d.setFont(font);
                    
                    // Format amount and percentage
                    String amountStr = String.format("¥%.2f", amount);
                    String percentStr = String.format("(%.1f%%)", proportion * 100);
                    
                    g2d.drawString(category + ": " + amountStr + " " + percentStr, width * 2/3 + 20, legendY + 12);
                    
                    i++;
                }
            }
            
            // Draw white center to create donut effect
            g2d.setColor(Color.WHITE);
            int innerSize = pieSize / 3;
            g2d.fillOval(centerX - innerSize/2, centerY - innerSize/2, innerSize, innerSize);
            
            System.out.println("[SimplePieChart.paint] Pie chart drawing complete");
        }
    }
    
    // Simple line chart implementation
    private static class SimpleLineChart extends JPanel {
        private final TreeMap<LocalDate, BigDecimal> data;
        private final Font font;
        private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM-dd");
        
        public SimpleLineChart(TreeMap<LocalDate, BigDecimal> data, Font font) {
            this.data = data;
            this.font = new Font(font.getName(), Font.PLAIN, 11); // Use smaller font
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            System.out.println("[SimpleLineChart] Created line chart with " + data.size() + " data points");
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int width = getWidth() - 40; // Leave margin
            int height = getHeight() - 60; // Leave margin
            int leftMargin = 60; // Left margin for Y-axis labels
            int bottomMargin = 40; // Bottom margin for X-axis labels
            
            if (width < 100 || height < 100) {
                g2d.drawString("Window too small to display chart", 10, 20);
                return; // Too small, don't draw
            }
            
            // If no data, show message
            if (data.isEmpty()) {
                g2d.drawString("No transaction data", width/2 - 50, height/2);
                return;
            }
            
            System.out.println("[SimpleLineChart.paint] Starting to draw line chart, data points: " + data.size());
            
            // Find maximum amount for Y-axis scaling
            BigDecimal maxAmount = BigDecimal.ZERO;
            for (BigDecimal amount : data.values()) {
                if (amount.compareTo(maxAmount) > 0) {
                    maxAmount = amount;
                }
            }
            
            // For aesthetics, round up max value a bit
            double yMax = Math.ceil(maxAmount.doubleValue() * 1.1);
            // Ensure yMax is not zero
            if (yMax == 0) yMax = 10;
            
            // Calculate scale
            double xScale = (double) width / (data.size() - 1 > 0 ? data.size() - 1 : 1);
            double yScale = (double) height / yMax;
            
            // Draw axes
            g2d.setColor(Color.BLACK);
            g2d.drawLine(leftMargin, 20, leftMargin, 20 + height); // Y-axis
            g2d.drawLine(leftMargin, 20 + height, leftMargin + width, 20 + height); // X-axis
            
            // Draw Y-axis ticks and labels
            g2d.setFont(font);
            int numYTicks = 5; // Number of Y-axis ticks
            for (int i = 0; i <= numYTicks; i++) {
                int y = 20 + height - (int)(i * height / numYTicks);
                double value = i * yMax / numYTicks;
                g2d.drawLine(leftMargin - 5, y, leftMargin, y); // Tick mark
                g2d.drawString(String.format("¥%.0f", value), 5, y + 5); // Tick label
            }
            
            // Draw line
            g2d.setColor(new Color(0, 120, 215)); // Blue line
            g2d.setStroke(new BasicStroke(2)); // Set line width
            
            // Collect points
            int[] xPoints = new int[data.size()];
            int[] yPoints = new int[data.size()];
            
            // Draw X-axis ticks and labels, and collect point coordinates
            int i = 0;
            for (Map.Entry<LocalDate, BigDecimal> entry : data.entrySet()) {
                LocalDate date = entry.getKey();
                BigDecimal amount = entry.getValue();
                
                int x = leftMargin + (int)(i * xScale);
                int y = 20 + height - (int)(amount.doubleValue() * yScale);
                
                xPoints[i] = x;
                yPoints[i] = y;
                
                // Draw X-axis tick and label for every point
                String dateLabel = date.format(dateFormatter);
                FontMetrics fm = g2d.getFontMetrics();
                int labelWidth = fm.stringWidth(dateLabel);
                
                g2d.setColor(Color.BLACK);
                g2d.drawLine(x, 20 + height, x, 20 + height + 5); // X-axis tick
                g2d.drawString(dateLabel, x - labelWidth/2, 20 + height + 20); // X-axis date label
                g2d.setColor(new Color(0, 120, 215)); // Restore line color
                
                i++;
            }
            
            // Draw line
            g2d.drawPolyline(xPoints, yPoints, data.size());
            
            // Draw data points
            for (i = 0; i < data.size(); i++) {
                g2d.setColor(new Color(0, 120, 215));
                g2d.fillOval(xPoints[i] - 4, yPoints[i] - 4, 8, 8); // Data point
                g2d.setColor(Color.WHITE);
                g2d.fillOval(xPoints[i] - 2, yPoints[i] - 2, 4, 4); // White center of data point
            }
            
            // Draw amount labels for ALL points
            g2d.setColor(Color.BLACK);
            i = 0;
            for (Map.Entry<LocalDate, BigDecimal> entry : data.entrySet()) {
                BigDecimal amount = entry.getValue();
                int x = xPoints[i];
                int y = yPoints[i];
                
                // Display amount label for every point
                String amountLabel = String.format("¥%.0f", amount);
                FontMetrics fm = g2d.getFontMetrics();
                int labelWidth = fm.stringWidth(amountLabel);
                
                g2d.drawString(amountLabel, x - labelWidth/2, y - 10);
                
                i++;
            }
            
            // Add chart title
            g2d.setFont(new Font(font.getName(), Font.BOLD, 14));
            g2d.drawString("Daily Spending Trend", leftMargin + width/2 - 70, 15);
            
            System.out.println("[SimpleLineChart.paint] Line chart drawing complete");
        }
    }
} 