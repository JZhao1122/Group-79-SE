package mock;

import dto.User;
import exception.AuthException;
import service.UserService;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Mock implementation of the UserService interface for testing and demonstration
 * Added CSV file persistence storage functionality
 */
public class MockUserService implements UserService {
    // User storage, key is username, value is user object
    private final Map<String, User> userStore = new HashMap<>();
    
    // User data file path
    private static final String DATA_DIR = "data";
    private static final String USER_DATA_FILE = DATA_DIR + File.separator + "user_data.csv";
    
    // Default user
    public MockUserService() {
        // Ensure data directory exists
        ensureDataDirectoryExists();
        
        // Try to load user data from file
        boolean loadSuccess = loadUsersFromFile();
        
        // If loading fails or no user data, add default user
        if (!loadSuccess || userStore.isEmpty()) {
            User defaultUser = new User("user123", "admin", "password", "admin@example.com");
            userStore.put(defaultUser.getUsername(), defaultUser);
            // Save default user to file
            boolean saved = saveUsersToFile();
            if (!saved) {
                System.err.println("[UserService] Failed to save default user data");
            }
        }
    }
    
    /**
     * Ensure data directory exists
     */
    private void ensureDataDirectoryExists() {
        Path dataDir = Paths.get(DATA_DIR);
        if (!Files.exists(dataDir)) {
            try {
                Files.createDirectories(dataDir);
                System.out.println("[UserService] Created data directory: " + dataDir.toAbsolutePath());
            } catch (IOException e) {
                System.err.println("[UserService] Failed to create data directory: " + e.getMessage());
            }
        }
    }
    
    @Override
    public User login(String username, String password) throws AuthException {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            throw new AuthException("Username and password cannot be empty");
        }
        
        User user = userStore.get(username);
        if (user == null) {
            throw new AuthException("User does not exist");
        }
        
        if (!user.getPassword().equals(password)) {
            throw new AuthException("Incorrect password");
        }
        
