package vaultmaster.com.vault.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import vaultmaster.com.vault.model.Team;

import java.util.List;
import java.util.UUID;

public interface TeamRepository extends CrudRepository<Team, UUID> {

    /**
     * Find all teams created by a specific user.
     *
     * @param createdBy the UUID of the user who created the teams.
     * @return a list of teams created by the specified user.
     */
    @Query("SELECT * FROM teams WHERE created_by = :createdBy")
    List<Team> findByCreatedBy(UUID createdBy);

    /**
     * Find a team by its name.
     *
     * @param teamName the name of the team.
     * @return the team with the specified name.
     */
    @Query("SELECT * FROM teams WHERE team_name = :teamName")
    Team findByTeamName(String teamName);

    /**
     * Check if a team exists by its name.
     *
     * @param teamName the name of the team.
     * @return true if a team with the specified name exists, otherwise false.
     */
    boolean existsByTeamName(String teamName);
}
