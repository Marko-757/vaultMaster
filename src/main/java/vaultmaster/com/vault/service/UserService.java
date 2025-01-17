package vaultmaster.com.vault.service;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import vaultmaster.com.vault.model.User;
import vaultmaster.com.vault.model.VerificationToken;
import vaultmaster.com.vault.repository.UserRepository;
import vaultmaster.com.vault.repository.VerificationTokenRepository;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private static final int VERIFICATION_CODE_LENGTH = 6;

    public UserService(UserRepository userRepository, VerificationTokenRepository verificationTokenRepository) {
        this.userRepository = userRepository;
        this.verificationTokenRepository = verificationTokenRepository;
    }

    /**
     * Register a new user and generate a verification token.
     *
     * @param email       the user's email
     * @param password    the user's password
     * @param fullName    the user's full name
     * @param phoneNumber the user's phone number
     * @param createdBy   the user who created this record (e.g., "System")
     * @return the verification token
     */
    public String registerUserWithVerification(String email, String password, String fullName, String phoneNumber, String createdBy) {
        if (existsByEmail(email)) {
            throw new IllegalArgumentException("Email is already registered.");
        }

        String hashedPassword = hashPassword(password);
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPasswordHash(hashedPassword);
        newUser.setFullName(fullName);
        newUser.setPhoneNumber(phoneNumber);
        newUser.setCreatedBy(createdBy);
        newUser.setCreatedDate(LocalDateTime.now());
        newUser.setVerified(false); // Set unverified initially

        User savedUser = userRepository.save(newUser);

        // Generate a verification token
        String verificationToken = UUID.randomUUID().toString();
        VerificationToken token = new VerificationToken();
        token.setUserId(savedUser.getUserId());
        token.setToken(verificationToken);
        token.setExpiresAt(LocalDateTime.now().plusDays(1));
        verificationTokenRepository.save(token);

        return verificationToken;
    }

    /**
     * Verify a user's email using the provided token.
     *
     * @param token the verification token
     * @return true if verification is successful, false otherwise
     */
    public boolean verifyUser(String token) {
        Optional<VerificationToken> tokenOptional = verificationTokenRepository.findByToken(token);

        if (tokenOptional.isPresent()) {
            VerificationToken verificationToken = tokenOptional.get();
            if (verificationToken.getExpiresAt().isAfter(LocalDateTime.now())) {
                User user = userRepository.findById(verificationToken.getUserId())
                        .orElseThrow(() -> new IllegalArgumentException("User not found."));
                user.setVerified(true); // Mark user as verified
                userRepository.save(user);
                verificationTokenRepository.delete(verificationToken); // Remove the token after verification
                return true;
            }
        }
        return false;
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
     * Generate a secure password hash using BCrypt.
     *
     * @param password the plain-text password
     * @return the hashed password
     */
    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * Validate a plain-text password against a hashed password.
     *
     * @param password       the plain-text password
     * @param hashedPassword the hashed password
     * @return true if the passwords match, false otherwise
     */
    public boolean validatePassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
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

        // Optionally, update last login or other metadata here
        user.setModifiedDate(LocalDateTime.now());
        user.setModifiedBy("System");
        userRepository.save(user);

        return user; // Login successful
    }

    /**
     * Generate a secure verification code for 2FA or registration.
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
}
