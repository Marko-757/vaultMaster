package vaultmaster.com.vault.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vaultmaster.com.vault.model.PersonalFileFolder;
import vaultmaster.com.vault.repository.PersonalFileFolderRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PersonalFileFolderService {

    private final PersonalFileFolderRepository folderRepository;

    public void createFolder(UUID userId, String folderName) {
        PersonalFileFolder folder = PersonalFileFolder.builder()
                .folderId(UUID.randomUUID())
                .userId(userId)
                .folderName(folderName)
                .createdAt(LocalDateTime.now())
                .build();

        folderRepository.save(folder);
    }

    public List<PersonalFileFolder> getFoldersByUser(UUID userId) {
        return folderRepository.findByUserId(userId);
    }

    public PersonalFileFolder getFolderById(UUID folderId) {
        return folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("Folder not found"));
    }

    public void updateFolderName(UUID folderId, String newName) {
        folderRepository.updateFolderName(folderId, newName);
    }

    public void deleteFolder(UUID folderId) {
        folderRepository.deleteById(folderId);
    }
}
