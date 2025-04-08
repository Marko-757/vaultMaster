package vaultmaster.com.vault.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import vaultmaster.com.vault.model.PersonalPWEntry;
import vaultmaster.com.vault.repository.PersonalPWRepository;
import vaultmaster.com.vault.util.AESUtil;

import java.util.List;
import java.util.UUID;

@Service
public class PersonalPWService {

    private final PersonalPWRepository repository;

    public PersonalPWService(PersonalPWRepository repository) {
        this.repository = repository;
    }

    public PersonalPWEntry addPassword(PersonalPWEntry entry) {
        try {
            String encryptedPassword = AESUtil.encrypt(entry.getPasswordHash());
            entry.setPasswordHash(encryptedPassword);
            return repository.insertPassword(entry);
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    public List<PersonalPWEntry> getUserPasswords(UUID userId) {
        if (!repository.userExists(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist.");
        }
        return repository.getPasswordsByUser(userId);
    }

    public PersonalPWEntry getPasswordById(Long entryId) {
        PersonalPWEntry entry = repository.getPasswordById(entryId);
        if (entry == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Password entry not found.");
        }
        return entry;
    }

    public void deletePassword(Long entryId) {
        int rowsAffected = repository.deletePassword(entryId);
        if (rowsAffected == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Password entry not found.");
        }
    }

    public void updatePassword(PersonalPWEntry entry) {
        try {
            String encrypted = AESUtil.encrypt(entry.getPasswordHash());
            entry.setPasswordHash(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }

        int rowsAffected = repository.updatePassword(entry);
        if (rowsAffected == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Password entry not found or user unauthorized.");
        }
    }

    public String decryptPasswordById(Long entryId, UUID userId) throws Exception {
        PersonalPWEntry entry = repository.getPasswordById(entryId);
        if (entry == null || !entry.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Password entry not found or unauthorized.");
        }

        String encryptedPassword = entry.getPasswordHash();

        if (!AESUtil.isValidEncryptedFormat(encryptedPassword)) {
            throw new IllegalArgumentException("Encrypted password format is invalid.");
        }

        return AESUtil.decrypt(encryptedPassword);
    }

    public List<UUID> getUserFolders(UUID userId) {
        if (!repository.userExists(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist.");
        }
        return repository.getUserFolderIds(userId);
    }

    public List<PersonalPWEntry> getPasswordsByFolder(UUID folderId) {
        List<PersonalPWEntry> passwords = repository.getPasswordsByFolder(folderId);
        if (passwords == null || passwords.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No passwords found for this folder.");
        }
        return passwords;
    }
}
