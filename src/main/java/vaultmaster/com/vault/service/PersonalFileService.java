package vaultmaster.com.vault.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vaultmaster.com.vault.model.PersonalFile;
import vaultmaster.com.vault.repository.PersonalFileRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class PersonalFileService {

    private final S3Service s3Service;
    private final PersonalFileRepository personalFileRepository;

    public PersonalFile uploadPersonalFile(MultipartFile file, UUID userId, UUID folderId) throws IOException {
        byte[] fileBytes = file.getBytes();
        String originalFilename = file.getOriginalFilename();
        long fileSize = file.getSize();
        String mimeType = file.getContentType(); // <-- Add this line

        String s3Key = s3Service.uploadFile(fileBytes, originalFilename);

        PersonalFile newFile = new PersonalFile();
        newFile.setFileId(UUID.randomUUID());
        newFile.setUserId(userId);
        newFile.setFolderId(folderId);
        newFile.setFileKey(s3Key);
        newFile.setOriginalFilename(originalFilename);
        newFile.setFileSize(fileSize);
        newFile.setMimeType(mimeType); // <-- Set it here
        newFile.setUploadedAt(LocalDateTime.now());

        personalFileRepository.save(newFile);
        return newFile;
    }


    private String detectMimeType(String filename) {
        try {
            return Files.probeContentType(Paths.get(filename));
        } catch (IOException e) {
            return "application/octet-stream"; // fallback type
        }
    }


    public byte[] downloadPersonalFile(String key) throws IOException {
        return s3Service.downloadFile(key);
    }

    public void deletePersonalFile(UUID fileId) {
        PersonalFile file = personalFileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found with ID: " + fileId));

        s3Service.deleteFile(file.getFileKey());  // Remove from S3
        personalFileRepository.deleteById(fileId);  // Remove metadata from DB
    }

    public List<PersonalFile> getFilesByUser(UUID userId) {
        return personalFileRepository.findByUserId(userId);
    }

    public List<PersonalFile> getFilesByFolder(UUID folderId) {
        return personalFileRepository.findByFolderId(folderId);
    }

    public PersonalFile getFileById(UUID fileId) {
        return personalFileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));
    }

}
