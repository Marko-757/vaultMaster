package vaultmaster.com.vault.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@Table("teams") // Map to the "teams" table in the database
public class Team {

    @Id
    @Column("team_id")
    private UUID teamId;

    @Column("team_name")
    @NotNull(message = "Team name cannot be null")
    @Size(min = 3, max = 255, message = "Team name must be between 3 and 255 characters")
    private String teamName;

    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("created_by")
    private UUID createdBy;

}
