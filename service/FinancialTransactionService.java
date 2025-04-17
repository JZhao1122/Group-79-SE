package service;

import dto.TransactionData;
import exception.TransactionException;
import java.io.InputStream;

public interface FinancialTransactionService {
    String addTransaction(TransactionData data) throws TransactionException;
    int importTransactions(InputStream fileStream) throws TransactionException;
}
