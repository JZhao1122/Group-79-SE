package service;

import dto.TransactionDisplayData;
import exception.QueryException;
import exception.TransactionException;
import java.util.List;

public interface TransactionQueryService {
    List<TransactionDisplayData> getTransactionsForReview(String userId) throws QueryException;
    void updateTransactionCategory(String transactionId, String correctedCategory) throws TransactionException;
}
