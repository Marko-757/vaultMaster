package vaultmaster.com.vault.repository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import vaultmaster.com.vault.model.Permission;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class PermissionRepository {

    private final JdbcTemplate jdbcTemplate;

    public PermissionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Permission> permissionRowMapper = (rs, rowNum) -> {
        Permission p = new Permission();
        p.setId(UUID.fromString(rs.getString("permission_id")));
        p.setName(rs.getString("permission_name"));
        return p;
    };

    public Optional<Permission> findByName(String name) {
        String sql = "SELECT * FROM permissions WHERE permission_name = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, permissionRowMapper, name));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Permission> findAll() {
        String sql = "SELECT * FROM permissions";
        return jdbcTemplate.query(sql, permissionRowMapper);
    }
}
