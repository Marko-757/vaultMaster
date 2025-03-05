package vaultmaster.com.vault.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "roles")

public class Role {
    @Id
    @GeneratedValue
    private UUID roleId;

    private String roleName;
    private UUID createdBy;
    private Date createdAt;
}

