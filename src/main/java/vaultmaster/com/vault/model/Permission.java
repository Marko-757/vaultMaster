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
@Table(name = "permissions")

public class Permission {
    @Id
    @GeneratedValue
    private UUID permissionId;

    private String permissionName;
}

