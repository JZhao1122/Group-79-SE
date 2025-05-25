package service;

import dto.User;
import exception.AuthException;

/**
 * User service interface, handles user login, registration, etc.
 */
public interface UserService {
    /**
     * User login
     * @param username Username
     * @param password Password
     * @return The logged-in user object if successful
     * @throws AuthException Thrown when login fails
     */
    User login(String username, String password) throws AuthException;
    
    /**
     * User registration
     * @param username Username
     * @param password Password
     * @param email Email address
     * @return The registered user object if successful
     * @throws AuthException Thrown when registration fails
     */
    User register(String username, String password, String email) throws AuthException;
    
    /**
     * Get user information by user ID
     * @param userId User ID
     * @return User object
     * @throws AuthException Thrown when retrieval fails
     */
    User getUserById(String userId) throws AuthException;
    
    /**
     * Delete or deactivate user account
     * @param userId User ID
     * @param password Password for verification
     * @return True if deleted successfully, otherwise false
     * @throws AuthException Thrown when deletion fails
     */
    boolean deleteUser(String userId, String password) throws AuthException;
}
