package mock; // 或者你实际的包名，例如 GUI.mock

import dto.TransactionData;
import exception.TransactionException;
import service.FinancialTransactionService; // 确保导入接口
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList; // 用于创建 List
import java.util.List;    // 用于 List 类型

public abstract class MockFinancialTransactionService implements FinancialTransactionService {
    private long transactionCounter = 0;

    @Override
    public String addTransaction(TransactionData data) throws TransactionException {
        System.out.println("[Mock] Adding transaction: " + data);
        if (data.getAmount() == null || data.getDate() == null || data.getDescription() == null || data.getDescription().isEmpty()) {
            throw new TransactionException("Mock Validation Error: Missing required fields.");
        }
        transactionCounter++;
        return "TXN" + transactionCounter;
    }

    @Override
    public int importTransactions(InputStream fileStream) throws TransactionException {
        System.out.println("[Mock] Importing transactions from stream...");
        try {
            if (fileStream.read() == -1) {
                System.out.println("[Mock] Warning: Input stream seems empty.");
            }
            System.out.println("[Mock] File stream seems valid.");
            while(fileStream.read() != -1);
            // fileStream.close(); // 注意：传入的流不应该由这个 mock 方法关闭，调用者负责关闭
        } catch (IOException e) {
            throw new TransactionException("Mock IO Error during import.", e);
        }
        return 5;
    }

    // --- 新增的 getAllTransactions 方法实现 ---
    @Override
    public List<TransactionData> getAllTransactions(String userId) throws TransactionException {
        // 这是一个 Mock 实现，所以通常返回一些预设的假数据或空列表
        System.out.println("[Mock] getAllTransactions called for userId: " + userId);
        List<TransactionData> mockTransactions = new ArrayList<>();

        // 可选：根据 userId 添加一些模拟数据
        if ("user123".equals(userId)) { // 假设你的 DeepManageApp 中 userId 是 "user123"
            TransactionData t1 = new TransactionData();
            t1.setId("mockTxn001");
            t1.setAmount(new java.math.BigDecimal("123.45"));
            t1.setDate(java.time.LocalDate.now().minusDays(5));
            t1.setDescription("Mocked Grocery Purchase");
            t1.setCategory("Groceries");
            t1.setPaymentMethod("Credit Card");
            mockTransactions.add(t1);

            TransactionData t2 = new TransactionData();
            t2.setId("mockTxn002");
            t2.setAmount(new java.math.BigDecimal("50.00"));
            t2.setDate(java.time.LocalDate.now().minusDays(2));
            t2.setDescription("Mocked Online Subscription");
            t2.setCategory("Subscriptions");
            t2.setPaymentMethod("Alipay");
            mockTransactions.add(t2);
        }
        // 或者无论 userId 是什么都返回一些通用的模拟数据

        return mockTransactions; // 返回模拟的交易列表
    }
    // 如果接口中的 getAllTransactions() 没有参数，则应该是：
    /*
    @Override
    public List<TransactionData> getAllTransactions() throws TransactionException {
        System.out.println("[Mock] getAllTransactions called");
        List<TransactionData> mockTransactions = new ArrayList<>();
        // 添加一些通用的模拟数据
        TransactionData t1 = new TransactionData();
        // ... (设置 t1 的属性)
        mockTransactions.add(t1);
        return mockTransactions;
    }
    */
}