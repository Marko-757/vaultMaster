package vaultmaster.com.vault.repository;

import vaultmaster.com.vault.model.TwoFactorAuth;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TwoFactorAuthRepository extends CrudRepository<TwoFactorAuth, UUID> {
    Optional<TwoFactorAuth> findTopByUserIdOrderByCreatedAtDesc(UUID userId);
}
