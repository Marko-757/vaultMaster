package vaultmaster.com.vault.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@Table("teams") // Map to the "teams" table in the database
public class Team {

    // Getters and Setters
    @Id
    @Column("team_id")
    private UUID teamId;

    @Column("team_name")
    private String teamName;

    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("created_by")
    private UUID createdBy;

}
