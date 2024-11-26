package vaultmaster.com.vault.repository;

import org.springframework.data.repository.CrudRepository;
import vaultmaster.com.vault.model.User;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    // Spring Data JDBC will provide basic CRUD operations automatically.
    User findByEmail(String email);
}
