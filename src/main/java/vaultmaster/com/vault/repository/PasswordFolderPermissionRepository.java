package vaultmaster.com.vault.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class PasswordFolderPermissionRepository {

    private final JdbcTemplate jdbcTemplate;

    public PasswordFolderPermissionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void grantPermission(UUID folderId, UUID roleId, String permission) {
        String sql = """
            INSERT INTO password_folder_permissions (folder_id, role_id, permission_name)
            VALUES (?, ?, ?)
        """;
        jdbcTemplate.update(sql, folderId, roleId, permission);
    }

    public List<String> getPermissions(UUID folderId, UUID roleId) {
        String sql = """
            SELECT permission_name FROM password_folder_permissions
            WHERE folder_id = ? AND role_id = ?
        """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("permission"), folderId, roleId);
    }

    public void revokePermission(UUID folderId, UUID roleId, String permission) {
        String sql = """
            DELETE FROM password_folder_permissions
            WHERE folder_id = ? AND role_id = ? AND permission_name = ?
        """;
        jdbcTemplate.update(sql, folderId, roleId, permission);
    }
}
