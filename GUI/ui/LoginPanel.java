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
    private final JButton loginButton = new JButton("Login");
    private final JButton registerButton = new JButton("Register");
    private final JButton switchButton = new JButton("Switch to Register");
    private final JLabel statusLabel = new JLabel(" ");
    
    // Labels for form fields
    private final JLabel usernameLabel = new JLabel("Username:");
    private final JLabel passwordLabel = new JLabel("Password:");
    private final JLabel emailLabel = new JLabel("Email:");
    
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
        // Set text field hints
        usernameField.setToolTipText("Enter username");
        passwordField.setToolTipText("Enter password");
        emailField.setToolTipText("Enter email address");
        
        // Set status label style
        statusLabel.setForeground(Color.RED);
        
        // Initial setup
        emailField.setVisible(false); // Initially hide email field (login mode)
        emailLabel.setVisible(false); // Initially hide email label (login mode)
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);
        
        // Title panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(Color.WHITE);
        JLabel titleLabel = new JLabel("DeepManage - Financial Management System");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titlePanel.add(titleLabel);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Username row
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(usernameLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);
        
        // Password row
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);
        
        // Email row (only shown for registration)
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(emailLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);
        
        // Switch button and status label
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.add(switchButton, BorderLayout.NORTH);
        bottomPanel.add(statusLabel, BorderLayout.SOUTH);
        
        // Add all panels to main panel
        add(titlePanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        
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
                statusLabel.setText("Login successful!");
                statusLabel.setForeground(new Color(0, 128, 0)); // Green
                
                // Notify listener of successful login
                if (loginListener != null) {
                    loginListener.onLoginSuccess(user);
                }
            } catch (AuthException ex) {
                statusLabel.setText("Login failed: " + ex.getMessage());
                statusLabel.setForeground(Color.RED);
            }
        });
        
        // Register button event
        registerButton.addActionListener(e -> {
            try {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword());
                String email = emailField.getText().trim();
                
                User user = userService.register(username, password, email);
                statusLabel.setText("Registration successful! Please login");
                statusLabel.setForeground(new Color(0, 128, 0)); // Green
                
                // Switch to login mode
                isLoginMode = true;
                updateUIForMode();
            } catch (AuthException ex) {
                statusLabel.setText("Registration failed: " + ex.getMessage());
                statusLabel.setForeground(Color.RED);
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