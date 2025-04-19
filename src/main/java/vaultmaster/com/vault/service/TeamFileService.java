package vaultmaster.com.vault.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vaultmaster.com.vault.model.TeamFile;
import vaultmaster.com.vault.repository.TeamFileRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TeamFileService {

    private final TeamFileRepository teamFileRepository;
    private final S3Service s3Service;
    private final TeamFileFolderService teamFileFolderService;

    public TeamFileService(TeamFileRepository teamFileRepository, S3Service s3Service, TeamFileFolderService teamFileFolderService) {
        this.teamFileRepository = teamFileRepository;
        this.s3Service = s3Service;
        this.teamFileFolderService = teamFileFolderService;
    }

    public TeamFile uploadTeamFile(MultipartFile file, UUID teamId, UUID uploaderId, UUID folderId) throws IOException {
        String key = s3Service.uploadFile(file.getBytes(), file.getOriginalFilename());

        TeamFile teamFile = new TeamFile();
        teamFile.setFileId(UUID.randomUUID());
        teamFile.setTeamId(teamId);
        teamFile.setFolderId(folderId);
        teamFile.setFileKey(key);
        teamFile.setOriginalFilename(file.getOriginalFilename());
        teamFile.setFileSize(file.getSize());
        teamFile.setMimeType(file.getContentType());
        teamFile.setCreatedAt(LocalDateTime.now());
        teamFile.setCreatedBy(uploaderId);

        teamFileRepository.save(teamFile);
        return teamFile;
    }


    public Optional<TeamFile> getFileById(UUID fileId) {
        return teamFileRepository.findById(fileId);
    }

    public List<TeamFile> getFilesByTeamId(UUID teamId) {
        return teamFileRepository.findByTeamId(teamId);
    }

    public List<TeamFile> getFilesByFolderId(UUID folderId) {
        return teamFileRepository.findByFolderId(folderId);
    }

    public void deleteFile(UUID fileId) {
        teamFileRepository.deleteById(fileId);
    }

    public boolean moveFile(UUID fileId, UUID folderId) {
        return teamFileRepository.moveFileToFolder(fileId, folderId) > 0;
    }

    public boolean moveFileToFolder(UUID fileId, UUID folderId) {
        return teamFileRepository.updateFileFolder(fileId, folderId) > 0;
    }
    public UUID getTeamIdByFolderId(UUID folderId) {
        return teamFileFolderService.getTeamIdByFolderId(folderId);
    }

}
