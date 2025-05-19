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
        List<TransactionData> allTransactions = financialTransactionService.getAllTransactions();
        List<TransactionDisplayData> displayList = new ArrayList<>();
        for (TransactionData t : allTransactions) {
            TransactionDisplayData display = new TransactionDisplayData(
                t.getId(), t.getDate(), t.getDescription(), t.getAmount(),
                t.getPaymentMethod(), t.getCategory(), t.getCategory()
            );
            displayList.add(display);
        }
        return displayList;
    }

    @Override
    public void updateTransactionCategory(String transactionId, String correctedCategory) throws TransactionException {
        // 可选：实现分类更正逻辑
    }
}