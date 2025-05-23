package ui;// Import services and mocks
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate; // Added for mock data
import java.util.List;
import javax.swing.*;
import javax.swing.border.Border;

import dto.TransactionData;
import dto.User;
import exception.AuthException;
import exception.TransactionException;
import mock.*;
import real.FinancialTransactionServiceImpl;
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

    // --- User Service ---
    private final UserService userService = new MockUserService();
    private User currentUser; // Current logged in user

    // --- Services (using Mock implementations) ---
    // First instantiate AI service
    private final TransactionAnalysisAlService transactionAnalysisAlService = new MockTransactionAnalysisAlServic();
    
    // Then use the AI service to instantiate the transaction service
    private final FinancialTransactionService financialTransactionService = new FinancialTransactionServiceImpl(transactionAnalysisAlService) {};
    
    // Now connect the AI service to the transaction service for bidirectional relationship
    {
        // Set the transaction service in the AI service
        ((MockTransactionAnalysisAlServic)transactionAnalysisAlService).setTransactionService(financialTransactionService);
    }
    
    // Use real FinancialTransactionServiceImpl to instantiate TransactionQueryServiceImpl
    private final TransactionQueryService transactionQueryService = new TransactionQueryServiceImpl(financialTransactionService);
    
    // Other services can continue to use mock
    private final FinancialHealthAlService financialHealthAlService = new MockFinancialHealthAlService(financialTransactionService);
    private final FinancialInsightsAlService financialInsightsAlService = new MockFinancialInsightsAlService();
    private final PortfolioIntelligenceAlService portfolioIntelligenceAlService = new MockPortfolioIntelligenceAlService();

    // --- User ID (Hardcoded for demo) ---
    private String currentUserId = "user123"; // Will be replaced by logged-in user's ID

    // --- GUI Components ---
    private JPanel mainContentPanel;
    private CardLayout cardLayout;
    private JButton selectedSidebarButton = null;
    private JPanel sidebarPanel;
    private JPanel loginPanel; // Login panel
    private JPanel mainPanel; // Main interface panel

    // --- Shared state for Savings Goal ---
    private BigDecimal currentGlobalSavingsGoal = new BigDecimal("0.00"); // Initial default
    private DashboardPanel dashboardPanelInstance; // To call update method on DashboardPanel

    public DeepManageApp() {
        setTitle("DeepManage - AI Expense Assistant");
        setSize(950, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Initialize login interface
        initLoginPanel();
        
        // Show login interface
        showLoginPanel();
    }
    
    /**
     * Initialize login panel
     */
    private void initLoginPanel() {
        loginPanel = new LoginPanel(userService, user -> {
            // Login success callback
            this.currentUser = user;
            this.currentUserId = user.getUserId();
            
            // Initialize and show main interface
            initMainPanel();
            showMainPanel();
        });
    }
    
    /**
     * Initialize main panel
     */
    private void initMainPanel() {
        mainPanel = new JPanel(new BorderLayout());
        
        createTopBar();
        createSidebar();
        createMainContentArea();

        // Select "Dashboard" initially
        selectSidebarButton((JButton) sidebarPanel.getComponent(0)); // Changed to Dashboard
        cardLayout.show(mainContentPanel, "Dashboard");
    }
    
    /**
     * Show login interface
     */
    private void showLoginPanel() {
        getContentPane().removeAll();
        getContentPane().add(loginPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
    
    /**
     * Show main interface
     */
    private void showMainPanel() {
        getContentPane().removeAll();
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        setTitle("DeepManage - Welcome " + currentUser.getUsername());
        revalidate();
        repaint();
    }

    private void createTopBar() {
        JPanel topBarPanel = new JPanel(new BorderLayout());
        topBarPanel.setBackground(COLOR_TOP_BAR);
        topBarPanel.setPreferredSize(new Dimension(getWidth(), 40));

        JLabel titleLabel = new JLabel(" DeepManage - " + (currentUser != null ? currentUser.getUsername() : ""));
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        
        // Account management button
        JButton accountButton = new JButton("Account");
        accountButton.addActionListener(e -> showAccountDialog());
        
        // Logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            currentUser = null;
            showLoginPanel();
        });
        
        rightPanel.add(accountButton);
        rightPanel.add(logoutButton);

        topBarPanel.add(titleLabel, BorderLayout.WEST);
        topBarPanel.add(rightPanel, BorderLayout.EAST);

        mainPanel.add(topBarPanel, BorderLayout.NORTH);
    }
    
    /**
     * Show account management dialog
     */
    private void showAccountDialog() {
        AccountDialog dialog = new AccountDialog(this, currentUser, userService, () -> {
            // Account deletion success callback
            currentUser = null;
            showLoginPanel();
        });
        dialog.setVisible(true);
    }

    private void createSidebar() {
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(COLOR_SIDEBAR_BACKGROUND);
        sidebarPanel.setPreferredSize(new Dimension(180, getHeight()));

        String[] sidebarItems = {
                "Dashboard", "Transactions", "Financial Health", "Financial Planning",
                "Categorization", "Insights", "Portfolio"
        };
        String[] panelNames = { // Must match names used in createMainContentArea
                "Dashboard", "Transactions", "Financial Health", "Financial Planning",
                "Categorization", "Insights", "Portfolio"
        };

        for (int i = 0; i < sidebarItems.length; i++) {
            JButton button = createSidebarButton(sidebarItems[i]);
            final String panelName = panelNames[i];
            final boolean isPlaceholder = panelName.equals("Reports") || panelName.equals("Settings");

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
        mainPanel.add(sidebarPanel, BorderLayout.WEST);
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

        this.dashboardPanelInstance = new DashboardPanel(financialTransactionService, currentUserId, this); // Pass `this` for callback, and store instance
        mainContentPanel.add(this.dashboardPanelInstance, "Dashboard");
        mainContentPanel.add(new Module1Panel(financialTransactionService, this), "Transactions");
        mainContentPanel.add(new Module2Panel(financialHealthAlService, currentUserId, this), "Financial Health"); // Pass `this` for callback
        mainContentPanel.add(new FinancialPlanningPanel(financialHealthAlService, currentUserId), "Financial Planning");
        mainContentPanel.add(new Module3Panel(transactionQueryService, transactionAnalysisAlService, currentUserId), "Categorization");
        mainContentPanel.add(new Module4Panel(financialInsightsAlService, currentUserId), "Insights");
        mainContentPanel.add(new Module5Panel(portfolioIntelligenceAlService), "Portfolio");

        mainPanel.add(mainContentPanel, BorderLayout.CENTER);
    }

    // --- Method to update and propagate savings goal ---
    public void updateUserSavingsGoal(BigDecimal newGoal) {
        this.currentGlobalSavingsGoal = (newGoal != null) ? newGoal : BigDecimal.ZERO;
        if (this.dashboardPanelInstance != null) {
            this.dashboardPanelInstance.setSavingsGoalDisplay(this.currentGlobalSavingsGoal);
        }
        System.out.println("[DeepManageApp] Savings goal updated to: " + this.currentGlobalSavingsGoal);
    }

    // --- Method for Dashboard to initially fetch the goal (optional, if needed on Dashboard's own refresh) ---
    public BigDecimal getCurrentGlobalSavingsGoal() {
        return this.currentGlobalSavingsGoal;
    }

    // --- Method to tell DashboardPanel to refresh its data ---
    public void refreshDashboardData() {
        if (dashboardPanelInstance != null) {
            System.out.println("[DeepManageApp] Requesting Dashboard refresh.");
            dashboardPanelInstance.loadAndDisplayCharts(); // Call existing method on dashboard
        }
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
