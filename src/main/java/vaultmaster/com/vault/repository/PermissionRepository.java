package vaultmaster.com.vault.repository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import vaultmaster.com.vault.model.Permission;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class PermissionRepository {

    private final JdbcTemplate jdbcTemplate;

    public PermissionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Permission> permissionRowMapper = (rs, rowNum) -> {
        Permission p = new Permission();
        p.setId(rs.getLong("id"));
        p.setName(rs.getString("name"));
        return p;
    };

    public Optional<Permission> findByName(String name) {
        String sql = "SELECT * FROM permissions WHERE name = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, permissionRowMapper, name));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Permission> findAll() {
        return jdbcTemplate.query("SELECT * FROM permissions", permissionRowMapper);
    }
}
