package vaultmaster.com.vault.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import vaultmaster.com.vault.model.Permission;

import java.util.List;
import java.util.UUID;

@Repository
public class RolePermissionRepository {

    private final JdbcTemplate jdbcTemplate;

    public RolePermissionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void assignPermissionToRole(UUID roleId, UUID permissionId) {
        String sql = "INSERT INTO role_permissions (role_id, permission_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, roleId, permissionId);
    }


    public List<Permission> findPermissionsByRoleId(UUID roleId) {
        String sql = """
        SELECT p.permission_id, p.permission_name
        FROM permissions p
        JOIN role_permissions rp ON p.permission_id = rp.permission_id
        WHERE rp.role_id = ?
        """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Permission p = new Permission();
            p.setId(UUID.fromString(rs.getString("permission_id")));
            p.setName(rs.getString("permission_name"));
            return p;
        }, roleId);
    }

    public boolean roleHasPermission(UUID roleId, String permissionName) {
        String sql = """
        SELECT COUNT(*)
        FROM role_permissions rp
        JOIN permissions p ON rp.permission_id = p.permission_id
        WHERE rp.role_id = ? AND p.permission_name = ?
    """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, roleId, permissionName);
        return count != null && count > 0;
    }
}
