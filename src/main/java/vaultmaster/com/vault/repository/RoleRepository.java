package vaultmaster.com.vault.repository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import vaultmaster.com.vault.model.Role;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class RoleRepository {

    private final JdbcTemplate jdbcTemplate;

    public RoleRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Role> roleRowMapper = (rs, rowNum) -> mapRowToRole(rs);

    private Role mapRowToRole(ResultSet rs) throws SQLException {
        Role role = new Role();
        role.setRoleId(UUID.fromString(rs.getString("role_id")));
        role.setRoleName(rs.getString("role_name"));
        role.setTeamId(UUID.fromString(rs.getString("team_id")));
        role.setCreatedBy(UUID.fromString(rs.getString("created_by")));
        role.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return role;
    }

    public Role save(Role role) {
        String sql = """
            INSERT INTO roles (role_id, team_id, role_name, created_by, created_at)
            VALUES (?, ?, ?, ?, ?)
        """;

        jdbcTemplate.update(sql,
                role.getRoleId(),
                role.getTeamId(),
                role.getRoleName(),
                role.getCreatedBy(),
                role.getCreatedAt());

        return role;
    }

    public Optional<Role> findById(UUID roleId) {
        String sql = "SELECT * FROM roles WHERE role_id = ?";

        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, roleRowMapper, roleId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Role> findByNameAndTeamId(String roleName, UUID teamId) {
        String sql = "SELECT * FROM roles WHERE role_name = ? AND team_id = ?";

        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, roleRowMapper, roleName, teamId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Role> findByName(String roleName) {
        String sql = "SELECT * FROM roles WHERE role_name = ?";

        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, roleRowMapper, roleName));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Role> findByTeamId(UUID teamId) {
        String sql = "SELECT * FROM roles WHERE team_id = ?";
        return jdbcTemplate.query(sql, roleRowMapper, teamId);
    }

    public void delete(UUID roleId) {
        String sql = "DELETE FROM roles WHERE role_id = ?";
        jdbcTemplate.update(sql, roleId);
    }

    public UUID findTeamIdByRoleId(UUID roleId) {
        String sql = "SELECT team_id FROM roles WHERE role_id = ?";
        return jdbcTemplate.queryForObject(sql, UUID.class, roleId);
    }

    public boolean existsById(UUID roleId) {
        String sql = "SELECT COUNT(*) FROM roles WHERE role_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, roleId);
        return count != null && count > 0;
    }

    public void renameRole(UUID roleId, String newName) {
        String sql = "UPDATE roles SET role_name = ? WHERE role_id = ?";
        jdbcTemplate.update(sql, newName, roleId);
    }

    public boolean existsByIdAndTeamId(UUID roleId, UUID teamId) {
        String sql = "SELECT COUNT(*) FROM roles WHERE role_id = ? AND team_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, roleId, teamId);
        return count != null && count > 0;
    }

    public Optional<Role> findRoleForUserInTeam(UUID userId, UUID teamId) {
        String sql = """
        SELECT r.* FROM team_members tm
        JOIN roles r ON tm.role_id = r.role_id
        WHERE tm.user_id = ? AND tm.team_id = ?
    """;

        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, roleRowMapper, userId, teamId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

}
