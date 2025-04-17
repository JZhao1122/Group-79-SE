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

        // Initialize table model here
        String[] columnNames = {"ID", "Date", "Description", "Amount", "Payment Method", "AI Suggestion", "Corrected Category"};
        this.tableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return column == 6; }
        };
        this.reviewTable = new JTable(tableModel);

        initComponents();
        // Optional: Load data initially
        // loadTransactionsForReview();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);

        // Top: Load Button and Table
        JPanel topPanel = new JPanel(new BorderLayout(5,5));
        topPanel.setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);
        JButton loadReviewButton = new JButton("Load Transactions for Review");
        topPanel.add(loadReviewButton, BorderLayout.NORTH);

        reviewTable.setFillsViewportHeight(true);
        reviewTable.setRowHeight(25);
        reviewTable.setGridColor(Color.LIGHT_GRAY);
        JComboBox<String> categoryEditorCombo = new JComboBox<>(new String[]{"Dining", "Transportation", "Entertainment", "Shopping", "Groceries", "Utilities", "Rent", "Salary", "Other"});
        reviewTable.getColumnModel().getColumn(6).setCellEditor(new DefaultCellEditor(categoryEditorCombo));
        JScrollPane tableScrollPane = new JScrollPane(reviewTable);
        tableScrollPane.getViewport().setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);
        topPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Middle: Save Category Button
        JPanel middlePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        middlePanel.setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);
        JButton saveCategoryButton = new JButton("Save Selected Category Correction");
        middlePanel.add(saveCategoryButton);

        // Bottom: Seasonal Analysis
        JPanel bottomPanel = new JPanel(new BorderLayout(5,5));
        bottomPanel.setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);
        JButton seasonalButton = new JButton("Load Seasonal Spending Analysis");
        seasonalResultArea.setEditable(false);
        JScrollPane seasonalScrollPane = new JScrollPane(seasonalResultArea);
        seasonalScrollPane.setBorder(BorderFactory.createTitledBorder("Seasonal Analysis Log / Status"));

        bottomPanel.add(seasonalButton, BorderLayout.NORTH);
        bottomPanel.add(seasonalScrollPane, BorderLayout.CENTER);

        JPanel southWrapper = new JPanel(new BorderLayout());
        southWrapper.setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);
        southWrapper.add(middlePanel, BorderLayout.NORTH);
        southWrapper.add(bottomPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.CENTER);
        add(southWrapper, BorderLayout.SOUTH);

        // --- Action Listeners ---
        loadReviewButton.addActionListener(e -> loadTransactionsForReview());
        saveCategoryButton.addActionListener(e -> saveCategoryCorrection());
        seasonalButton.addActionListener(e -> loadSeasonalAnalysis());
    }

    private void loadTransactionsForReview() {
        tableModel.setRowCount(0);
        seasonalResultArea.setText("Loading transactions for review...\n");
        new SwingWorker<List<TransactionDisplayData>, Void>() {
            @Override
            protected List<TransactionDisplayData> doInBackground() throws QueryException {
                return transactionQueryService.getTransactionsForReview(currentUserId);
            }
            @Override
            protected void done() {
                try {
                    List<TransactionDisplayData> transactions = get();
                    seasonalResultArea.append("Loaded " + transactions.size() + " transactions.\n");
                    for (TransactionDisplayData tx : transactions) {
                        tableModel.addRow(new Object[]{ tx.getId(), tx.getDate(), tx.getDescription(), tx.getAmount(), tx.getPaymentMethod(), tx.getAiSuggestedCategory(), tx.getCurrentCategory() });
                    }
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    seasonalResultArea.append("ERROR loading transactions: " + cause.getMessage() + "\n");
                    JOptionPane.showMessageDialog(Module3Panel.this, "Error loading transactions: " + cause.getMessage(), "Query Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void saveCategoryCorrection() {
        int selectedRow = reviewTable.getSelectedRow();
        if (selectedRow == -1) { JOptionPane.showMessageDialog(this, "Please select a transaction row to update.", "Selection Error", JOptionPane.WARNING_MESSAGE); return; }
        if (reviewTable.isEditing()) { reviewTable.getCellEditor().stopCellEditing(); }
        String transactionId = (String) tableModel.getValueAt(selectedRow, 0);
        String newCategory = (String) tableModel.getValueAt(selectedRow, 6);
        seasonalResultArea.append("Saving category for " + transactionId + " as " + newCategory + "...\n");

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
                    seasonalResultArea.append("SUCCESS: Category updated for " + transactionId + ".\n");
                    // Optionally refresh row or table
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    seasonalResultArea.append("ERROR updating category: " + cause.getMessage() + "\n");
                    JOptionPane.showMessageDialog(Module3Panel.this, "Error updating category: " + cause.getMessage(), "Update Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void loadSeasonalAnalysis() {
        seasonalResultArea.setText("Loading Seasonal Analysis...\n");
        new SwingWorker<List<String>, Void>() {
            @Override
            protected List<String> doInBackground() throws AlException {
                return transactionAnalysisAlService.analyzeSeasonalSpending(currentUserId);
            }
            @Override
            protected void done() {
                try {
                    List<String> patterns = get();
                    seasonalResultArea.append("AI Seasonal Spending Analysis:\n------------------------------\n");
                    patterns.forEach(p -> seasonalResultArea.append("- " + p + "\n"));
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    seasonalResultArea.append("ERROR loading seasonal analysis: " + cause.getMessage() + "\n");
                    JOptionPane.showMessageDialog(Module3Panel.this, "Error: " + cause.getMessage(), "AI Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
}
