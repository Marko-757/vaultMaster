package vaultmaster.com.vault.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import vaultmaster.com.vault.model.TwoFactorAuthentication;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TwoFactorAuthenticationRepository extends CrudRepository<TwoFactorAuthentication, Long> {
    Optional<TwoFactorAuthentication> findByUserId(Long userId);

    void deleteByExpiresBefore(LocalDateTime now);
}
