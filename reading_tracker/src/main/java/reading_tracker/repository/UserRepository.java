package reading_tracker.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import reading_tracker.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}
