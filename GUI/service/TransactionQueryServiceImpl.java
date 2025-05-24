package service;

import dto.TransactionData;
import dto.TransactionDisplayData;
import exception.QueryException;
import exception.TransactionException;
import java.util.ArrayList;
import java.util.List;

public class TransactionQueryServiceImpl implements TransactionQueryService {
    private final FinancialTransactionService financialTransactionService;

    public TransactionQueryServiceImpl(FinancialTransactionService financialTransactionService) {
        this.financialTransactionService = financialTransactionService;
    }

    @Override
    public List<TransactionDisplayData> getTransactionsForReview(String userId) throws QueryException {
        try {
            List<TransactionData> allTransactions = financialTransactionService.getAllTransactions();
            List<TransactionDisplayData> displayList = new ArrayList<>();
            
            System.out.println("[TransactionQueryServiceImpl] Retrieved " + allTransactions.size() + " transaction records");
            
            for (TransactionData t : allTransactions) {
                // Use existing category as AI suggested category, corrected category initially set to same value
                TransactionDisplayData display = new TransactionDisplayData(
                    t.getId(), 
                    t.getDate(), 
                    t.getDescription(), 
                    t.getAmount(),
                    t.getPaymentMethod(), 
                    t.getCategory(), // AI suggested category
                    t.getCategory()  // Current/corrected category
                );
                displayList.add(display);
            }
            
            System.out.println("[TransactionQueryServiceImpl] Returning " + displayList.size() + " display records");
            return displayList;
        } catch (Exception e) {
            throw new QueryException("Failed to retrieve transactions for review: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateTransactionCategory(String transactionId, String correctedCategory) throws TransactionException {
        try {
            // Get all transactions
            List<TransactionData> allTransactions = financialTransactionService.getAllTransactions();
            
            // Find and update the specified transaction's category
            for (TransactionData transaction : allTransactions) {
                if (transactionId.equals(transaction.getId())) {
                    transaction.setCategory(correctedCategory);
                    System.out.println("[TransactionQueryServiceImpl] Updated transaction " + transactionId + " category to: " + correctedCategory);
                    return;
                }
            }
            
            throw new TransactionException("Transaction ID not found: " + transactionId);
        } catch (Exception e) {
            if (e instanceof TransactionException) {
                throw e;
            }
            throw new TransactionException("Failed to update transaction category: " + e.getMessage(), e);
        }
    }
}