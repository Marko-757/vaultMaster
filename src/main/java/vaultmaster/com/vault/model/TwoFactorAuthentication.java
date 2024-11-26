package vaultmaster.com.vault.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("two_factor_auth")
public class TwoFactorAuthentication {

    @Id
    private Long authId;
    private Long userId;
    private String token;
    private LocalDateTime expires;
    private Boolean isValid;

}
