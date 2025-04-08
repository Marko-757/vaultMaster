package vaultmaster.com.vault.service;

import org.springframework.stereotype.Service;
import vaultmaster.com.vault.model.PasswordEntry;
import vaultmaster.com.vault.repository.PasswordEntryRepository;
import vaultmaster.com.vault.util.AESUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class PasswordEntryService {

    private final PasswordEntryRepository repository;

    public PasswordEntryService(PasswordEntryRepository repository) {
        this.repository = repository;
    }

    public PasswordEntry createPasswordEntry(UUID userId, PasswordEntry entry) {
        try {
            String encrypted = AESUtil.encrypt(entry.getPasswordHash());
            entry.setPasswordHash(encrypted);
            entry.setUserId(userId); // ✅ this sets the creator of the password
            entry.setCreatedAt(LocalDateTime.now());
            entry.setUpdatedAt(LocalDateTime.now());
            return repository.save(entry);
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed: " + e.getMessage(), e);
        }
    }


    // RETRIEVE encrypted (no password exposed)
    public Optional<PasswordEntry> getByEntryId(int entryId) {
        return repository.findById(entryId);
    }

    // RETRIEVE decrypted password
    public String getDecryptedPassword(int entryId) {
        PasswordEntry entry = repository.findById(entryId)
                .orElseThrow(() -> new RuntimeException("Entry not found"));
        try {
            return AESUtil.decrypt(entry.getPasswordHash());
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed: " + e.getMessage(), e);
        }
    }

    // UPDATE
    public void updatePasswordEntry(int entryId, PasswordEntry updatedEntry) {
        try {
            String encrypted = AESUtil.encrypt(updatedEntry.getPasswordHash());
            updatedEntry.setPasswordHash(encrypted);
            updatedEntry.setUpdatedAt(LocalDateTime.now());
            repository.update(entryId, updatedEntry);
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed: " + e.getMessage(), e);
        }
    }

    // DELETE
    public void deletePasswordEntry(int entryId) {
        repository.delete(entryId);
    }

    // List by user (personal)
    public List<PasswordEntry> getAllForUser(UUID userId) {
        return repository.findByUserId(userId);
    }

    // List by team (team-related passwords)
    public List<PasswordEntry> getAllForTeam(UUID teamId) {
        return repository.findByTeamId(teamId);
    }
}
