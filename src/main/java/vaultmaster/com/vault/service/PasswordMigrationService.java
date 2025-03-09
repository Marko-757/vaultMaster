package vaultmaster.com.vault.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import vaultmaster.com.vault.model.User;
import vaultmaster.com.vault.repository.UserRepository;

import java.util.List;

@Service
public class PasswordMigrationService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public PasswordMigrationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void migratePasswords() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            String currentPassword = user.getPasswordHash();

            // If password is not hashed (too short), hash it
            if (currentPassword.length() < 60) {
                String hashedPassword = encoder.encode(currentPassword);
                userRepository.updatePassword(user.getUserId(), hashedPassword);
                System.out.println("Updated password for user: " + user.getEmail());
            }
        }
    }
}
