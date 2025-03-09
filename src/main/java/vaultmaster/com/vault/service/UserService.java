package vaultmaster.com.vault.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import vaultmaster.com.vault.model.User;
import vaultmaster.com.vault.repository.UserRepository;
import vaultmaster.com.vault.security.JwtService;
import vaultmaster.com.vault.dto.AuthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    /**
     * Registers a new user.
     * @param email the user's email.
     * @param passwordHash the already hashed password from frontend.
     * @param fullName the user's full name.
     * @param phoneNumber the user's phone number.
     */
    public void registerUser(String email, String passwordHash, String fullName, String phoneNumber) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("User with this email already exists.");
        }

        User newUser = new User();
        newUser.setUserId(UUID.randomUUID());
        newUser.setEmail(email);
        newUser.setPasswordHash(passwordHash); // Already hashed in frontend
        newUser.setFullName(fullName);
        newUser.setPhoneNumber(phoneNumber);
        Date now = new Date();
        newUser.setCreatedDate(now);
        newUser.setModifiedDate(now);
        newUser.setCreatedBy(null);
        newUser.setModifiedBy(null);
        newUser.setVerified(false);

        userRepository.save(newUser);
    }

    public AuthResponse login(String email, String plaintextPassword) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("Invalid email");
        }

        User user = userOptional.get();
        String storedHash = user.getPasswordHash();

        // 🔍 Debugging logs
        logger.info("Checking login for email: {}", email);
        logger.info("Stored Hash: {}", storedHash);

        // ✅ Correct way to check password: use `matches()` instead of generating a new hash
        if (!passwordEncoder.matches(plaintextPassword, storedHash)) {
            logger.warn("Login failed: Incorrect password for email '{}'", email);
            throw new IllegalArgumentException("Incorrect password");
        }

        // ✅ Generate a JWT token for the user
        String token = jwtService.generateToken(user);

        return new AuthResponse(user, token);
    }


    /**
     * Verifies a user using a token.
     * @param token the verification token.
     * @return true if the user is verified, false otherwise.
     */
    public boolean verifyUser(String token) {
        Optional<User> userOptional = userRepository.findByVerificationToken(token);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setVerified(true);
            userRepository.updateUserVerificationStatus(user.getUserId(), true);
            return true;
        }

        return false;
    }

    /**
     * Check if email exists in database.
     */
    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> getUserById(UUID userId) {
        return userRepository.findById(userId);
    }

    public void deleteUser(UUID userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        userRepository.delete(userId);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
