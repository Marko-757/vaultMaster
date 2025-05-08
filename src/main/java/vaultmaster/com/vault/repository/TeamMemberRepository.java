package vaultmaster.com.vault.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import vaultmaster.com.vault.dto.TeamMemberProfile;
import vaultmaster.com.vault.model.TeamMember;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public class TeamMemberRepository {

    private final JdbcTemplate jdbc;
    private final UserRowMapper userRowMapper;

    public TeamMemberRepository(JdbcTemplate jdbc, UserRowMapper userRowMapper) {
        this.jdbc = jdbc;
        this.userRowMapper = userRowMapper;
    }

    private final RowMapper<TeamMember> rowMapper = (rs, rowNum) -> {
        TeamMember member = new TeamMember();
        member.setTeamId(UUID.fromString(rs.getString("team_id")));
        member.setUserId(UUID.fromString(rs.getString("user_id")));
        String roleId = rs.getString("role_id");
        member.setRoleId(roleId != null ? UUID.fromString(roleId) : null);
        member.setCreatedBy(rs.getString("created_by"));
        member.setModifiedBy(rs.getString("modified_by"));
        member.setCreatedDate(rs.getTimestamp("created_date").toLocalDateTime());
        member.setModifiedDate(rs.getTimestamp("modified_date").toLocalDateTime());
        return member;
    };

    // CRUD operations
    public void insert(TeamMember member) {
        String sql = """
            INSERT INTO team_members (
                team_id, user_id, role_id, created_by, modified_by, created_date, modified_date
            ) VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        jdbc.update(sql,
                member.getTeamId(),
                member.getUserId(),
                member.getRoleId(),
                member.getCreatedBy(),
                member.getModifiedBy(),
                Timestamp.valueOf(member.getCreatedDate()),
                Timestamp.valueOf(member.getModifiedDate())
        );
    }

    public Optional<TeamMember> findByTeamIdAndUserId(UUID teamId, UUID userId) {
        String sql = "SELECT * FROM team_members WHERE team_id = ? AND user_id = ?";
        try {
            return Optional.ofNullable(jdbc.queryForObject(sql, rowMapper, teamId, userId));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<TeamMember> findByTeamId(UUID teamId) {
        String sql = "SELECT * FROM team_members WHERE team_id = ?";
        return jdbc.query(sql, rowMapper, teamId);
    }

    public List<TeamMember> findByUserId(UUID userId) {
        String sql = "SELECT * FROM team_members WHERE user_id = ?";
        return jdbc.query(sql, rowMapper, userId);
    }

    public void deleteByTeamIdAndUserId(UUID teamId, UUID userId) {
        String sql = "DELETE FROM team_members WHERE team_id = ? AND user_id = ?";
        jdbc.update(sql, teamId, userId);
    }

    public boolean existsByTeamIdAndUserId(UUID teamId, UUID userId) {
        String sql = "SELECT COUNT(*) FROM team_members WHERE team_id = ? AND user_id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, teamId, userId);
        return count != null && count > 0;
    }

    public void updateModifiedInfo(UUID teamId, UUID userId, String modifiedBy, LocalDateTime modifiedDate) {
        String sql = "UPDATE team_members SET modified_by = ?, modified_date = ? WHERE team_id = ? AND user_id = ?";
        jdbc.update(sql, modifiedBy, modifiedDate, teamId, userId);
    }

    public void assignRole(UUID teamId, UUID userId, UUID roleId) {
        String sql = "UPDATE team_members SET role_id = ? WHERE team_id = ? AND user_id = ?";
        jdbc.update(sql, roleId, teamId, userId);
    }

    public void assignRoleWithAudit(UUID teamId, UUID userId, UUID roleId, UUID modifiedBy) {
        assignRole(teamId, userId, roleId);
        updateModifiedInfo(teamId, userId, modifiedBy.toString(), LocalDateTime.now());
    }

    public void removeUserFromTeam(UUID teamId, UUID userId) {
        String sql = "DELETE FROM team_members WHERE team_id = ? AND user_id = ?";
        jdbc.update(sql, teamId, userId);
    }

    public List<Map<String, Object>> findRolesByUserId(UUID userId) {
        String sql = """
            SELECT r.role_id, r.role_name, t.team_id, t.team_name
            FROM team_members tm
            JOIN roles r ON tm.role_id = r.role_id
            JOIN teams t ON tm.team_id = t.team_id
            WHERE tm.user_id = ?
        """;

        return jdbc.query(sql, (rs, rowNum) -> {
            Map<String, Object> role = new HashMap<>();
            role.put("roleId", rs.getObject("role_id"));
            role.put("roleName", rs.getString("role_name"));
            role.put("teamId", rs.getObject("team_id"));
            role.put("teamName", rs.getString("team_name"));
            return role;
        }, userId);
    }

    public List<TeamMemberProfile> findTeamMemberProfilesByTeamId(UUID teamId) {
        String sql = """
        SELECT u.user_id, u.full_name, u.email, u.phone_number, r.role_name
        FROM team_members tm
        JOIN users u ON tm.user_id = u.user_id
        LEFT JOIN roles r ON tm.role_id = r.role_id
        WHERE tm.team_id = ?
    """;

        return jdbc.query(sql, (rs, rowNum) -> new TeamMemberProfile(
                UUID.fromString(rs.getString("user_id")),       // ✅ include userId
                rs.getString("full_name"),
                rs.getString("email"),
                rs.getString("phone_number"),
                rs.getString("role_name") != null ? rs.getString("role_name") : "Member"
        ), teamId);
    }


    // 🔹 New logic for invite section: Get memberships that user is in but doesn't own
    public List<Map<String, Object>> findMembershipsExcludingOwned(UUID userId) {
        String sql = """
            SELECT t.team_id, t.team_name
            FROM team_members tm
            JOIN teams t ON tm.team_id = t.team_id
            WHERE tm.user_id = ? AND t.created_by != ?
        """;
        return jdbc.query(sql, (rs, rowNum) -> {
            Map<String, Object> team = new HashMap<>();
            team.put("teamId", UUID.fromString(rs.getString("team_id")));
            team.put("teamName", rs.getString("team_name"));
            return team;
        }, userId, userId);
    }
}
