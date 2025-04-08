package vaultmaster.com.vault.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import vaultmaster.com.vault.model.RoleTeamPasswordAccess;

import java.util.List;
import java.util.UUID;

@Repository
public class RoleTeamPasswordAccessRepository {

    private final JdbcTemplate jdbcTemplate;

    public RoleTeamPasswordAccessRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void grantAccess(UUID roleId, int teamPasswordId) {
        String sql = "INSERT INTO role_password_access (role_id, team_password_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        jdbcTemplate.update(sql, roleId, teamPasswordId);
    }

    public void revokeAccess(UUID roleId, int teamPasswordId) {
        String sql = "DELETE FROM role_password_access WHERE role_id = ? AND team_password_id = ?";
        jdbcTemplate.update(sql, roleId, teamPasswordId);
    }

    public boolean hasAccess(UUID roleId, int teamPasswordId) {
        String sql = "SELECT COUNT(*) FROM role_password_access WHERE role_id = ? AND team_password_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, roleId, teamPasswordId);
        return count != null && count > 0;
    }

    public List<Integer> getAccessiblePasswordIds(UUID roleId) {
        String sql = "SELECT team_password_id FROM role_password_access WHERE role_id = ?";
        return jdbcTemplate.queryForList(sql, Integer.class, roleId);
    }
}
