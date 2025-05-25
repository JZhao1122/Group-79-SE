package ui;

import dto.TransactionDisplayData;
import exception.AlException;
import exception.QueryException;
import exception.TransactionException;
import service.TransactionAnalysisAlService;
import service.TransactionQueryService;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class Module3Panel extends JPanel {

    private final TransactionQueryService transactionQueryService;
    private final TransactionAnalysisAlService transactionAnalysisAlService;
    private final String currentUserId;

    private final DefaultTableModel tableModel;
    private final JTable reviewTable;
    private final JTextArea seasonalResultArea = new JTextArea(8, 60);

    public Module3Panel(TransactionQueryService tqService, TransactionAnalysisAlService taService, String userId) {
        this.transactionQueryService = tqService;
        this.transactionAnalysisAlService = taService;
        this.currentUserId = userId;

        // Initialize table model here
        String[] columnNames = {"Transaction ID", "Date", "Description", "Amount", "Payment Method", "AI Suggestion", "Corrected Category"};
        this.tableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return column == 6; }
        };
        this.reviewTable = new JTable(tableModel);

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);

        // Create title panel
        JPanel titlePanel = createTitlePanel();
        
        // Create main content panel
        JPanel mainContentPanel = createMainContentPanel();
        
        // Create a seasonal analysis panel
        JPanel seasonalPanel = createSeasonalAnalysisPanel();

        add(titlePanel, BorderLayout.NORTH);
        add(mainContentPanel, BorderLayout.CENTER);
        add(seasonalPanel, BorderLayout.SOUTH);
    }

    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("* Smart Transaction Categorization");
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 24));
        titleLabel.setForeground(DeepManageApp.COLOR_TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel("Review AI-suggested transaction categories and make necessary corrections");
        subtitleLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        subtitleLabel.setForeground(DeepManageApp.COLOR_TEXT_SECONDARY);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(subtitleLabel);

        titlePanel.add(textPanel, BorderLayout.WEST);
        return titlePanel;
    }

    private JPanel createMainContentPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);

        // Create operation button panel
        JPanel buttonPanel = createButtonPanel();
        
        // Create a table panel
        JPanel tablePanel = createTablePanel();

        mainPanel.add(buttonPanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        return mainPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        buttonPanel.setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JButton loadReviewButton = createStyledButton(">> Load Transactions for Review", DeepManageApp.COLOR_BUTTON_PRIMARY);
        JButton saveCategoryButton = createStyledButton("* Save Category Correction", DeepManageApp.COLOR_SUCCESS);

        loadReviewButton.addActionListener(e -> loadTransactionsForReview());
        saveCategoryButton.addActionListener(e -> saveCategoryCorrection());

        buttonPanel.add(loadReviewButton);
        buttonPanel.add(Box.createHorizontalStrut(15));
        buttonPanel.add(saveCategoryButton);

        return buttonPanel;
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color bgColor = backgroundColor;
                if (getModel().isPressed()) {
                    bgColor = new Color(
                        Math.max(0, backgroundColor.getRed() - 40),
                        Math.max(0, backgroundColor.getGreen() - 40),
                        Math.max(0, backgroundColor.getBlue() - 40)
                    );
                } else if (getModel().isRollover()) {
                    bgColor = new Color(
                        Math.max(0, backgroundColor.getRed() - 20),
                        Math.max(0, backgroundColor.getGreen() - 20),
                        Math.max(0, backgroundColor.getBlue() - 20)
                    );
                }
                
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                
                g2.dispose();
            }
        };
        
        button.setFont(new Font("Microsoft YaHei", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(backgroundColor);
        button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Calculate appropriate width based on text length
        FontMetrics fm = button.getFontMetrics(button.getFont());
        int textWidth = fm.stringWidth(text);
        int buttonWidth = Math.max(250, textWidth + 60); // Minimum 250px, or text width + padding
        button.setPreferredSize(new Dimension(buttonWidth, 40));
        
        return button;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(DeepManageApp.COLOR_PANEL_BACKGROUND);
        tablePanel.setBorder(createStyledBorder("Transaction Review List"));

        // Configure table style
        setupTableStyle();
        
        JScrollPane tableScrollPane = new JScrollPane(reviewTable);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder());
        tableScrollPane.getViewport().setBackground(DeepManageApp.COLOR_PANEL_BACKGROUND);
        tableScrollPane.setPreferredSize(new Dimension(0, 300));

        tablePanel.add(tableScrollPane, BorderLayout.CENTER);
        return tablePanel;
    }

    private void setupTableStyle() {
        // Basic table settings
        reviewTable.setFillsViewportHeight(true);
        reviewTable.setRowHeight(35);
        reviewTable.setGridColor(DeepManageApp.COLOR_BORDER);
        reviewTable.setSelectionBackground(new Color(DeepManageApp.COLOR_PRIMARY.getRed(), 
                                                    DeepManageApp.COLOR_PRIMARY.getGreen(), 
                                                    DeepManageApp.COLOR_PRIMARY.getBlue(), 30));
        reviewTable.setSelectionForeground(DeepManageApp.COLOR_TEXT_PRIMARY);
        reviewTable.setBackground(DeepManageApp.COLOR_PANEL_BACKGROUND);
        reviewTable.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        reviewTable.setShowGrid(true);
        reviewTable.setShowHorizontalLines(true);
        reviewTable.setShowVerticalLines(true);
        
        // Set uniform thick border for all grid lines
        reviewTable.setBorder(BorderFactory.createLineBorder(DeepManageApp.COLOR_BORDER, 2));
        reviewTable.setIntercellSpacing(new Dimension(2, 2));

        // Table header style - ensure proper visibility
        JTableHeader header = reviewTable.getTableHeader();
        header.setBackground(DeepManageApp.COLOR_PRIMARY);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Microsoft YaHei", Font.BOLD, 14)); // Increased font size
        header.setPreferredSize(new Dimension(0, 45)); // Increased height
        header.setBorder(BorderFactory.createLineBorder(DeepManageApp.COLOR_BORDER, 2));
        header.setReorderingAllowed(false); // Prevent column reordering
        header.setResizingAllowed(true); // Allow column resizing
        
        // Ensure header text is always visible
        header.setOpaque(true);
        header.repaint();

        // Set column widths
        reviewTable.getColumnModel().getColumn(0).setPreferredWidth(120); // Transaction ID
        reviewTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Date
        reviewTable.getColumnModel().getColumn(2).setPreferredWidth(200); // Description
        reviewTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Amount
        reviewTable.getColumnModel().getColumn(4).setPreferredWidth(120); // Payment Method
        reviewTable.getColumnModel().getColumn(5).setPreferredWidth(160); // AI Suggestion
        reviewTable.getColumnModel().getColumn(6).setPreferredWidth(160); // Corrected Category

        // Set category editor
        JComboBox<String> categoryEditorCombo = new JComboBox<>(new String[]{
            "Dining", "Transportation", "Entertainment", "Shopping", "Groceries", "Utilities", "Rent", "Salary", "Other", "Education", "Digital & Internet Services"
        });
        categoryEditorCombo.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        reviewTable.getColumnModel().getColumn(6).setCellEditor(new DefaultCellEditor(categoryEditorCombo));

        // Set cell renderers with borders
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createLineBorder(DeepManageApp.COLOR_BORDER, 1));
                setHorizontalAlignment(JLabel.CENTER);
                return c;
            }
        };
        
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createLineBorder(DeepManageApp.COLOR_BORDER, 1));
                setHorizontalAlignment(JLabel.LEFT);
                return c;
            }
        };

        // Apply renderers
        reviewTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // ID
        reviewTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer); // Date
        reviewTable.getColumnModel().getColumn(2).setCellRenderer(leftRenderer);   // Description
        reviewTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer); // Amount
        reviewTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer); // Payment Method
        reviewTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer); // AI Suggestion
        reviewTable.getColumnModel().getColumn(6).setCellRenderer(centerRenderer); // Corrected Category
    }

    private JPanel createSeasonalAnalysisPanel() {
        JPanel seasonalPanel = new JPanel(new BorderLayout(15, 15));
        seasonalPanel.setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);
        seasonalPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Seasonal analysis button
        JPanel seasonalButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        seasonalButtonPanel.setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);
        
        JButton seasonalButton = createStyledButton("^ Load Seasonal Spending Analysis", DeepManageApp.COLOR_ACCENT);
        seasonalButton.addActionListener(e -> loadSeasonalAnalysis());
        seasonalButtonPanel.add(seasonalButton);

        // Results display area
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBackground(DeepManageApp.COLOR_PANEL_BACKGROUND);
        resultPanel.setBorder(createStyledBorder("Seasonal Analysis Results"));

        seasonalResultArea.setEditable(false);
        seasonalResultArea.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        seasonalResultArea.setBackground(DeepManageApp.COLOR_PANEL_BACKGROUND);
        seasonalResultArea.setForeground(DeepManageApp.COLOR_TEXT_PRIMARY);
        seasonalResultArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        seasonalResultArea.setLineWrap(true);
        seasonalResultArea.setWrapStyleWord(true);

        JScrollPane seasonalScrollPane = new JScrollPane(seasonalResultArea);
        seasonalScrollPane.setBorder(BorderFactory.createEmptyBorder());
        seasonalScrollPane.setPreferredSize(new Dimension(0, 150));
        seasonalScrollPane.getViewport().setBackground(DeepManageApp.COLOR_PANEL_BACKGROUND);

        resultPanel.add(seasonalScrollPane, BorderLayout.CENTER);

        seasonalPanel.add(seasonalButtonPanel, BorderLayout.NORTH);
        seasonalPanel.add(resultPanel, BorderLayout.CENTER);

        return seasonalPanel;
    }

    private Border createStyledBorder(String title) {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(DeepManageApp.COLOR_BORDER, 1),
                title,
                0,
                0,
                new Font("Microsoft YaHei", Font.BOLD, 14),
                DeepManageApp.COLOR_TEXT_PRIMARY
            ),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );
    }

    private void loadTransactionsForReview() {
        tableModel.setRowCount(0);
        seasonalResultArea.setText("Loading transactions for review...\n");
        seasonalResultArea.append("User ID: " + currentUserId + "\n");
        
        new SwingWorker<List<TransactionDisplayData>, Void>() {
            @Override
            protected List<TransactionDisplayData> doInBackground() throws QueryException {
                System.out.println("[Module3Panel] Starting to retrieve transactions for review, User ID: " + currentUserId);
                List<TransactionDisplayData> transactions = transactionQueryService.getTransactionsForReview(currentUserId);
                System.out.println("[Module3Panel] Retrieved transaction data: " + transactions.size() + " records");
                return transactions;
            }
            
            @Override
            protected void done() {
                try {
                    List<TransactionDisplayData> transactions = get();
                    seasonalResultArea.append("✅ Successfully loaded " + transactions.size() + " transaction records\n");
                    
                    if (transactions.isEmpty()) {
                        seasonalResultArea.append("⚠️ No transaction data found\n");
                    } else {
                        seasonalResultArea.append("Populating table data...\n");
                        for (int i = 0; i < transactions.size(); i++) {
                            TransactionDisplayData tx = transactions.get(i);
                            Object[] rowData = new Object[]{
                                tx.getId(), 
                                tx.getDate(), 
                                tx.getDescription(), 
                                tx.getAmount(), 
                                tx.getPaymentMethod(), 
                                tx.getAiSuggestedCategory(), 
                                tx.getCurrentCategory()
                            };
                            tableModel.addRow(rowData);
                            System.out.println("[Module3Panel] Added row " + (i+1) + ": " + tx.getDescription());
                        }
                        seasonalResultArea.append("✅ Table data population completed\n");
                        
                        // Refresh table display
                        reviewTable.revalidate();
                        reviewTable.repaint();
                    }
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    String errorMsg = "❌ Failed to load transactions: " + cause.getMessage();
                    seasonalResultArea.append(errorMsg + "\n");
                    System.err.println("[Module3Panel] " + errorMsg);
                    ex.printStackTrace();
                    
                    JOptionPane.showMessageDialog(Module3Panel.this, 
                        "Failed to load transactions: " + cause.getMessage(), 
                        "Query Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void saveCategoryCorrection() {
        int selectedRow = reviewTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a transaction record to update", 
                "Selection Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (reviewTable.isEditing()) {
            reviewTable.getCellEditor().stopCellEditing();
        }
        
        String transactionId = (String) tableModel.getValueAt(selectedRow, 0);
        String newCategory = (String) tableModel.getValueAt(selectedRow, 6);
        seasonalResultArea.append("Saving category for transaction " + transactionId + " as " + newCategory + "...\n");

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws TransactionException {
                transactionQueryService.updateTransactionCategory(transactionId, newCategory);
                return null;
            }
            
            @Override
            protected void done() {
                try {
                    get();
                    seasonalResultArea.append("✅ Successfully updated category for transaction " + transactionId + "\n");
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    seasonalResultArea.append("❌ Failed to update category: " + cause.getMessage() + "\n");
                    JOptionPane.showMessageDialog(Module3Panel.this, 
                        "Failed to update category: " + cause.getMessage(), 
                        "Update Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void loadSeasonalAnalysis() {
        seasonalResultArea.setText("Loading seasonal spending analysis...\n");
        
        new SwingWorker<List<String>, Void>() {
            @Override
            protected List<String> doInBackground() throws AlException {
                return transactionAnalysisAlService.analyzeSeasonalSpending(currentUserId);
            }
            
            @Override
            protected void done() {
                try {
                    List<String> patterns = get();
                    seasonalResultArea.append("\n📊 AI Seasonal Spending Analysis Results:\n");
                    seasonalResultArea.append("═══════════════════════════════════\n");
                    patterns.forEach(p -> seasonalResultArea.append("• " + p + "\n"));
                    seasonalResultArea.append("\nAnalysis completed ✨\n");
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    seasonalResultArea.append("❌ Seasonal analysis failed: " + cause.getMessage() + "\n");
                    JOptionPane.showMessageDialog(Module3Panel.this, 
                        "Analysis failed: " + cause.getMessage(), 
                        "AI Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
}
