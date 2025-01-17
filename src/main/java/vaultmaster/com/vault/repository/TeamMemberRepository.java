package vaultmaster.com.vault.repository;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import vaultmaster.com.vault.model.TeamMember;
import vaultmaster.com.vault.model.TeamMemberId;

import java.util.List;
import java.util.UUID;

public interface TeamMemberRepository extends CrudRepository<TeamMember, TeamMemberId> {

    @Query("SELECT * FROM team_members WHERE team_id = :teamId")
    List<TeamMember> findByTeamId(UUID teamId);

    @Query("SELECT * FROM team_members WHERE user_id = :userId")
    List<TeamMember> findByUserId(UUID userId);

    @Modifying
    @Query("INSERT INTO team_members (team_id, user_id, role, created_date, modified_date, created_by, modified_by) " +
            "VALUES (:#{#teamMember.id.teamId}, :#{#teamMember.id.userId}, :#{#teamMember.role}, " +
            ":#{#teamMember.createdDate}, :#{#teamMember.modifiedDate}, :#{#teamMember.createdBy}, :#{#teamMember.modifiedBy})")
    void insert(TeamMember teamMember);
}
