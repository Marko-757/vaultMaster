package vaultmaster.com.vault.repository;

import org.springframework.data.repository.CrudRepository;
import vaultmaster.com.vault.model.User;

import java.util.UUID;

public interface UserRepository extends CrudRepository<User, UUID> {
    // Find a user by email
    User findByEmail(String email);

    // Check if a user exists by email
    boolean existsByEmail(String email);
}
