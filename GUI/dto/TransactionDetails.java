package dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionDetails {
    private String description; 
    private String merchant;    
    private BigDecimal amount;  
    private LocalDate date;     

    // Getters & Setters...
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getMerchant() { return merchant; }
    public void setMerchant(String merchant) { this.merchant = merchant; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
}
