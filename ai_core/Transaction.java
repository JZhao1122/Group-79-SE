import java.time.LocalDate;

public class Transaction {
    public LocalDate date;
    public double amount;
    public String category; // optional
    public String description;
    public String paymentMethod;

    public Transaction(LocalDate date, double amount, String category, String description, String paymentMethod) {
        this.date = date;
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.paymentMethod = paymentMethod;
    }
}