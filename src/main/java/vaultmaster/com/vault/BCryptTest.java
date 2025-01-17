package vaultmaster.com.vault;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class BCryptTest {
    public static void main(String[] args) {
        // Example password
        String password = "password123";

        // Hash the password using BCrypt
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        System.out.println("Hashed Password: " + hashedPassword);

        // Validate the password
        boolean matches = BCrypt.checkpw(password, hashedPassword);
        System.out.println("Password matches: " + matches);
    }
}

