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
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);

        // Header Panel with title and description
        JPanel headerPanel = createHeaderPanel();
        
        // Combined Panel for actions and results
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);
        
        // Action Panel with styled buttons
        JPanel actionPanel = createActionPanel();
        
        // Results Panel with enhanced styling
        JPanel resultsPanel = createResultsPanel();

        mainPanel.add(actionPanel, BorderLayout.NORTH);
        mainPanel.add(resultsPanel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setBackground(DeepManageApp.COLOR_PANEL_BACKGROUND);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DeepManageApp.COLOR_BORDER, 1),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));
        
        JLabel titleLabel = new JLabel("📋 Portfolio Intelligence Center");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(DeepManageApp.COLOR_PRIMARY);
        
        JLabel descriptionLabel = new JLabel("<html>Optimize your investment portfolio with AI-powered analysis and intelligent allocation recommendations.</html>");
        descriptionLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        descriptionLabel.setForeground(new Color(0x555555));
        
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(descriptionLabel, BorderLayout.CENTER);
        
        return headerPanel;
    }
    
    private JPanel createActionPanel() {
        JPanel mainActionPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        mainActionPanel.setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);
        
        // Portfolio Import Panel
        JPanel importPanel = new JPanel(new BorderLayout(10, 10));
        importPanel.setBackground(DeepManageApp.COLOR_PANEL_BACKGROUND);
        importPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DeepManageApp.COLOR_BORDER, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel importTitle = new JLabel("📁 Portfolio Data Import");
        importTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        importTitle.setForeground(DeepManageApp.COLOR_TEXT_PRIMARY);
        
        JLabel importDesc = new JLabel("<html>Import your portfolio holdings from CSV file to start analysis and get AI-powered recommendations.</html>");
        importDesc.setFont(new Font("SansSerif", Font.PLAIN, 12));
        importDesc.setForeground(new Color(0x666666));
        
        JButton importButton = createStyledButton("📂 Import Portfolio CSV", DeepManageApp.COLOR_ACCENT);
        importButton.addActionListener(e -> importPortfolioCSV());
        
        importPanel.add(importTitle, BorderLayout.NORTH);
        importPanel.add(importDesc, BorderLayout.CENTER);
        importPanel.add(importButton, BorderLayout.SOUTH);
        
        // Portfolio Evaluation Panel
        JPanel evaluationPanel = new JPanel(new BorderLayout(10, 10));
        evaluationPanel.setBackground(DeepManageApp.COLOR_PANEL_BACKGROUND);
        evaluationPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DeepManageApp.COLOR_BORDER, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel evaluationTitle = new JLabel("⚡ Portfolio Analysis");
        evaluationTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        evaluationTitle.setForeground(DeepManageApp.COLOR_TEXT_PRIMARY);
        
        JLabel evaluationDesc = new JLabel("<html>Get intelligent allocation recommendations and risk assessment based on your current portfolio composition.</html>");
        evaluationDesc.setFont(new Font("SansSerif", Font.PLAIN, 12));
        evaluationDesc.setForeground(new Color(0x666666));
        
        JButton evaluationButton = createStyledButton("🎯 Evaluate Portfolio", DeepManageApp.COLOR_SUCCESS);
        evaluationButton.addActionListener(e -> evaluateAllocation());
        
        evaluationPanel.add(evaluationTitle, BorderLayout.NORTH);
        evaluationPanel.add(evaluationDesc, BorderLayout.CENTER);
        evaluationPanel.add(evaluationButton, BorderLayout.SOUTH);
        
        mainActionPanel.add(importPanel);
        mainActionPanel.add(evaluationPanel);
        
        return mainActionPanel;
    }
    
    private JPanel createResultsPanel() {
        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);
        
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        resultArea.setBackground(new Color(0xF8F9FA));
        resultArea.setForeground(DeepManageApp.COLOR_TEXT_PRIMARY);
        resultArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        resultArea.setText("🎯 Welcome to Portfolio Intelligence!\n\nGet started by choosing an action above:\n\n📂 Import Portfolio CSV:\n   • Upload your current portfolio holdings\n   • Supports standard CSV format\n   • Automatically processes asset allocations\n   • Prepares data for AI analysis\n\n🎯 Evaluate Portfolio:\n   • AI-powered allocation analysis\n   • Risk assessment and recommendations\n   • Optimization suggestions\n   • Performance insights and trends");
        
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DeepManageApp.COLOR_BORDER, 1),
            BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10),
                "📊 Portfolio Analysis Results & Recommendations",
                0,
                0,
                new Font("SansSerif", Font.BOLD, 14),
                DeepManageApp.COLOR_TEXT_PRIMARY
            )
        ));
        scrollPane.setMinimumSize(new Dimension(0, 200));
        
        resultsPanel.add(scrollPane, BorderLayout.CENTER);
        
        return resultsPanel;
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color buttonColor = bgColor;
                if (getModel().isPressed()) {
                    buttonColor = new Color(
                        Math.max(0, bgColor.getRed() - 40),
                        Math.max(0, bgColor.getGreen() - 40),
                        Math.max(0, bgColor.getBlue() - 40)
                    );
                } else if (getModel().isRollover()) {
                    buttonColor = new Color(
                        Math.max(0, bgColor.getRed() - 20),
                        Math.max(0, bgColor.getGreen() - 20),
                        Math.max(0, bgColor.getBlue() - 20)
                    );
                }
                
                g2.setColor(buttonColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                // Draw text
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                
                g2.dispose();
            }
        };
        
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }

    private void importPortfolioCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Portfolio CSV File");
        // Optionally set file filters for .csv if needed
        // Example: fileChooser.setFileFilter(new FileNameExtensionFilter("CSV files", "csv"));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            resultArea.setText("🔄 Importing portfolio from: " + selectedFile.getAbsolutePath() + "\n\n");
            try (InputStream fileStream = new FileInputStream(selectedFile)) { // Use try-with-resources for the stream
                portfolioIntelligenceAlService.importPortfolioFromCSV(fileStream);
                resultArea.append("✅ SUCCESS: Portfolio CSV " + selectedFile.getName() + " processed successfully.\n");
                resultArea.append("📊 Data is now available for portfolio analysis functions.\n");
                resultArea.append("\n🎯 Next step: Click 'Evaluate Portfolio' to get AI recommendations!\n");
                JOptionPane.showMessageDialog(this, "Portfolio CSV Import completed for " + selectedFile.getName() + ".", "Import Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (AlException ex) {
                resultArea.append("❌ ERROR during CSV import (AI Service): " + ex.getMessage() + "\n");
                JOptionPane.showMessageDialog(this, "Error during CSV import: " + ex.getMessage(), "Import Error (AI Service)", JOptionPane.ERROR_MESSAGE);
            } catch (FileNotFoundException ex) {
                resultArea.append("❌ ERROR: Selected file not found: " + selectedFile.getAbsolutePath() + "\n");
                JOptionPane.showMessageDialog(this, "File not found: " + selectedFile.getName(), "Import Error", JOptionPane.ERROR_MESSAGE);
            } catch (IOException ex) {
                resultArea.append("❌ ERROR reading file " + selectedFile.getName() + ": " + ex.getMessage() + "\n");
                JOptionPane.showMessageDialog(this, "Error reading file: " + ex.getMessage(), "Import Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) { // Catch any other unexpected exceptions
                resultArea.append("❌ UNEXPECTED ERROR during CSV import: " + ex.getMessage() + "\n");
                JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage(), "Import Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace(); // Good for debugging
            }
        } else {
            resultArea.append("📝 CSV import cancelled by user.\n");
        }
    }

    private void evaluateAllocation() {
        resultArea.setText("🔄 Evaluating Portfolio Allocation...\n\n");
        resultArea.append("🤖 AI is analyzing your portfolio composition and market conditions...\n\n");

        Map<String, BigDecimal> compositionToEvaluate = portfolioIntelligenceAlService.getLatestImportedPortfolio();

        if (compositionToEvaluate == null || compositionToEvaluate.isEmpty()) {
            resultArea.append("⚠️ No portfolio data has been imported. Using default mock portfolio for evaluation.\n");
            resultArea.append("📊 Default: Stocks 60%, Bonds 25%, Cash 15%\n");
            resultArea.append("═══════════════════════════════════════════════════════\n");
            // Fallback to default if nothing imported or service returns null/empty
            compositionToEvaluate = Map.of(
                    "Stocks", new BigDecimal("0.60"), 
                    "Bonds", new BigDecimal("0.25"), 
                    "Cash", new BigDecimal("0.15")
            );
        } else {
            resultArea.append("📈 Using imported portfolio data for evaluation:\n");
            resultArea.append("Current Portfolio Composition:\n");
            // It's good practice to ensure amounts are scaled for display if they aren't already
            // However, the mock service already scales them in its output log upon import.
            compositionToEvaluate.forEach((asset, amount) -> 
                resultArea.append(String.format("  💼 %s: %.2f%%\n", asset, amount.multiply(new BigDecimal("100"))))
            );
            resultArea.append("═══════════════════════════════════════════════════════\n");
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
                    resultArea.append("\n🎯 AI-Optimized Portfolio Allocation Recommendations:\n");
                    resultArea.append("═══════════════════════════════════════════════════════\n");
                    if (suggestedComposition.isEmpty()) {
                        resultArea.append("🤖 AI did not provide specific new allocation recommendations.\n");
                        resultArea.append("💡 This might indicate your current portfolio is already well-optimized!\n");
                    } else {
                        resultArea.append("📊 Recommended Portfolio Composition:\n\n");
                        suggestedComposition.forEach((asset, amount) -> 
                            resultArea.append(String.format("  🎯 %s: %.2f%%\n", asset, amount.multiply(new BigDecimal("100"))))
                        );
                        // Optional: Calculate and display total of suggested composition to verify
                        BigDecimal suggestedTotal = suggestedComposition.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
                        resultArea.append(String.format("\n💰 Total Allocation Coverage: %.1f%%\n", suggestedTotal.multiply(new BigDecimal("100")).setScale(1, RoundingMode.HALF_UP)));
                        resultArea.append("\n✨ Consider rebalancing your portfolio according to these AI recommendations for optimal performance!\n");
                    }
                } catch (Exception ex) { 
                    handleException(ex, "portfolio evaluation with AI suggestion"); 
                }
            }
        }.execute();
    }

    private void handleException(Exception ex, String context) {
        Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
        resultArea.append("❌ ERROR getting " + context + ": " + cause.getMessage() + "\n");
        JOptionPane.showMessageDialog(this, "Error getting " + context + ": " + cause.getMessage(), "AI Error", JOptionPane.ERROR_MESSAGE);
    }
}
