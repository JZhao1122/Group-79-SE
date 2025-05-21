package ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.imageio.ImageIO;
import java.io.IOException;

import mock.*;
import real.FinancialTransactionServiceImpl;
import service.*;

public class DeepManageApp extends JFrame {

    // --- Color Palette ---
    public static final Color COLOR_TOP_BAR = new Color(0x2196F3);  // Modern blue
    public static final Color COLOR_SIDEBAR_BACKGROUND = new Color(0x1A237E);  // Deep blue
    public static final Color COLOR_SIDEBAR_TEXT = new Color(0xFFFFFF);
    public static final Color COLOR_SIDEBAR_SELECTION = new Color(0x3949AB);  // Indigo
    public static final Color COLOR_MAIN_BACKGROUND = new Color(0xF5F5F5);  // Light gray
    public static final Color COLOR_BUTTON_TEXT = new Color(0x212121);  // Dark gray
    public static final Color COLOR_ACCENT = new Color(0x2196F3);  // Blue accent
    public static final Color COLOR_SUCCESS = new Color(0x4CAF50);  // Green
    public static final Color COLOR_ERROR = new Color(0xF44336);  // Red
    public static final Color COLOR_WARNING = new Color(0xFFC107);  // Amber
    public static final Color COLOR_INFO = new Color(0x2196F3);  // Blue

    // --- Font Settings ---
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_SUBHEADER = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_NORMAL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.PLAIN, 13);

    // --- Border Settings ---
    public static final Border SIDEBAR_BUTTON_BORDER = BorderFactory.createEmptyBorder(12, 20, 12, 20);
    public static final Border MAIN_PANEL_BORDER = BorderFactory.createEmptyBorder(25, 25, 25, 25);
    public static final Border MAIN_PANEL_HEADER_BORDER = BorderFactory.createEmptyBorder(0, 0, 20, 0);
    public static final Border MAIN_PANEL_CONTENT_BORDER = BorderFactory.createEmptyBorder(20, 0, 0, 0);
    public static final Border MAIN_PANEL_FOOTER_BORDER = BorderFactory.createEmptyBorder(20, 0, 0, 0);
    public static final Border ROUNDED_BORDER = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(0xE0E0E0), 1),
        BorderFactory.createEmptyBorder(8, 12, 8, 12)
    );

    // --- Services ---
    private final MockTransactionAnalysisAlService transactionAnalysisAlService = new MockTransactionAnalysisAlService();
    private final FinancialTransactionService financialTransactionService;
    private final TransactionQueryService transactionQueryService;
    private final FinancialHealthAlService financialHealthAlService;
    private final FinancialInsightsAlService financialInsightsAlService;
    private final PortfolioIntelligenceAlService portfolioIntelligenceAlService;

    // --- User ID ---
    private final String currentUserId = "user123";

    // --- GUI Components ---
    private JPanel mainContentPanel;
    private CardLayout cardLayout;
    private JButton selectedSidebarButton = null;
    private JPanel sidebarPanel;
    private Image decorativeIcon;

    public DeepManageApp() {
        // 构建依赖链，避免循环依赖
        this.financialTransactionService = new FinancialTransactionServiceImpl(transactionAnalysisAlService);
        this.transactionAnalysisAlService.setFinancialTransactionService(financialTransactionService);

        this.transactionQueryService = new TransactionQueryServiceImpl(financialTransactionService);
        this.financialHealthAlService = new MockFinancialHealthAlService(financialTransactionService);
        this.financialInsightsAlService = new MockFinancialInsightsAlService(financialTransactionService);
        this.portfolioIntelligenceAlService = new MockPortfolioIntelligenceAlService(financialTransactionService, "mock");

        setTitle("DeepManage - AI Expense Assistant");
        setSize(950, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        try {
            decorativeIcon = ImageIO.read(getClass().getResource("/ui/finance_ai_decor.png"));
        } catch (IOException | IllegalArgumentException e) {
            decorativeIcon = null;
        }

        createTopBar();
        createSidebar();
        createMainContentArea();

        selectSidebarButton((JButton) sidebarPanel.getComponent(1));
        cardLayout.show(mainContentPanel, "Transactions");
    }

    private void createTopBar() {
        JPanel topBarPanel = new JPanel(new BorderLayout());
        topBarPanel.setBackground(COLOR_TOP_BAR);
        topBarPanel.setPreferredSize(new Dimension(getWidth(), 50));

        JLabel titleLabel = new JLabel(" DeepManage");
        titleLabel.setFont(FONT_HEADER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        titleLabel.setOpaque(false);
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        JLabel iconLabel = new JLabel("💡");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        iconLabel.setForeground(Color.WHITE);

        topBarPanel.add(titlePanel, BorderLayout.WEST);
        topBarPanel.add(iconLabel, BorderLayout.EAST);

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
        String[] panelNames = sidebarItems;

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
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, button.getPreferredSize().height + 15));
        button.setFont(FONT_BUTTON);
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button != selectedSidebarButton) {
                    button.setBackground(COLOR_SIDEBAR_SELECTION);
                }
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
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
        mainContentPanel.setOpaque(true);
        mainContentPanel.setBackground(Color.WHITE);

        mainContentPanel.add(new Module1Panel(financialTransactionService), "Transactions");
        mainContentPanel.add(new Module2Panel(financialHealthAlService, currentUserId), "Financial Health");
        mainContentPanel.add(new Module3Panel(transactionQueryService, transactionAnalysisAlService, currentUserId), "Categorization");
        mainContentPanel.add(new Module4Panel(financialInsightsAlService, currentUserId), "Insights");
        mainContentPanel.add(new Module5Panel(portfolioIntelligenceAlService), "Portfolio");

        add(mainContentPanel, BorderLayout.CENTER);
    }

    public static void showInfoDialog(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showErrorDialog(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

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