        System.out.println("[UserService] User login successful: " + username);
        return user;
    }
    
    @Override
    public User register(String username, String password, String email) throws AuthException {
        // Input validation
        if (username == null || username.isEmpty()) {
            throw new AuthException("Username cannot be empty");
        }
        if (password == null || password.isEmpty()) {
            throw new AuthException("Password cannot be empty");
        }
        if (email == null || email.isEmpty()) {
            throw new AuthException("Email cannot be empty");
        }
        
        // Check special characters
        if (username.contains(",") || password.contains(",") || email.contains(",")) {
            throw new AuthException("Username, password and email cannot contain commas");
        }
        
        // Check if username already exists
        if (userStore.containsKey(username)) {
            throw new AuthException("Username already exists");
        }
        
        // Create new user
        String userId = "user-" + UUID.randomUUID().toString().substring(0, 8);
        User newUser = new User(userId, username, password, email);
        userStore.put(username, newUser);
        
        // Save to file
        boolean saved = saveUsersToFile();
        if (!saved) {
            throw new AuthException("Registration successful but failed to save user data");
        }
        
        System.out.println("[UserService] New user registration successful: " + username);
        return newUser;
    }
    
    @Override
    public User getUserById(String userId) throws AuthException {
        if (userId == null || userId.isEmpty()) {
            throw new AuthException("User ID cannot be empty");
        }
        
        // Traverse user storage to find matching user ID
        for (User user : userStore.values()) {
            if (user.getUserId().equals(userId)) {
                return user;
            }
        }
        
        throw new AuthException("User does not exist: " + userId);
    }
    
    @Override
    public boolean deleteUser(String userId, String password) throws AuthException {
        // Input validation
        if (userId == null || userId.isEmpty()) {
            throw new AuthException("User ID cannot be empty");
        }
        if (password == null || password.isEmpty()) {
            throw new AuthException("Password cannot be empty");
        }
        
        // Find user to delete
        User userToDelete = null;
        String usernameToDelete = null;
        
        for (Map.Entry<String, User> entry : userStore.entrySet()) {
            User user = entry.getValue();
            if (user.getUserId().equals(userId)) {
                userToDelete = user;
                usernameToDelete = entry.getKey();
                break;
            }
        }
        
        if (userToDelete == null) {
            throw new AuthException("User does not exist");
        }
        
        // Verify password
        if (!userToDelete.getPassword().equals(password)) {
            throw new AuthException("Incorrect password, cannot delete account");
        }
        
        // Prevent deleting the last user
        if (userStore.size() <= 1) {
            throw new AuthException("The system must retain at least one user account");
        }
        
        // Delete user
        userStore.remove(usernameToDelete);
        
        // Save changes to file
        boolean saved = saveUsersToFile();
        if (!saved) {
            // If saving fails, restore the user data in memory
            userStore.put(usernameToDelete, userToDelete);
            throw new AuthException("Account deletion failed: Unable to save user data changes");
        }
        
        System.out.println("[UserService] User account deleted: " + usernameToDelete + " (ID: " + userId + ")");
        return true;
    }
    
    /**
     * Load user data from CSV file
     * @return Whether loading was successful
     */
    private boolean loadUsersFromFile() {
        Path filePath = Paths.get(USER_DATA_FILE);
        if (!Files.exists(filePath)) {
            System.out.println("[UserService] User data file does not exist, will create new file: " + filePath.toAbsolutePath());
            return false;
        }
        
        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            // Skip header line
            String line = reader.readLine();
            if (line == null) {
                System.out.println("[UserService] User data file is empty");
                return false;
            }
            
            // Read data lines
            int lineCount = 1;
            while ((line = reader.readLine()) != null) {
                lineCount++;
                try {
                    String[] parts = line.split(",");
                    if (parts.length >= 4) {
                        String userId = parts[0].trim();
                        String username = parts[1].trim();
                        String password = parts[2].trim();
                        String email = parts[3].trim();
                        
                        User user = new User(userId, username, password, email);
                        userStore.put(username, user);
                    } else {
                        System.err.println("[UserService] Line " + lineCount + " has incorrect format: " + line);
                    }
                } catch (Exception e) {
                    System.err.println("[UserService] Error parsing line " + lineCount + ": " + e.getMessage());
                }
            }
            
            System.out.println("[UserService] Loaded " + userStore.size() + " users from file");
            return true;
        } catch (IOException e) {
            System.err.println("[UserService] Error reading user data file: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Save user data to CSV file
     * @return Whether saving was successful
     */
    private boolean saveUsersToFile() {
        Path filePath = Paths.get(USER_DATA_FILE);
        Path backupPath = Paths.get(USER_DATA_FILE + ".bak");
        
        // First create a temporary file
        Path tempPath = null;
        try {
            tempPath = Files.createTempFile("user_data_", ".tmp");
        } catch (IOException e) {
            System.err.println("[UserService] Failed to create temporary file: " + e.getMessage());
            return false;
        }
        
        // Write to temporary file
        try (BufferedWriter writer = Files.newBufferedWriter(tempPath, StandardCharsets.UTF_8)) {
            // Write header line
            writer.write("UserId,Username,Password,Email");
            writer.newLine();
            
            // Write user data
            for (User user : userStore.values()) {
                // Ensure fields don't contain commas
                String userId = sanitizeField(user.getUserId());
                String username = sanitizeField(user.getUsername());
                String password = sanitizeField(user.getPassword());
                String email = sanitizeField(user.getEmail());
                
                writer.write(String.format("%s,%s,%s,%s", userId, username, password, email));
                writer.newLine();
            }
            
            System.out.println("[UserService] Successfully wrote to temporary file: " + tempPath);
        } catch (IOException e) {
            System.err.println("[UserService] Failed to write to temporary file: " + e.getMessage());
            try {
                Files.deleteIfExists(tempPath);
            } catch (IOException ex) {
                // Ignore errors when deleting temporary file
            }
            return false;
        }
        
        // Backup existing file
        if (Files.exists(filePath)) {
            try {
                Files.copy(filePath, backupPath, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("[UserService] Successfully created backup file: " + backupPath);
            } catch (IOException e) {
                System.err.println("[UserService] Failed to create backup file: " + e.getMessage());
                // Continue even if backup fails
            }
        }
        
        // Replace with new file
        try {
            Files.move(tempPath, filePath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("[UserService] Successfully saved " + userStore.size() + " users to file: " + filePath.toAbsolutePath());
            return true;
        } catch (IOException e) {
            System.err.println("[UserService] Failed to replace user data file: " + e.getMessage());
            
            // Try to restore backup
            if (Files.exists(backupPath)) {
                try {
                    Files.move(backupPath, filePath, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("[UserService] Restored backup file");
                } catch (IOException ex) {
                    System.err.println("[UserService] Failed to restore backup file: " + ex.getMessage());
                }
            }
            return false;
        }
    }
    
    /**
     * Sanitize CSV field, ensure it doesn't contain commas
     */
    private String sanitizeField(String field) {
        if (field == null) {
            return "";
        }
        // Replace commas with spaces
        return field.replace(',', ' ');
    }
} 