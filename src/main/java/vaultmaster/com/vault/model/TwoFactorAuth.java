package vaultmaster.com.vault.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("two_factor_auth")
public class TwoFactorAuth {

    @Id
    private UUID authId; // Spring will auto-generate this if you omit it on save
    private UUID userId;
    private String token;
    private LocalDateTime expiresAt;
    private boolean isValid;
    private LocalDateTime createdAt;
}
