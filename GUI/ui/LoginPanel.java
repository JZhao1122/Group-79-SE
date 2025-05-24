package ui;

import dto.User;
import exception.AuthException;
import service.UserService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Login and Registration Panel
 */
public class LoginPanel extends JPanel {
    private final UserService userService;
    private final LoginListener loginListener;
    
    // UI Components
    private final JTextField usernameField = new JTextField(15);
    private final JPasswordField passwordField = new JPasswordField(15);
    private final JTextField emailField = new JTextField(15);
    private final JButton loginButton = new JButton("🔐 Login");
    private final JButton registerButton = new JButton("📝 Register");
    private final JButton switchButton = new JButton("Switch to Register");
    private final JLabel statusLabel = new JLabel(" ");
    
    // Labels for form fields
    private final JLabel usernameLabel = new JLabel("👤 Username:");
    private final JLabel passwordLabel = new JLabel("🔒 Password:");
    private final JLabel emailLabel = new JLabel("📧 Email:");
    
    // Status flag
    private boolean isLoginMode = true; // true for login mode, false for register mode
    
    /**
     * Login callback interface
     */
    public interface LoginListener {
        void onLoginSuccess(User user);
    }
    
    public LoginPanel(UserService userService, LoginListener loginListener) {
        this.userService = userService;
        this.loginListener = loginListener;
        initComponents();
        setupLayout();
        setupListeners();
    }
    
    private void initComponents() {
        // Set text field hints and styling
        usernameField.setToolTipText("Enter username");
        passwordField.setToolTipText("Enter password");
        emailField.setToolTipText("Enter email address");
        
        // Style text fields
        styleTextField(usernameField);
        styleTextField(passwordField);
        styleTextField(emailField);
        
        // Style labels
        usernameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        passwordLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        emailLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        
        usernameLabel.setForeground(new Color(0x2D3436));
        passwordLabel.setForeground(new Color(0x2D3436));
        emailLabel.setForeground(new Color(0x2D3436));
        
        // Set status label style
        statusLabel.setForeground(new Color(0xE84142));
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        
        // Style buttons
        styleButton(loginButton, new Color(0x1B4F72), Color.WHITE);
        styleButton(registerButton, new Color(0x27AE60), Color.WHITE);
        styleButton(switchButton, new Color(0x4A4A4A), Color.WHITE);
        
        // Initial setup
        emailField.setVisible(false); // Initially hide email field (login mode)
        emailLabel.setVisible(false); // Initially hide email label (login mode)
    }
    
