package dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionDisplayData {
    private String id;
    private LocalDate date;
    private String description;
    private BigDecimal amount;
    private String paymentMethod;
    public String aiSuggestedCategory; 
    private String currentCategory;     

    // Constructor
    public TransactionDisplayData(String id, LocalDate date, String description, BigDecimal amount, String paymentMethod, String aiSuggestedCategory, String currentCategory) {
        this.id = id;
        this.date = date;
        this.description = description;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.aiSuggestedCategory = aiSuggestedCategory;
        this.currentCategory = currentCategory;
    }

    // Getters
    public String getId() { return id; }
    public LocalDate getDate() { return date; }
    public String getDescription() { return description; }
    public BigDecimal getAmount() { return amount; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getAiSuggestedCategory() { return aiSuggestedCategory; }
    public String getCurrentCategory() { return currentCategory; }

    // Setter for editable field
    public void setCurrentCategory(String currentCategory) { this.currentCategory = currentCategory; }
}
