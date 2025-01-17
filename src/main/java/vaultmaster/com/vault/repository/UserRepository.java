package vaultmaster.com.vault.repository;

import org.springframework.data.repository.CrudRepository;
import vaultmaster.com.vault.model.User;

public interface UserRepository extends CrudRepository<User, Long> {
    // Find a user by email
    User findByEmail(String email);

    // Check if a user exists by email
    boolean existsByEmail(String email);
}
