package vaultmaster.com.vault.service;

import org.springframework.stereotype.Service;
import vaultmaster.com.vault.model.TeamPassword;
import vaultmaster.com.vault.model.PasswordEntry; // Add this import
import vaultmaster.com.vault.repository.TeamPasswordRepository;
import vaultmaster.com.vault.repository.PasswordEntryRepository; // Add this import
import vaultmaster.com.vault.util.AESUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TeamPasswordService {

    private final TeamPasswordRepository repo;
    private final PasswordEntryRepository passwordEntryRepository;  // Add the PasswordEntryRepository

    public TeamPasswordService(TeamPasswordRepository repo, PasswordEntryRepository passwordEntryRepository) {
        this.repo = repo;
        this.passwordEntryRepository = passwordEntryRepository;  // Inject the PasswordEntryRepository
    }

    public TeamPassword createPassword(TeamPassword pw) {
        try {
            // Step 1: Create the password entry
            PasswordEntry entry = new PasswordEntry();
            entry.setUserId(pw.getCreatedBy());
            entry.setAccountName(pw.getAccountName());
            entry.setUsername(pw.getUsername());
            entry.setPasswordHash(pw.getPlaintextPassword());
            entry.setWebsite(pw.getWebsite());
            entry.setFolderId(pw.getFolderId());

            // Encrypt the password entry
            String encryptedPassword = AESUtil.encrypt(entry.getPasswordHash());
            entry.setPasswordHash(encryptedPassword);

            // Set audit fields
            LocalDateTime now = LocalDateTime.now();
            entry.setCreatedAt(now);
            entry.setUpdatedAt(now);

            // Step 2: Save the password entry and get its entryId
            PasswordEntry createdEntry = passwordEntryRepository.save(entry);

            // Step 3: Create the team password and associate with the password entry
            pw.setEntryId(createdEntry.getEntryId());  // Associate the created password entry
            pw.setCreatedAt(now);
            pw.setModifiedAt(now);
            pw.setModifiedBy(pw.getCreatedBy()); // Initially same as creator

            // Save the team password in the team_passwords table
            return repo.save(pw);
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    public TeamPassword getPasswordById(int id) {
        return repo.findById(id).map(password -> {
            try {
                String encrypted = password.getEncryptedPassword();

                if (!AESUtil.isValidEncryptedFormat(encrypted)) {
                    throw new IllegalArgumentException("Invalid encrypted password format.");
                }

                String decrypted = AESUtil.decrypt(encrypted);
                password.setEncryptedPassword(decrypted); // Still using encryptedPassword field for decrypted output
                return password;
            } catch (Exception e) {
                throw new RuntimeException("Decryption failed", e);
            }
        }).orElseThrow(() -> new RuntimeException("Password not found"));
    }

    public List<TeamPassword> getPasswordsByTeam(UUID teamId) {
        return repo.findByTeamId(teamId);
    }

    public void deletePassword(int id) {
        // First delete the team password
        TeamPassword teamPassword = repo.findById(id).orElseThrow(() -> new RuntimeException("Password not found"));

        // Delete the associated password entry from password_entries table
        passwordEntryRepository.delete(teamPassword.getEntryId());

        // Now delete the team password
        repo.delete(id);
    }
}
