package dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionData {
    private BigDecimal amount;
    private LocalDate date;
    private String description;
    private String paymentMethod;

    // Default constructor
    public TransactionData() {}

    // Getters and Setters
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    @Override
    public String toString() {
        return "TransactionData{" +
                "amount=" + amount +
                ", date=" + date +
                ", description='" + description + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                '}';
    }
}
