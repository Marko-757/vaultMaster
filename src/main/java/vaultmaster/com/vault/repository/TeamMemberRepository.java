package vaultmaster.com.vault.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import vaultmaster.com.vault.model.TeamMember;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TeamMemberRepository {

    private final JdbcTemplate jdbcTemplate;

    public TeamMemberRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<TeamMember> rowMapper = (rs, rowNum) -> {
        TeamMember member = new TeamMember();
        member.setId(UUID.fromString(rs.getString("id")));
        member.setTeamId(UUID.fromString(rs.getString("team_id")));
        member.setUserId(UUID.fromString(rs.getString("user_id")));
        String roleId = rs.getString("role_id");
        member.setRoleId(roleId != null ? UUID.fromString(roleId) : null);
        return member;
    };

    public Optional<TeamMember> findByTeamIdAndUserId(UUID teamId, UUID userId) {
        String sql = "SELECT * FROM team_members WHERE team_id = ? AND user_id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, teamId.toString(), userId.toString()));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public void assignRole(UUID teamId, UUID userId, UUID roleId) {
        String sql = "UPDATE team_members SET role_id = ? WHERE team_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, roleId.toString(), teamId.toString(), userId.toString());
    }
}
