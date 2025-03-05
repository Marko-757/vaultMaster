package vaultmaster.com.vault.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@Table("team_members")  // Map to your database table
public class TeamMember {

    // Getters and Setters
    @Id  // Indicates this is the primary key (composite key)
    private TeamMemberId id;  // Composite primary key class

    private String role;

    @Column("created_date")
    private LocalDateTime createdDate;

    @Column("modified_date")
    private LocalDateTime modifiedDate;

    @Column("created_by")
    private String createdBy;

    @Column("modified_by")
    private String modifiedBy;

}
