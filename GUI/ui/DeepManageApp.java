package ui;// Import services and mocks
import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import mock.*;
import service.*;

public class DeepManageApp extends JFrame {

    // --- Color Palette (Centralized for UI consistency) ---
    public static final Color COLOR_TOP_BAR = new Color(0x5DADE2); // Light Blueish
    public static final Color COLOR_SIDEBAR_BACKGROUND = new Color(0x2C3E50); // Dark Blue/Gray
    public static final Color COLOR_SIDEBAR_TEXT = Color.WHITE;
    public static final Color COLOR_SIDEBAR_SELECTION = new Color(0x3498DB); // Brighter Blue for selection
    public static final Color COLOR_MAIN_BACKGROUND = Color.WHITE; // White or new Color(0xF5F5F5) for light gray
    public static final Color COLOR_BUTTON_TEXT = Color.BLACK;
    public static final Border SIDEBAR_BUTTON_BORDER = BorderFactory.createEmptyBorder(10, 15, 10, 15); // Padding

    // --- Services (using Mock implementations) ---
    // Instantiate mock services directly here
    private final FinancialTransactionService financialTransactionService = new FinancialTransactionServiceImpl();
    private final FinancialHealthAlService financialHealthAlService = new MockFinancialHealthAlService();
    private final TransactionAnalysisAlService transactionAnalysisAlService = new MockTransactionAnalysisAlServic();
    private final TransactionQueryService transactionQueryService = new MockTransactionQueryService();
    private final FinancialInsightsAlService financialInsightsAlService = new MockFinancialInsightsAlService();
    private final PortfolioIntelligenceAlService portfolioIntelligenceAlService = new MockPortfolioIntelligenceAlService();

    // --- User ID (Hardcoded for demo) ---
    private final String currentUserId = "user123";

    // --- GUI Components ---
    private JPanel mainContentPanel;
    private CardLayout cardLayout;
    private JButton selectedSidebarButton = null;
    private JPanel sidebarPanel;

    public DeepManageApp() {
        setTitle("DeepManage - AI Expense Assistant");
        setSize(950, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        createTopBar();
        createSidebar();
        createMainContentArea();

        // Select "Transactions" initially
        selectSidebarButton((JButton) sidebarPanel.getComponent(1)); // Index 1 is Transactions
        cardLayout.show(mainContentPanel, "Transactions");
    }

    private void createTopBar() {
        JPanel topBarPanel = new JPanel(new BorderLayout());
        topBarPanel.setBackground(COLOR_TOP_BAR);
        topBarPanel.setPreferredSize(new Dimension(getWidth(), 40));

        JLabel titleLabel = new JLabel(" DeepManage");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        JPanel iconsPanel = new JPanel(); // Placeholder
        iconsPanel.setOpaque(false);

        topBarPanel.add(titleLabel, BorderLayout.WEST);
        topBarPanel.add(iconsPanel, BorderLayout.EAST);

        add(topBarPanel, BorderLayout.NORTH);
    }

    private void createSidebar() {
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(COLOR_SIDEBAR_BACKGROUND);
        sidebarPanel.setPreferredSize(new Dimension(180, getHeight()));

        String[] sidebarItems = {
                "Dashboard", "Transactions", "Financial Health", "Categorization",
                "Insights", "Portfolio", "Reports", "Settings"
        };
        String[] panelNames = { // Must match names used in createMainContentArea
                "Dashboard", "Transactions", "Financial Health", "Categorization",
                "Insights", "Portfolio", "Reports", "Settings"
        };

        for (int i = 0; i < sidebarItems.length; i++) {
            JButton button = createSidebarButton(sidebarItems[i]);
            final String panelName = panelNames[i];
            final boolean isPlaceholder = panelName.equals("Dashboard") || panelName.equals("Reports") || panelName.equals("Settings");

            if (!isPlaceholder) {
                button.addActionListener(e -> {
                    selectSidebarButton((JButton) e.getSource());
                    cardLayout.show(mainContentPanel, panelName);
                });
            } else {
                button.setEnabled(false);
                button.setToolTipText("Feature not implemented in demo");
            }
            sidebarPanel.add(button);
        }

        sidebarPanel.add(Box.createVerticalGlue());
        add(sidebarPanel, BorderLayout.WEST);
    }

    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setForeground(COLOR_SIDEBAR_TEXT);
        button.setBackground(COLOR_SIDEBAR_BACKGROUND);
        button.setBorder(SIDEBAR_BUTTON_BORDER);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, button.getPreferredSize().height + 10));
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        return button;
    }

    private void selectSidebarButton(JButton button) {
        if (selectedSidebarButton != null) {
            selectedSidebarButton.setBackground(COLOR_SIDEBAR_BACKGROUND);
        }
        selectedSidebarButton = button;
        selectedSidebarButton.setBackground(COLOR_SIDEBAR_SELECTION);
    }

    private void createMainContentArea() {
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(COLOR_MAIN_BACKGROUND);

        // Instantiate UI panels and add them to CardLayout
        // Pass required services and user ID
        mainContentPanel.add(new Module1Panel(financialTransactionService), "Transactions");
        mainContentPanel.add(new Module2Panel(financialHealthAlService, currentUserId), "Financial Health");
        mainContentPanel.add(new Module3Panel(transactionQueryService, transactionAnalysisAlService, currentUserId), "Categorization");
        mainContentPanel.add(new Module4Panel(financialInsightsAlService, currentUserId), "Insights");
        mainContentPanel.add(new Module5Panel(portfolioIntelligenceAlService), "Portfolio");

        // Add placeholder panels if desired
        // JPanel placeholderPanel = new JPanel();
        // placeholderPanel.add(new JLabel("Dashboard Feature Coming Soon"));
        // placeholderPanel.setBackground(COLOR_MAIN_BACKGROUND);
        // mainContentPanel.add(placeholderPanel, "Dashboard");
        // mainContentPanel.add(new JPanel(), "Reports"); // Empty placeholders
        // mainContentPanel.add(new JPanel(), "Settings");

        add(mainContentPanel, BorderLayout.CENTER);
    }

    // --- Helper Methods (Optional: Dialog wrappers) ---
    // These could be static methods in a utility class too
    public static void showInfoDialog(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }
    public static void showErrorDialog(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }


    // --- Main Method ---
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Couldn't set Look and Feel: " + e);
        }

        SwingUtilities.invokeLater(() -> {
            DeepManageApp app = new DeepManageApp();
            app.setVisible(true);
        });
    }
}
