package vaultmaster.com.vault.service;

import org.springframework.stereotype.Service;
import vaultmaster.com.vault.model.TeamFile;
import vaultmaster.com.vault.repository.TeamFileRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TeamFileService {

    private final TeamFileRepository teamFileRepository;

    public TeamFileService(TeamFileRepository teamFileRepository) {
        this.teamFileRepository = teamFileRepository;
    }

    public TeamFile uploadFile(TeamFile file, UUID uploaderId) {
        file.setFileId(UUID.randomUUID());
        file.setCreatedAt(LocalDateTime.now());
        file.setCreatedBy(uploaderId);
        teamFileRepository.save(file);
        return file;
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
}
