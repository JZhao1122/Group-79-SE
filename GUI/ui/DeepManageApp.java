package ui;// Import services and mocks
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate; // Added for mock data
import java.util.List;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.ColorUIResource;

import dto.TransactionData;
import dto.User;
import exception.AuthException;
import exception.TransactionException;
import mock.*;
import real.FinancialTransactionServiceImpl;
import service.*;

public class DeepManageApp extends JFrame {

    // --- Enhanced Color Palette (Modern & Elegant) ---
    public static final Color COLOR_PRIMARY = new Color(0x2E86AB); // Deep Blue
    public static final Color COLOR_PRIMARY_LIGHT = new Color(0x1B4F72); // Changed to darker blue for better contrast
    public static final Color COLOR_PRIMARY_DARK = new Color(0x1E5F7A); // Dark Blue
    public static final Color COLOR_TOP_BAR = new Color(0x2E86AB); // Primary Deep Blue
    public static final Color COLOR_SIDEBAR_BACKGROUND = new Color(0x1A1A2E); // Dark Navy
    public static final Color COLOR_SIDEBAR_TEXT = new Color(0xE8E8E8); // Light Gray
    public static final Color COLOR_SIDEBAR_SELECTION = new Color(0x4DABDB); // Light Blue for selection
    public static final Color COLOR_SIDEBAR_HOVER = new Color(0x2E4057); // Hover effect
    public static final Color COLOR_MAIN_BACKGROUND = new Color(0xF8F9FA); // Very Light Gray
    public static final Color COLOR_PANEL_BACKGROUND = Color.WHITE; // Pure White for panels
    public static final Color COLOR_ACCENT = new Color(0x00B894); // Green Accent
    public static final Color COLOR_WARNING = new Color(0xFD79A8); // Pink Warning
    public static final Color COLOR_ERROR = new Color(0xE84142); // Red Error
    public static final Color COLOR_SUCCESS = new Color(0x27AE60); // Changed to darker green for better contrast
    public static final Color COLOR_TEXT_PRIMARY = new Color(0x2D3436); // Dark Gray
    public static final Color COLOR_TEXT_SECONDARY = new Color(0x636E72); // Medium Gray
    public static final Color COLOR_BORDER = new Color(0xDDD); // Light Border
    public static final Color COLOR_BUTTON_PRIMARY = new Color(0x1B4F72); // Changed to darker blue for better contrast
    public static final Color COLOR_BUTTON_SECONDARY = new Color(0x636E72); // Secondary Button
    
    // Enhanced borders with padding and styling
    public static final Border SIDEBAR_BUTTON_BORDER = BorderFactory.createEmptyBorder(12, 20, 12, 20);
    public static final Border PANEL_BORDER = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(COLOR_BORDER, 1),
        BorderFactory.createEmptyBorder(15, 15, 15, 15)
    );

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
        topBarPanel.setPreferredSize(new Dimension(getWidth(), 50)); // 增加高度
        topBarPanel.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15)); // 添加内边距

        // 标题和用户信息
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("💰 DeepManage");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel userLabel = new JLabel(" - " + (currentUser != null ? currentUser.getUsername() : ""));
        userLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        userLabel.setForeground(new Color(0xE8E8E8));
        
        leftPanel.add(titleLabel);
        leftPanel.add(userLabel);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);
        
        // 账户管理按钮
        JButton accountButton = createStyledButton("👤 Account", false);
        accountButton.addActionListener(e -> showAccountDialog());
        
        // 注销按钮
        JButton logoutButton = createStyledButton("⚡ Logout", true);
        logoutButton.addActionListener(e -> {
            currentUser = null;
            showLoginPanel();
        });
        
        rightPanel.add(accountButton);
        rightPanel.add(logoutButton);

        topBarPanel.add(leftPanel, BorderLayout.WEST);
        topBarPanel.add(rightPanel, BorderLayout.EAST);

        mainPanel.add(topBarPanel, BorderLayout.NORTH);
    }
    
    /**
     * 创建样式化按钮
     */
    private JButton createStyledButton(String text, boolean isWarning) {
        Color bgColor = isWarning ? COLOR_WARNING : COLOR_PRIMARY_LIGHT;
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                if (getModel().isPressed()) {
                    g.setColor(new Color(
                        Math.max(0, bgColor.getRed() - 40),
                        Math.max(0, bgColor.getGreen() - 40),
                        Math.max(0, bgColor.getBlue() - 40)
                    ));
                } else if (getModel().isRollover()) {
                    g.setColor(isWarning ? 
                        new Color(bgColor.getRed() - 20, bgColor.getGreen() - 20, bgColor.getBlue() - 20) :
                        COLOR_PRIMARY_DARK);
                } else {
                    g.setColor(bgColor);
                }
                g.fillRect(0, 0, getWidth(), getHeight());
                
                // Draw text
                g.setColor(Color.WHITE);
                FontMetrics fm = g.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g.drawString(getText(), x, y);
            }
        };
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
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
        sidebarPanel.setPreferredSize(new Dimension(220, getHeight())); // 增加宽度
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0)); // 添加顶部和底部边距

        String[] sidebarItems = {
                "📊 Dashboard", "💰 Transactions", "❤️ Financial Health", "📈 Financial Planning",
                "🏷️ Categorization", "💡 Insights", "📋 Portfolio"
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
            sidebarPanel.add(Box.createVerticalStrut(5)); // 添加按钮间距
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
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45)); // 增加高度
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // 添加悬停效果
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (button != selectedSidebarButton) {
                    button.setBackground(COLOR_SIDEBAR_HOVER);
                }
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (button != selectedSidebarButton) {
                    button.setBackground(COLOR_SIDEBAR_BACKGROUND);
                }
            }
        });
        
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
            // 设置UIManager属性，确保自定义按钮样式不被覆盖
            UIManager.put("Button.focus", new ColorUIResource(new Color(0, 0, 0, 0)));
            UIManager.put("Button.select", new ColorUIResource(new Color(0, 0, 0, 0)));
            UIManager.put("Button.background", new ColorUIResource(new Color(0, 0, 0, 0)));
            UIManager.put("Button.opaque", true);
            UIManager.put("Button.contentAreaFilled", true);
            
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
