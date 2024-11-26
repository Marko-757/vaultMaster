package vaultmaster.com.vault.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vaultmaster.com.vault.model.User;
import vaultmaster.com.vault.repository.UserRepository;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

@Service
public class UserService {

    private static final int SALT_LENGTH = 16;

    @Autowired
    private UserRepository userRepository;

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void saveUser(User user) {
        userRepository.save(user);  // Save user to the database
    }

    public User login(String email, String password) {
        // Retrieve the user by email
        User user = userRepository.findByEmail(email);

        // If user exists, check password
        if (user != null) {
            // Get the salt and stored password hash
            String salt = user.getSalt();
            String storedPasswordHash = user.getPasswordHash();

            // Hash the entered password with the salt
            String passwordHash = hashPassword(password, salt);

            // Compare the hashes
            if (storedPasswordHash.equals(passwordHash)) {
                return user; // Return the user if login is successful
            }
        }
        return null; // Return null if login fails (user not found or password doesn't match)
    }


    public User registerUser(String email, String password, String fullName, String phoneNumber) {
        // Generate salt
        String salt = generateSalt();

        // Combine password and salt and hash them (PBKDF2, BCrypt, or similar hashing algorithm)
        String passwordHash = hashPassword(password, salt);

        // Create and save the new user
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPasswordHash(passwordHash);
        newUser.setSalt(salt);
        newUser.setFullName(fullName);
        newUser.setPhoneNumber(phoneNumber);

        return userRepository.save(newUser);
    }

    // Generate a random salt
    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] saltBytes = new byte[SALT_LENGTH];
        random.nextBytes(saltBytes);
        return Base64.getEncoder().encodeToString(saltBytes);  // Store the salt as a Base64-encoded string
    }

    // Hash the password combined with the salt (for simplicity, using SHA-256 as an example)
    private String hashPassword(String password, String salt) {
        try {
            // Combine password and salt
            String saltedPassword = password + salt;

            // Use a hashing algorithm (e.g., SHA-256, PBKDF2, BCrypt)
            // Example using SHA-256 (though in practice, you should use a more secure hashing algorithm like PBKDF2 or BCrypt)
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(saltedPassword.getBytes());

            return Base64.getEncoder().encodeToString(hashBytes); // Return the hashed password as a Base64-encoded string
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    // Other service methods...
    public String getUserFullName(Long userId) {
        // Fetch user by userId
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        // Return the fullName using the getter method
        return user.getFullName();
    }

}
