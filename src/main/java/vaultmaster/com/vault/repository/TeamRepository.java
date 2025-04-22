package vaultmaster.com.vault.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import vaultmaster.com.vault.model.Team;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

@Repository
public class TeamRepository {

    private final JdbcTemplate jdbcTemplate;

    public TeamRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private RowMapper<Team> teamRowMapper = (rs, rowNum) -> {
        Team team = new Team();
        team.setTeamId(UUID.fromString(rs.getString("team_id")));
        team.setTeamName(rs.getString("team_name"));
        team.setCreatedBy(UUID.fromString(rs.getString("created_by")));
        team.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return team;
    };

    public boolean teamExists(UUID teamId) {
        String sql = "SELECT COUNT(*) FROM teams WHERE team_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, teamId);
        return count != null && count > 0;
    }

    public boolean existsByTeamName(String teamName) {
        String sql = "SELECT COUNT(*) FROM teams WHERE team_name = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, teamName);
        return count != null && count > 0;
    }

    public Team save(Team team) {
        if (team.getTeamId() == null) {
            // Create new team
            UUID newId = UUID.randomUUID();
            team.setTeamId(newId);
            String insertSql = "INSERT INTO teams (team_id, team_name, created_by, created_at) VALUES (?, ?, ?, ?)";
            jdbcTemplate.update(insertSql,
                    newId,
                    team.getTeamName(),
                    team.getCreatedBy(),
                    Timestamp.valueOf(team.getCreatedAt()));
        } else {
            // Update existing team
            String updateSql = "UPDATE teams SET team_name = ? WHERE team_id = ?";
            jdbcTemplate.update(updateSql, team.getTeamName(), team.getTeamId());
        }
        return team;
    }

    public List<Team> findByCreatedBy(UUID userId) {
        String sql = "SELECT * FROM teams WHERE created_by = ?";
        return jdbcTemplate.query(sql, teamRowMapper, userId);
    }

    public Optional<Team> findById(UUID teamId) {
        String sql = "SELECT * FROM teams WHERE team_id = ?";
        List<Team> results = jdbcTemplate.query(sql, teamRowMapper, teamId);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public List<Team> findAll() {
        String sql = "SELECT * FROM teams";
        return jdbcTemplate.query(sql, teamRowMapper);
    }
    public List<Team> findMembershipsByUser(UUID userId) {
        String sql = """
        SELECT t.* 
        FROM teams t
        JOIN team_members tm ON t.team_id = tm.team_id
        WHERE tm.user_id = ? AND t.created_by <> ?
    """;
        return jdbcTemplate.query(sql, teamRowMapper, userId, userId);
    }

    public void deleteById(UUID teamId) {
        String sql = "DELETE FROM teams WHERE team_id = ?";
        jdbcTemplate.update(sql, teamId);
    }


}
