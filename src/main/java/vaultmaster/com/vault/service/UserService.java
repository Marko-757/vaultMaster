package vaultmaster.com.vault.service;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import vaultmaster.com.vault.model.User;
import vaultmaster.com.vault.repository.UserRepository;

import java.security.SecureRandom;
import java.util.Random;

@Service
public class UserService {

    private final UserRepository userRepository;
    private static final int VERIFICATION_CODE_LENGTH = 6;

    // Constructor injection
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Register a new user with secure password hashing.
     *
     * @param email       the user's email
     * @param password    the user's password
     * @param fullName    the user's full name
     * @param phoneNumber the user's phone number
     * @return the registered user
     */
    public User registerUser(String email, String password, String fullName, String phoneNumber) {
        // Check if email is already registered
        if (existsByEmail(email)) {
            throw new IllegalArgumentException("Email is already registered.");
        }

        // Hash the password with BCrypt
        String hashedPassword = hashPassword(password);

        // Create and save the new user
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPasswordHash(hashedPassword);
        newUser.setFullName(fullName);
        newUser.setPhoneNumber(phoneNumber);

        return userRepository.save(newUser);
    }

    /**
     * Log in a user by verifying their credentials.
     *
     * @param email    the user's email
     * @param password the user's password
     * @return the authenticated user
     */
    public User login(String email, String password) {
        // Retrieve user by email
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new IllegalArgumentException("Invalid email or user does not exist.");
        }

        // Validate the password using BCrypt
        if (!validatePassword(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid password.");
        }

        return user; // Login successful
    }

    /**
     * Generate a secure verification code.
     *
     * @return a random 6-digit verification code
     */
    public String generateVerificationCode() {
        Random random = new SecureRandom();
        StringBuilder code = new StringBuilder(VERIFICATION_CODE_LENGTH);
        for (int i = 0; i < VERIFICATION_CODE_LENGTH; i++) {
            code.append(random.nextInt(10)); // Append random digit (0-9)
        }
        return code.toString();
    }

    /**
     * Check if an email is already registered.
     *
     * @param email the email to check
     * @return true if the email is registered, false otherwise
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Get user by email.
     *
     * @param email the email to search for
     * @return the user associated with the email
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Hash a password using BCrypt.
     *
     * @param password the plain-text password
     * @return the hashed password
     */
    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * Validate a password against a hashed password.
     *
     * @param password       the plain-text password
     * @param hashedPassword the hashed password
     * @return true if the passwords match, false otherwise
     */
    public boolean validatePassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}
