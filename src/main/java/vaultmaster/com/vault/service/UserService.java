package vaultmaster.com.vault.service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import vaultmaster.com.vault.model.User;
import vaultmaster.com.vault.repository.UserRepository;
import vaultmaster.com.vault.security.JwtService;
import vaultmaster.com.vault.dto.AuthResponse;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    /**
     * Authenticate the user by email and password.
     * @param email the user's email.
     * @param password the provided password.
     * @return the User if authentication succeeds.
     * @throws IllegalArgumentException if credentials are invalid.
     */
    public User login(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        User user = userOptional.get();

        // Compare plaintext password with hashed password
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        return user;  // Return authenticated user
    }




    /**
     * Generates a simple 6-digit verification code.
     * @return a 6-digit code as String.
     */
    public String generateVerificationCode() {
        int code = (int) (Math.random() * 900000) + 100000; // Random 6-digit number
        return String.valueOf(code);
    }

    /**
     * Authenticates the user and returns a JWT token.
     * (Optional if needed for token-based authentication)
     */
    public AuthResponse authenticateUser(String email, String password) {
        User user = login(email, password);  // Authenticate user

        if (password == null || !password.equals(user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String token = jwtService.generateToken(user);  // Generate JWT token
        return new AuthResponse(user, token);
    }




    /**
     * Registers a new user with verification.
     * @param email the user's email.
     * @param passwordHash
     * @param fullName the user's full name.
     * @param phoneNumber the user's phone number.
     * @param createdByStr information about who created the user.
     * @return a verification token.
     * @throws IllegalArgumentException if the user already exists.
     */
    public void registerUser(String email, String passwordHash, String fullName, String phoneNumber, String createdByStr) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("User with this email already exists.");
        }

        User newUser = new User();
        newUser.setUserId(UUID.randomUUID());
        newUser.setEmail(email);
        newUser.setPasswordHash(passwordHash); // Accept hashed password from frontend
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





    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }



    /**
     * Dummy implementation for verifying a user using a token.
     * @param token the verification token.
     * @return true if verified; false otherwise.
     */
    public boolean verifyUser(String token) {
        // Implement your verification logic here.
        // For demonstration, we return true.
        return true;
    }

    public Optional<User> getUserById(UUID userId) {
        return userRepository.findById(userId);
    }

    public void deleteUser(UUID userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        userRepository.delete(userId);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }



}
