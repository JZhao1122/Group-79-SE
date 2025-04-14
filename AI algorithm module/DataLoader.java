import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class DataLoader {
    public static List<Transaction> loadTransactionsFromCSV(String filePath) {
        List<Transaction> transactions = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 5) {
                    LocalDate date = LocalDate.parse(parts[0]);
                    double amount = Double.parseDouble(parts[1]);
                    String category = parts[2].isEmpty() ? null : parts[2];
                    String description = parts[3];
                    String paymentMethod = parts[4];
                    transactions.add(new Transaction(date, amount, category, description, paymentMethod));
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading CSV: " + e.getMessage());
        }
        return transactions;
    }
}