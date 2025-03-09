package vaultmaster.com.vault.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import vaultmaster.com.vault.repository.PersonalPWRepository;
import vaultmaster.com.vault.model.PersonalPWEntry;
import vaultmaster.com.vault.util.AESUtil;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class PersonalPWService {
    private final PersonalPWRepository repository;

    public PersonalPWService(PersonalPWRepository repository) {
        this.repository = repository;
    }

    public int addPassword(PersonalPWEntry entry) throws Exception {
        entry.setTimestamps();
        return repository.savePassword(entry);
    }

    public List<PersonalPWEntry> getUserPasswords(UUID userId) {
        return repository.getPasswordsByUser(userId);
    }

    public PersonalPWEntry getPasswordById(Long entryId) {
        PersonalPWEntry entry = repository.getPasswordById(entryId);
        if (entry == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Password entry not found");
        }
        return entry;
    }

    public void deletePassword(Long entryId) {
        int rowsAffected = repository.deletePassword(entryId);
        if (rowsAffected == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Password entry not found");
        }
    }

}

