package vaultmaster.com.vault.service;

import org.springframework.stereotype.Service;
import vaultmaster.com.vault.repository.RoleTeamFileAccessRepository;

import java.util.List;
import java.util.UUID;

@Service
public class RoleTeamFileAccessService {

    private final RoleTeamFileAccessRepository repository;

    public RoleTeamFileAccessService(RoleTeamFileAccessRepository repository) {
        this.repository = repository;
    }

    public void grantAccess(UUID roleId, UUID teamFileId) {
        repository.grantAccess(roleId, teamFileId);
    }

    public void revokeAccess(UUID roleId, UUID teamFileId) {
        repository.revokeAccess(roleId, teamFileId);
    }

    public boolean canAccess(UUID roleId, UUID teamFileId) {
        return repository.hasAccess(roleId, teamFileId);
    }

    public List<UUID> getAccessibleFileIds(UUID roleId) {
        return repository.getAccessibleFileIds(roleId);
    }
}
