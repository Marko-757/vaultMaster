package vaultmaster.com.vault.repository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import vaultmaster.com.vault.model.Role;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

@Repository
public class RoleRepository {

    private final JdbcTemplate jdbcTemplate;

    public RoleRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Role> roleRowMapper = (rs, rowNum) -> {
        Role role = new Role();
        role.setRoleId(UUID.fromString(rs.getString("role_id")));
        role.setTeamId(UUID.fromString(rs.getString("team_id")));
        role.setRoleName(rs.getString("role_name"));
        String createdByStr = rs.getString("created_by");
        role.setCreatedBy(createdByStr != null ? UUID.fromString(createdByStr) : null);
        Timestamp createdAtTs = rs.getTimestamp("created_at");
        role.setCreatedAt(createdAtTs != null ? new Date(createdAtTs.getTime()) : null);
        return role;
    };

    public Optional<Role> findById(UUID roleId) {
        String sql = "SELECT * FROM roles WHERE role_id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, roleRowMapper, roleId.toString()));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Role> findByTeamId(UUID teamId) {
        String sql = "SELECT * FROM roles WHERE team_id = ?";
        return jdbcTemplate.query(sql, roleRowMapper, teamId.toString());
    }

    public void save(Role role) {
        String sql = "INSERT INTO roles (role_id, team_id, role_name, created_by, created_at) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                role.getRoleId().toString(),
                role.getTeamId().toString(),
                role.getRoleName(),
                role.getCreatedBy() != null ? role.getCreatedBy().toString() : null,
                role.getCreatedAt() != null ? new Timestamp(role.getCreatedAt().getTime()) : null);
    }

    public void update(Role role) {
        String sql = "UPDATE roles SET role_name = ?, created_by = ?, created_at = ? WHERE role_id = ?";
        jdbcTemplate.update(sql,
                role.getRoleName(),
                role.getCreatedBy() != null ? role.getCreatedBy().toString() : null,
                role.getCreatedAt() != null ? new Timestamp(role.getCreatedAt().getTime()) : null,
                role.getRoleId().toString());
    }

    public int deleteById(UUID roleId) {
        String sql = "DELETE FROM roles WHERE role_id = ?";
        return jdbcTemplate.update(sql, roleId.toString());
    }
}
