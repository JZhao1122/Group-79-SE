package ui;

import exception.AlException;
import service.PortfolioIntelligenceAlService;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Map;

public class Module5Panel extends JPanel {

    private final PortfolioIntelligenceAlService portfolioIntelligenceAlService;
    private final JTextArea resultArea = new JTextArea(15, 60);

    public Module5Panel(PortfolioIntelligenceAlService service) {
        this.portfolioIntelligenceAlService = service;
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

        JLabel titleLabel = new JLabel("Portfolio Intelligence");
        titleLabel.setFont(DeepManageApp.FONT_HEADER);
        titleLabel.setForeground(DeepManageApp.COLOR_ACCENT);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        buttonPanel.setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);
        JButton evalButton = new JButton("Evaluate Portfolio Allocation");
        buttonPanel.add(evalButton);

        // Panel for CSV Import button
        JPanel importPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        importPanel.setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);
        JButton importCsvButton = new JButton("Import Portfolio CSV");
        importPanel.add(importCsvButton);

        // Adjust layout to include the new importPanel
        JPanel topControlsPanel = new JPanel(new BorderLayout());
        topControlsPanel.setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);
        topControlsPanel.add(buttonPanel, BorderLayout.NORTH);
        topControlsPanel.add(importPanel, BorderLayout.SOUTH);

        // Create results panel
        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setOpaque(true);
        resultsPanel.setBackground(Color.WHITE);
        resultsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Portfolio Analysis", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, DeepManageApp.FONT_SUBHEADER, DeepManageApp.COLOR_ACCENT));

        add(topControlsPanel, BorderLayout.NORTH); // Use the combined panel
        add(scrollPane, BorderLayout.CENTER);

        // --- Action Listeners ---
        evalButton.addActionListener(e -> evaluateAllocation());
        importCsvButton.addActionListener(e -> importPortfolioCSV());
    }

    private void importPortfolioCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Portfolio CSV File");
        // Optionally set file filters for .csv if needed
        // Example: fileChooser.setFileFilter(new FileNameExtensionFilter("CSV files", "csv"));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            resultArea.setText("Attempting to import portfolio from: " + selectedFile.getAbsolutePath() + "\n");
            try (InputStream fileStream = new FileInputStream(selectedFile)) { // Use try-with-resources for the stream
                portfolioIntelligenceAlService.importPortfolioFromCSV(fileStream);
                resultArea.append("SUCCESS: Portfolio CSV " + selectedFile.getName() + " processed by service.\n");
                resultArea.append("Data should now be available for portfolio analysis functions.\n");
                 JOptionPane.showMessageDialog(this, "Portfolio CSV Import completed for " + selectedFile.getName() + ".", "Import Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (AlException ex) {
                resultArea.append("ERROR during CSV import (AI Service): " + ex.getMessage() + "\n");
                JOptionPane.showMessageDialog(this, "Error during CSV import: " + ex.getMessage(), "Import Error (AI Service)", JOptionPane.ERROR_MESSAGE);
            } catch (FileNotFoundException ex) {
                resultArea.append("ERROR: Selected file not found: " + selectedFile.getAbsolutePath() + "\n");
                JOptionPane.showMessageDialog(this, "File not found: " + selectedFile.getName(), "Import Error", JOptionPane.ERROR_MESSAGE);
            } catch (IOException ex) {
                resultArea.append("ERROR reading file " + selectedFile.getName() + ": " + ex.getMessage() + "\n");
                JOptionPane.showMessageDialog(this, "Error reading file: " + ex.getMessage(), "Import Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) { // Catch any other unexpected exceptions
                resultArea.append("UNEXPECTED ERROR during CSV import: " + ex.getMessage() + "\n");
                JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage(), "Import Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace(); // Good for debugging
            }
        } else {
            resultArea.append("CSV import cancelled by user.\n");
        }
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(DeepManageApp.FONT_BUTTON);
        button.setForeground(Color.WHITE);
        button.setBackground(DeepManageApp.COLOR_ACCENT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Add hover effect
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

    private void evaluateAllocation() {
        resultArea.setText("Evaluating Portfolio Allocation...\n");

        Map<String, BigDecimal> compositionToEvaluate = portfolioIntelligenceAlService.getLatestImportedPortfolio();

        if (compositionToEvaluate == null || compositionToEvaluate.isEmpty()) {
            resultArea.append("No portfolio data has been imported. Using default mock portfolio for evaluation.\n");
            resultArea.append("Default: Stocks 60%, Bonds 25%, Cash 15%\n---------------------------------------\n");
            // Fallback to default if nothing imported or service returns null/empty
            compositionToEvaluate = Map.of(
                    "Stocks", new BigDecimal("0.60"), 
                    "Bonds", new BigDecimal("0.25"), 
                    "Cash", new BigDecimal("0.15")
            );
        } else {
            resultArea.append("Using imported portfolio data for evaluation:\n");
            // It's good practice to ensure amounts are scaled for display if they aren't already
            // However, the mock service already scales them in its output log upon import.
            compositionToEvaluate.forEach((asset, amount) -> 
                resultArea.append(String.format("  - %s: %.2f%n", asset, amount))
            );
            resultArea.append("---------------------------------------\n");
        }

        final Map<String, BigDecimal> finalCompositionForWorker = compositionToEvaluate; // effectively final for SwingWorker

        new SwingWorker<Map<String, BigDecimal>, Void>() {
            @Override protected Map<String, BigDecimal> doInBackground() throws AlException {
                // Pass the composition (either imported or default) to the service
                return portfolioIntelligenceAlService.evaluatePortfolioAllocation(finalCompositionForWorker);
            }
            @Override protected void done() {
                try {
                    Map<String, BigDecimal> suggestedComposition = get(); // Get the Map result
                    resultArea.append("\n--- AI Suggested Portfolio Allocation (RMB) ---\n");
                    if (suggestedComposition.isEmpty()) {
                        resultArea.append("AI did not provide a specific new allocation (perhaps due to input or mock logic).\n");
                    } else {
                        suggestedComposition.forEach((asset, amount) -> 
                            resultArea.append(String.format("  - %s: %.2f%n", asset, amount))
                        );
                        // Optional: Calculate and display total of suggested composition to verify
                        BigDecimal suggestedTotal = suggestedComposition.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
                        resultArea.append(String.format("  Total Suggested Value: %.2f%n", suggestedTotal.setScale(2, RoundingMode.HALF_UP)));
                    }
                } catch (Exception ex) { 
                    handleException(ex, "portfolio evaluation with AI suggestion"); 
                }
            }
        }.execute();
    }

    private void handleException(Exception ex, String context) {
        Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
        resultArea.setForeground(DeepManageApp.COLOR_ERROR);
        resultArea.append("ERROR getting " + context + ": " + cause.getMessage() + "\n");
        JOptionPane.showMessageDialog(this, "Error getting " + context + ": " + cause.getMessage(), "AI Error", JOptionPane.ERROR_MESSAGE);
    }
}
