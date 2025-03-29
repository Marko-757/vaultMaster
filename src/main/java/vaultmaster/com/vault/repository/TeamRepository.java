package vaultmaster.com.vault.repository;

import org.springframework.data.repository.CrudRepository;
import vaultmaster.com.vault.model.Team;

import java.util.List;
import java.util.UUID;

public interface TeamRepository extends CrudRepository<Team, UUID> {

    // Find all teams created by a specific user
    List<Team> findByCreatedBy(UUID createdBy);

    // Check if a team exists by its name
    boolean existsByTeamName(String teamName);

}
