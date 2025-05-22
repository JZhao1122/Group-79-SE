package ui;

import dto.TransactionDisplayData;
import exception.AlException;
import exception.QueryException;
import exception.TransactionException;
import service.TransactionAnalysisAlService;
import service.TransactionQueryService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.util.List;

public class Module3Panel extends JPanel {

    private final TransactionQueryService transactionQueryService;
    private final TransactionAnalysisAlService transactionAnalysisAlService;
    private final String currentUserId;

    private final DefaultTableModel tableModel;
    private final JTable reviewTable;
    private final JTextArea seasonalResultArea = new JTextArea(5, 60);

    public Module3Panel(TransactionQueryService tqService, TransactionAnalysisAlService taService, String userId) {
        this.transactionQueryService = tqService;
        this.transactionAnalysisAlService = taService;
        this.currentUserId = userId;

        // Initialize table model
        String[] columnNames = {"Transaction ID", "Date", "Description", "Amount (¥)", "Payment Method", "AI Category", "Corrected Category"};
        this.tableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return column == 6; }
        };
        this.reviewTable = new JTable(tableModel);

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(DeepManageApp.MAIN_PANEL_BORDER);
        setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);

        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);
        headerPanel.setBorder(DeepManageApp.MAIN_PANEL_HEADER_BORDER);

        JLabel titleLabel = new JLabel("Transaction Categorization");
        titleLabel.setFont(DeepManageApp.FONT_HEADER);
        titleLabel.setForeground(DeepManageApp.COLOR_ACCENT);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Top: Load Button and Table
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Transaction Review", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, DeepManageApp.FONT_SUBHEADER, DeepManageApp.COLOR_ACCENT));
        
        JButton loadReviewButton = createStyledButton("Load Transactions for Review");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);
        buttonPanel.add(loadReviewButton);
        topPanel.add(buttonPanel, BorderLayout.NORTH);

        // Style the table
        styleTable(reviewTable);
        
        JScrollPane tableScrollPane = new JScrollPane(reviewTable);
        tableScrollPane.setBorder(null);
        tableScrollPane.getViewport().setBackground(Color.WHITE);
        tableScrollPane.setPreferredSize(new Dimension(900, 320));
        topPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Middle: Save Category Button
        JPanel middlePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        middlePanel.setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);
        JButton saveCategoryButton = createStyledButton("Save Category Correction");
        middlePanel.add(saveCategoryButton);

        // Bottom: Seasonal Analysis
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Seasonal Analysis", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, DeepManageApp.FONT_SUBHEADER, DeepManageApp.COLOR_ACCENT));

        JButton seasonalButton = createStyledButton("Load Seasonal Analysis");
        JPanel seasonalButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        seasonalButtonPanel.setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);
        seasonalButtonPanel.add(seasonalButton);

        styleTextArea(seasonalResultArea);
        
        JScrollPane seasonalScrollPane = new JScrollPane(seasonalResultArea);
        seasonalScrollPane.setBorder(null);

        bottomPanel.add(seasonalButtonPanel, BorderLayout.NORTH);
        bottomPanel.add(seasonalScrollPane, BorderLayout.CENTER);

        JPanel southWrapper = new JPanel(new BorderLayout(15, 15));
        southWrapper.setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);
        southWrapper.add(middlePanel, BorderLayout.NORTH);
        southWrapper.add(bottomPanel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);
        add(topPanel, BorderLayout.CENTER);
        add(southWrapper, BorderLayout.SOUTH);

        // Action Listeners
        loadReviewButton.addActionListener(e -> loadTransactionsForReview());
        saveCategoryButton.addActionListener(e -> saveCategoryCorrection());
        seasonalButton.addActionListener(e -> loadSeasonalAnalysis());
    }

    private void styleTable(JTable table) {
        table.setFillsViewportHeight(true);
        table.setRowHeight(28);
        table.setGridColor(new Color(0xE8E8E8));
        table.setFont(DeepManageApp.FONT_NORMAL);
        table.setSelectionBackground(DeepManageApp.COLOR_ACCENT);
        table.setSelectionForeground(Color.WHITE);
        
        // Header styling
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(0xF8F8F8));
        table.getTableHeader().setForeground(DeepManageApp.COLOR_ACCENT);
        table.getTableHeader().setBorder(BorderFactory.createLineBorder(new Color(0xE0E0E0)));
        
        // Column widths
        int[] colWidths = {120, 100, 200, 100, 120, 150, 150};
        for (int i = 0; i < colWidths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(colWidths[i]);
        }
    }

    private void styleTextArea(JTextArea area) {
        area.setEditable(false);
        area.setFont(DeepManageApp.FONT_NORMAL);
        area.setBackground(Color.WHITE);
        area.setForeground(Color.BLACK);
        area.setMargin(new Insets(12, 12, 12, 12));
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(DeepManageApp.FONT_BUTTON);
        button.setForeground(Color.WHITE);
        button.setBackground(DeepManageApp.COLOR_ACCENT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(DeepManageApp.COLOR_ACCENT.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(DeepManageApp.COLOR_ACCENT);
            }
        });
        
        return button;
    }

    private void loadTransactionsForReview() {
        tableModel.setRowCount(0);
        seasonalResultArea.setText("Loading transactions for review...\n");
        seasonalResultArea.setForeground(DeepManageApp.COLOR_INFO);
        
        new SwingWorker<List<TransactionDisplayData>, Void>() {
            @Override
            protected List<TransactionDisplayData> doInBackground() throws QueryException {
                return transactionQueryService.getTransactionsForReview(currentUserId);
            }
            @Override
            protected void done() {
                try {
                    List<TransactionDisplayData> transactions = get();
                    seasonalResultArea.setForeground(Color.BLACK);
                    seasonalResultArea.append("Loaded " + transactions.size() + " transactions.\n");
                    for (TransactionDisplayData tx : transactions) {
                        tableModel.addRow(new Object[]{ tx.getId(), tx.getDate(), tx.getDescription(), tx.getAmount(), tx.getPaymentMethod(), tx.getAiSuggestedCategory(), tx.getCurrentCategory() });
                    }
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    seasonalResultArea.setForeground(DeepManageApp.COLOR_ERROR);
                    seasonalResultArea.append("ERROR loading transactions: " + cause.getMessage() + "\n");
                    JOptionPane.showMessageDialog(Module3Panel.this, "Error loading transactions: " + cause.getMessage(), "Query Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void saveCategoryCorrection() {
        int selectedRow = reviewTable.getSelectedRow();
        if (selectedRow == -1) { 
            JOptionPane.showMessageDialog(this, "Please select a transaction row to update.", "Selection Error", JOptionPane.WARNING_MESSAGE); 
            return; 
        }
        if (reviewTable.isEditing()) { reviewTable.getCellEditor().stopCellEditing(); }
        String transactionId = (String) tableModel.getValueAt(selectedRow, 0);
        String newCategory = (String) tableModel.getValueAt(selectedRow, 6);
        seasonalResultArea.append("Saving category for " + transactionId + " as " + newCategory + "...\n");
        seasonalResultArea.setForeground(DeepManageApp.COLOR_INFO);

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws TransactionException {
                transactionQueryService.updateTransactionCategory(transactionId, newCategory);
                return null;
            }
            @Override
            protected void done() {
                try {
                    get(); // Check for exceptions
                    seasonalResultArea.setForeground(DeepManageApp.COLOR_SUCCESS);
                    seasonalResultArea.append("SUCCESS: Category updated for " + transactionId + ".\n");
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    seasonalResultArea.setForeground(DeepManageApp.COLOR_ERROR);
                    seasonalResultArea.append("ERROR updating category: " + cause.getMessage() + "\n");
                    JOptionPane.showMessageDialog(Module3Panel.this, "Error updating category: " + cause.getMessage(), "Update Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void loadSeasonalAnalysis() {
        seasonalResultArea.setText("Loading Seasonal Analysis...\n");
        seasonalResultArea.setForeground(DeepManageApp.COLOR_INFO);
        
        new SwingWorker<List<String>, Void>() {
            @Override
            protected List<String> doInBackground() throws AlException {
                return transactionAnalysisAlService.analyzeSeasonalSpending(currentUserId);
            }
            @Override
            protected void done() {
                try {
                    List<String> patterns = get();
                    seasonalResultArea.setForeground(Color.BLACK);
                    seasonalResultArea.append("\nAI Seasonal Spending Analysis:\n");
                    seasonalResultArea.append("------------------------------\n");
                    patterns.forEach(p -> seasonalResultArea.append("• " + p + "\n"));
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    seasonalResultArea.setForeground(DeepManageApp.COLOR_ERROR);
                    seasonalResultArea.append("ERROR loading seasonal analysis: " + cause.getMessage() + "\n");
                    JOptionPane.showMessageDialog(Module3Panel.this, "Error: " + cause.getMessage(), "AI Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
}
