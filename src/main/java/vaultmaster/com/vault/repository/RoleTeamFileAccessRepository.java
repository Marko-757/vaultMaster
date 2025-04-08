package vaultmaster.com.vault.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import vaultmaster.com.vault.model.RoleTeamFileAccess;

import java.util.List;
import java.util.UUID;

@Repository
public class RoleTeamFileAccessRepository {

    private final JdbcTemplate jdbcTemplate;

    public RoleTeamFileAccessRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void grantAccess(UUID roleId, UUID teamFileId) {
        String sql = "INSERT INTO role_file_access (role_id, file_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        jdbcTemplate.update(sql, roleId, teamFileId);
    }

    public void revokeAccess(UUID roleId, UUID teamFileId) {
        String sql = "DELETE FROM role_file_access WHERE role_id = ? AND file_id = ?";
        jdbcTemplate.update(sql, roleId, teamFileId);
    }

    public boolean hasAccess(UUID roleId, UUID teamFileId) {
        String sql = "SELECT COUNT(*) FROM role_file_access WHERE role_id = ? AND file_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, roleId, teamFileId);
        return count != null && count > 0;
    }

    public List<UUID> getAccessibleFileIds(UUID roleId) {
        String sql = "SELECT file_id FROM role_file_access WHERE role_id = ?";
        return jdbcTemplate.queryForList(sql, UUID.class, roleId);
    }
}
