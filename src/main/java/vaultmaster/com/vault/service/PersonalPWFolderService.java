package vaultmaster.com.vault.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import vaultmaster.com.vault.model.PersonalPWFolder;
import vaultmaster.com.vault.repository.PersonalPWFolderRepository;
import java.util.List;
import java.util.UUID;

@Service
public class PersonalPWFolderService {
    private final PersonalPWFolderRepository repository;

    public PersonalPWFolderService(PersonalPWFolderRepository repository) {
        this.repository = repository;
    }

    public int createFolder(PersonalPWFolder folder) {
        if (folder.getFolderId() == null) {
            folder.setFolderId(UUID.randomUUID()); // ✅ Assign UUID if missing
        }
        return repository.createFolder(folder);
    }

    public List<PersonalPWFolder> getUserFolders(UUID userId) {
        boolean userExists = repository.doesUserExist(userId);
        if (!userExists) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist.");
        }
        return repository.getUserFolders(userId);
    }

    public int updateFolder(UUID folderId, String newName) {
        int rowsAffected = repository.updateFolder(folderId, newName);
        if (rowsAffected == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Folder not found.");
        }
        return rowsAffected;
    }

    public void deleteFolder(UUID folderId) {
        int rowsAffected = repository.deleteFolder(folderId);
        if (rowsAffected == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Folder not found.");
        }
    }

}
