package vaultmaster.com.vault.service;

import org.springframework.stereotype.Service;
import vaultmaster.com.vault.model.TeamPWFolder;
import vaultmaster.com.vault.repository.TeamPWFolderRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TeamPWFolderService {

    private final TeamPWFolderRepository repo;

    public TeamPWFolderService(TeamPWFolderRepository repo) {
        this.repo = repo;
    }

    public void createFolder(UUID teamId, String name, UUID createdBy) {
        TeamPWFolder folder = TeamPWFolder.builder()
                .folderId(UUID.randomUUID())
                .teamId(teamId)
                .folderName(name)
                .createdBy(createdBy)
                .createdAt(LocalDateTime.now())
                .modifiedBy(createdBy)
                .modifiedAt(LocalDateTime.now())
                .build();

        repo.createFolder(folder);
    }

    public List<TeamPWFolder> getFoldersForTeam(UUID teamId) {
        return repo.getFoldersByTeamId(teamId);
    }

    public boolean renameFolder(UUID folderId, String newName, UUID userId) {
        return repo.renameFolder(folderId, newName, userId) > 0;
    }

    public boolean deleteFolder(UUID folderId) {
        return repo.deleteFolder(folderId) > 0;
    }


}
