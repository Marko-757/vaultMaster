package vaultmaster.com.vault.model;

import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

import lombok.*;

@Setter
@Getter
@Table("roles")
public class Role {

    @Id
    @Column("role_id")
    private UUID roleId;

    @Column("role_name")
    @NotNull(message = "Role name cannot be null")
    @Size(min = 3, max = 255, message = "Role name must be between 3 and 255 characters")
    private String roleName;

    @Column("team_id")
    private UUID teamId;

    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("created_by")
    private UUID createdBy;

}

 /*   private String roleName;

    public UUID getRoleId() {
        return roleId;
    }

    public void setRoleId(UUID roleId) {
        this.roleId = roleId;
    }

    public UUID getTeamId() {
        return teamId;
    }

    public void setTeamId(UUID teamId) {
        this.teamId = teamId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
  */