    private void styleTextField(JTextField field) {
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xDDD), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setBackground(Color.WHITE);
        field.setForeground(new Color(0x2D3436));
    }
    
    private void styleButton(JButton button, Color bgColor, Color textColor) {
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setForeground(textColor);
        button.setBackground(bgColor);
        button.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Override paintComponent to ensure our styling is visible
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                JButton btn = (JButton) c;
                if (btn.getModel().isPressed()) {
                    g.setColor(new Color(
                        Math.max(0, bgColor.getRed() - 40),
                        Math.max(0, bgColor.getGreen() - 40),
                        Math.max(0, bgColor.getBlue() - 40)
                    ));
                } else if (btn.getModel().isRollover()) {
                    g.setColor(new Color(
                        Math.max(0, bgColor.getRed() - 20),
                        Math.max(0, bgColor.getGreen() - 20),
                        Math.max(0, bgColor.getBlue() - 20)
                    ));
                } else {
                    g.setColor(bgColor);
                }
                g.fillRect(0, 0, btn.getWidth(), btn.getHeight());
                
                // Draw text
                g.setColor(textColor);
                FontMetrics fm = g.getFontMetrics();
                int x = (btn.getWidth() - fm.stringWidth(btn.getText())) / 2;
                int y = (btn.getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g.drawString(btn.getText(), x, y);
            }
        });
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        setBackground(new Color(0xF8F9FA));
        
        // Main container with white background
        JPanel mainContainer = new JPanel(new BorderLayout(20, 20));
        mainContainer.setBackground(Color.WHITE);
        mainContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xDDD), 1),
            BorderFactory.createEmptyBorder(40, 40, 40, 40)
        ));
        
        // Title panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(Color.WHITE);
        JLabel titleLabel = new JLabel("💰 DeepManage");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setForeground(new Color(0x2E86AB));
        
        JLabel subtitleLabel = new JLabel("AI-Powered Financial Management System");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(0x636E72));
        
        JPanel titleContainer = new JPanel(new GridLayout(2, 1, 0, 5));
        titleContainer.setBackground(Color.WHITE);
        titleContainer.add(titleLabel);
        titleContainer.add(subtitleLabel);
        titlePanel.add(titleContainer);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Username row
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(usernameLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(usernameField, gbc);
        
        // Password row
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(passwordField, gbc);
        
        // Email row (only shown for registration)
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        formPanel.add(emailLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(emailField, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        formPanel.add(buttonPanel, gbc);
        
        // Switch button and status label
        JPanel bottomPanel = new JPanel(new BorderLayout(0, 10));
        bottomPanel.setBackground(Color.WHITE);
        
        JPanel switchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        switchPanel.setBackground(Color.WHITE);
        switchPanel.add(switchButton);
        
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        statusPanel.setBackground(Color.WHITE);
        statusPanel.add(statusLabel);
        
        bottomPanel.add(switchPanel, BorderLayout.NORTH);
        bottomPanel.add(statusPanel, BorderLayout.SOUTH);
        
        // Add all panels to main container
        mainContainer.add(titlePanel, BorderLayout.NORTH);
        mainContainer.add(formPanel, BorderLayout.CENTER);
        mainContainer.add(bottomPanel, BorderLayout.SOUTH);
        
        // Add main container to this panel
        add(mainContainer, BorderLayout.CENTER);
        
        // Initial state
        updateUIForMode();
    }
    
    private void setupListeners() {
        // Login button event
        loginButton.addActionListener(e -> {
            try {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword());
                
                User user = userService.login(username, password);
                statusLabel.setText("✅ Login successful!");
                statusLabel.setForeground(new Color(0x00B894)); // Green
                
                // Notify listener of successful login
                if (loginListener != null) {
                    loginListener.onLoginSuccess(user);
                }
            } catch (AuthException ex) {
                statusLabel.setText("❌ Login failed: " + ex.getMessage());
                statusLabel.setForeground(new Color(0xE84142)); // Red
            }
        });
        
        // Register button event
        registerButton.addActionListener(e -> {
            try {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword());
                String email = emailField.getText().trim();
                
                User user = userService.register(username, password, email);
                statusLabel.setText("✅ Registration successful! Please login");
                statusLabel.setForeground(new Color(0x00B894)); // Green
                
                // Switch to login mode
                isLoginMode = true;
                updateUIForMode();
            } catch (AuthException ex) {
                statusLabel.setText("❌ Registration failed: " + ex.getMessage());
                statusLabel.setForeground(new Color(0xE84142)); // Red
            }
        });
        
        // Switch button event
        switchButton.addActionListener(e -> {
            isLoginMode = !isLoginMode;
            updateUIForMode();
        });
    }
    
    /**
     * Update UI based on current mode
     */
    private void updateUIForMode() {
        if (isLoginMode) {
            // Login mode
            switchButton.setText("Switch to Register");
            loginButton.setVisible(true);
            registerButton.setVisible(false);
            emailField.setVisible(false);
            emailLabel.setVisible(false);
            statusLabel.setText(" ");
        } else {
            // Register mode
            switchButton.setText("Switch to Login");
            loginButton.setVisible(false);
            registerButton.setVisible(true);
            emailField.setVisible(true);
            emailLabel.setVisible(true);
            statusLabel.setText(" ");
        }
        
        // Clear fields
        usernameField.setText("");
        passwordField.setText("");
        emailField.setText("");
    }
} 