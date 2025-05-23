package service;

import dto.TransactionData;
import exception.TransactionException;
import java.io.InputStream;
import java.util.List;

public interface FinancialTransactionService {
    String addTransaction(TransactionData data) throws TransactionException;
    int importTransactions(InputStream fileStream) throws TransactionException;
    List<TransactionData> getAllTransactions();

    // --- 新增的 getAllTransactions 方法实现 ---
    List<TransactionData> getAllTransactions(String userId) throws TransactionException;
}
