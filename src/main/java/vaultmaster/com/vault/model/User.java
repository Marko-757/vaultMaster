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
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    private UUID userId;

    private String passwordHash;
    private String fullName;
    private String phoneNumber;
    private String email;
    private Date createdDate;
    private Date modifiedDate;
    private UUID createdBy;
    private UUID modifiedBy;
    private boolean verified;
}
