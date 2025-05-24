package ui;

import dto.User;
import exception.AuthException;
import service.UserService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Account Management Dialog
 * Provides account information viewing and account deletion functionality
 */
public class AccountDialog extends JDialog {
    private final User currentUser;
    private final UserService userService;
    private final AccountActionListener actionListener;
    
    // UI Components
    private final JPasswordField passwordField = new JPasswordField(15);
    private final JButton deleteButton = new JButton("Delete Account");
    private final JLabel statusLabel = new JLabel(" ");
    
    /**
     * Account action listener interface
     */
    public interface AccountActionListener {
        void onAccountDeleted();
    }
    
    /**
     * Create account management dialog
     * @param parent Parent window
     * @param user Current user
     * @param userService User service
     * @param listener Account action listener
     */
    public AccountDialog(JFrame parent, User user, UserService userService, AccountActionListener listener) {
        super(parent, "Account Management", true);
        this.currentUser = user;
        this.userService = userService;
        this.actionListener = listener;
        
        initComponents();
        setupLayout();
        setupListeners();
        
        // Set dialog properties
        setSize(450, 350);
        setLocationRelativeTo(parent);
        setResizable(false);
    }
    
    private void initComponents() {
        // Set status label style
        statusLabel.setForeground(Color.RED);
        
        // Style the delete button to make it more visible
        deleteButton.setPreferredSize(new Dimension(150, 30));
        deleteButton.setFont(deleteButton.getFont().deriveFont(Font.BOLD));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        
        // Account information panel
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("Account Information"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // User ID
        gbc.gridx = 0;
        gbc.gridy = 0;
        infoPanel.add(new JLabel("User ID:"), gbc);
        gbc.gridx = 1;
        JLabel userIdLabel = new JLabel(currentUser.getUserId());
        userIdLabel.setFont(userIdLabel.getFont().deriveFont(Font.BOLD));
        infoPanel.add(userIdLabel, gbc);
        
        // Username
        gbc.gridx = 0;
        gbc.gridy = 1;
        infoPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        JLabel usernameLabel = new JLabel(currentUser.getUsername());
        usernameLabel.setFont(usernameLabel.getFont().deriveFont(Font.BOLD));
        infoPanel.add(usernameLabel, gbc);
        
        // Email
        gbc.gridx = 0;
        gbc.gridy = 2;
        infoPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        JLabel emailLabel = new JLabel(currentUser.getEmail());
        infoPanel.add(emailLabel, gbc);
        
        // Delete account panel
        JPanel deletePanel = new JPanel();
        deletePanel.setLayout(new BoxLayout(deletePanel, BoxLayout.Y_AXIS));
        deletePanel.setBorder(BorderFactory.createTitledBorder("Delete Account"));
        
        // Instruction panel (separate from the border)
        JPanel instructionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel instructionLabel = new JLabel("Enter password to confirm deletion:");
        instructionPanel.add(instructionLabel);
        deletePanel.add(instructionPanel);
        
        // Password field panel
        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        passwordPanel.add(passwordField);
        deletePanel.add(passwordPanel);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        deleteButton.setBackground(new Color(220, 53, 69));
        deleteButton.setForeground(Color.WHITE);
        buttonPanel.add(deleteButton);
        deletePanel.add(buttonPanel);
        
        // Warning panel
        JPanel warningPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        // Create a formatted warning label with proper word wrapping
        JTextArea warningText = new JTextArea(
                "Warning: Account deletion cannot be undone! " +
                "All associated data will be permanently deleted!");
        warningText.setForeground(Color.RED);
        warningText.setBackground(deletePanel.getBackground());
        warningText.setEditable(false);
        warningText.setWrapStyleWord(true);
        warningText.setLineWrap(true);
        warningText.setFont(new Font("SansSerif", Font.BOLD, 12));
        warningText.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        warningText.setPreferredSize(new Dimension(400, 40));
        warningPanel.add(warningText);
        deletePanel.add(warningPanel);
        
        // Status panel
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        statusPanel.add(statusLabel);
        deletePanel.add(statusPanel);
        
        // Add panels to dialog
        add(infoPanel, BorderLayout.NORTH);
        add(deletePanel, BorderLayout.CENTER);
        
        // Bottom button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        bottomPanel.add(closeButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void setupListeners() {
        // Delete button event
        deleteButton.addActionListener(e -> {
            // Confirmation dialog
            int option = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete your account? This action cannot be undone!",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            
            if (option == JOptionPane.YES_OPTION) {
                // Clear previous status
                statusLabel.setText("");
                
                try {
                    String password = new String(passwordField.getPassword());
                    
                    // Ensure password is not empty
                    if (password.trim().isEmpty()) {
                        statusLabel.setText("Please enter your password");
                        return;
                    }
                    
                    // Display processing status
                    statusLabel.setText("Processing...");
                    deleteButton.setEnabled(false);
                    
                    // Try to delete user
                    boolean success = userService.deleteUser(currentUser.getUserId(), password);
                    if (success) {
                        JOptionPane.showMessageDialog(
                                this,
                                "Account successfully deleted!",
                                "Deletion Successful",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                        
                        // Notify listener
                        if (actionListener != null) {
                            actionListener.onAccountDeleted();
                        }
                        
                        // Close dialog
                        dispose();
                    }
                } catch (AuthException ex) {
                    statusLabel.setText(ex.getMessage());
                    deleteButton.setEnabled(true);
                    System.err.println("[AccountDialog] Account deletion failed: " + ex.getMessage());
                } catch (Exception ex) {
                    statusLabel.setText("Operation failed: " + ex.getMessage());
                    deleteButton.setEnabled(true);
                    System.err.println("[AccountDialog] Unexpected error: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });
    }
} 