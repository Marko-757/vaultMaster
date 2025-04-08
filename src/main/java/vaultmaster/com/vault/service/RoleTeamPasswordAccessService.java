package vaultmaster.com.vault.service;

import org.springframework.stereotype.Service;
import vaultmaster.com.vault.repository.RoleTeamPasswordAccessRepository;

import java.util.List;
import java.util.UUID;

@Service
public class RoleTeamPasswordAccessService {

    private final RoleTeamPasswordAccessRepository repository;

    public RoleTeamPasswordAccessService(RoleTeamPasswordAccessRepository repository) {
        this.repository = repository;
    }

    public void grantAccess(UUID roleId, int teamPasswordId) {
        repository.grantAccess(roleId, teamPasswordId);
    }

    public void revokeAccess(UUID roleId, int teamPasswordId) {
        repository.revokeAccess(roleId, teamPasswordId);
    }

    public boolean canAccess(UUID roleId, int teamPasswordId) {
        return repository.hasAccess(roleId, teamPasswordId);
    }

    public List<Integer> getAccessiblePasswordIds(UUID roleId) {
        return repository.getAccessiblePasswordIds(roleId);
    }
}
