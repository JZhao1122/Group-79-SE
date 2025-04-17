package dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionDetails {
    private String description; // 交易描述(必填)
    private String merchant;    // 商户名称(可选)
    private BigDecimal amount;  // 金额(可选,可能影响分类)
    private LocalDate date;     // 日期(可选,可能影响分类)

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
