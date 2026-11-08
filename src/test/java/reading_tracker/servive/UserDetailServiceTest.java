package reading_tracker.servive;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import reading_tracker.domain.User;
import reading_tracker.repository.UserRepository;

@SpringBootTest(classes = reading_tracker.ReadingTrackerTestApplication.class)
@Transactional
@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=none")
class UserDetailServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailService userDetailService;

    // Checks that a user saved to PostgreSQL is loaded with the correct credentials and role.
    @Test
    void loadUserByUsernameReturnsCredentialsAndRoleFromDatabaseUser() {
        String username = "test-" + UUID.randomUUID();
        User user = new User(username, "encoded-password", null, "USER");
        userRepository.saveAndFlush(user);

        var userDetails = userDetailService.loadUserByUsername(username);

        assertEquals(username, userDetails.getUsername());
        assertEquals("encoded-password", userDetails.getPassword());
        assertEquals("ROLE_USER", userDetails.getAuthorities().iterator().next().getAuthority());
    }

    // Checks that looking up a username absent from PostgreSQL raises an authentication exception.
    @Test
    void loadUserByUsernameThrowsWhenUserDoesNotExist() {
        String username = "missing-" + UUID.randomUUID();

        assertThrows(UsernameNotFoundException.class,
                () -> userDetailService.loadUserByUsername(username));
    }
}
