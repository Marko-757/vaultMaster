package vaultmaster.com.vault.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vaultmaster.com.vault.model.TeamPassword;
import vaultmaster.com.vault.model.PasswordEntry;
import vaultmaster.com.vault.repository.TeamPasswordRepository;
import vaultmaster.com.vault.repository.PasswordEntryRepository;
import vaultmaster.com.vault.util.AESUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TeamPasswordService {

    private final TeamPasswordRepository repo;
    private final PasswordEntryRepository passwordEntryRepository;

    @Value("${spring.profiles.active:}")
    private String activeProfile;

    public TeamPasswordService(TeamPasswordRepository repo, PasswordEntryRepository passwordEntryRepository) {
        this.repo = repo;
        this.passwordEntryRepository = passwordEntryRepository;
    }

    private boolean isDevEnvironment() {
        return "dev".equalsIgnoreCase(activeProfile);
    }

    public TeamPassword createPassword(TeamPassword pw) {
        try {
            // Create the password entry
            PasswordEntry entry = new PasswordEntry();
            entry.setUserId(pw.getCreatedBy());
            entry.setAccountName(pw.getAccountName());
            entry.setUsername(pw.getUsername());
            entry.setPasswordHash(pw.getPlaintextPassword());
            entry.setWebsite(pw.getWebsite());
            entry.setFolderId(pw.getFolderId());

            String plaintext = entry.getPasswordHash();
            String encryptedPassword = AESUtil.encrypt(plaintext);
            entry.setPasswordHash(encryptedPassword);

            if (isDevEnvironment()) {
                System.out.printf("[DEV] Encrypted: %s\n       Plaintext: %s%n", encryptedPassword, plaintext);
            }

            LocalDateTime now = LocalDateTime.now();
            entry.setCreatedAt(now);
            entry.setUpdatedAt(now);

            PasswordEntry createdEntry = passwordEntryRepository.save(entry);

            // Create the team password and associate with the password entry
            pw.setEntryId(createdEntry.getEntryId());
            pw.setCreatedAt(now);
            pw.setModifiedAt(now);
            pw.setModifiedBy(pw.getCreatedBy());

            // Save the team password in the team_passwords table
            return repo.save(pw);
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    public void updatePassword(TeamPassword updated) {
        // 1. Fetch the entry
        Optional<PasswordEntry> optional = passwordEntryRepository.findById(updated.getEntryId());
        if (optional.isEmpty()) {
            throw new RuntimeException("Password entry not found.");
        }

        PasswordEntry entry = optional.get();

        // 2. Update fields
        entry.setAccountName(updated.getAccountName());
        entry.setUsername(updated.getUsername());
        entry.setWebsite(updated.getWebsite());
        entry.setFolderId(updated.getFolderId());
        entry.setUpdatedAt(LocalDateTime.now());

        // 3. Handle password change
        if (updated.getPlaintextPassword() != null && !updated.getPlaintextPassword().isBlank()) {
            try {
                String encrypted = AESUtil.encrypt(updated.getPlaintextPassword());
                entry.setPasswordHash(encrypted);
            } catch (Exception e) {
                throw new RuntimeException("Failed to encrypt updated password", e);
            }
        }
        passwordEntryRepository.update(entry.getEntryId(), entry);
        repo.updateModifiedAt(updated.getTeamPasswordId());

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
        TeamPassword teamPassword = repo.findById(id).orElseThrow(() -> new RuntimeException("Password not found"));
        passwordEntryRepository.delete(teamPassword.getEntryId());

        repo.delete(id);
    }

    public boolean movePasswordToFolder(int teamPasswordId, UUID folderId) {
        return repo.updateFolder(folderId, teamPasswordId) > 0;
    }

    public List<TeamPassword> getPasswordsByFolder(UUID folderId) {
        return repo.findByFolderId(folderId);
    }

    public String decryptPasswordByEntryId(int entryId) throws Exception {
        PasswordEntry entry = passwordEntryRepository.findById(entryId)
                .orElseThrow(() -> new RuntimeException("Entry not found"));
        return AESUtil.decrypt(entry.getPasswordHash());
    }

    public UUID getTeamIdByEntryId(int entryId) {
        return repo.findTeamIdByEntryId(entryId)
                .orElseThrow(() -> new RuntimeException("Team ID not found for entry ID: " + entryId));
    }



}
