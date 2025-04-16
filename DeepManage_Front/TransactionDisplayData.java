package DeepManage_Front;

public class TransactionDisplayData {
    private String id;
    private LocalDate date;
    private String description;
    private BigDecimal amount;
    private String paymentMethod;
    private String aiSuggestedCategory;
    private String currentCategory;
    
    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public String getAiSuggestedCategory() { return aiSuggestedCategory; }
    public void setAiSuggestedCategory(String aiSuggestedCategory) { this.aiSuggestedCategory = aiSuggestedCategory; }
    
    public String getCurrentCategory() { return currentCategory; }
    public void setCurrentCategory(String currentCategory) { this.currentCategory = currentCategory; }
}
