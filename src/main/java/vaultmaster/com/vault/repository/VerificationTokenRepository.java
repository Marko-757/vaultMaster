package vaultmaster.com.vault.repository;

import org.springframework.data.repository.CrudRepository;
import vaultmaster.com.vault.model.VerificationToken;

import java.util.Optional;
import java.util.UUID;

public interface VerificationTokenRepository extends CrudRepository<VerificationToken, UUID> {
    Optional<VerificationToken> findByToken(String token);
}
