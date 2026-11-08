package reading_tracker.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import reading_tracker.domain.User;
import reading_tracker.repository.UserRepository;

@SpringBootTest(classes = reading_tracker.ReadingTrackerTestApplication.class)
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=none")
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    // Checks that an HTTP signup request saves a new user with an encoded password.
    void registerSavesUserWithEncodedPassword() throws Exception {
        String username = "reader-" + UUID.randomUUID();
        String password = "secure-pass";

        mockMvc.perform(post("/signup")
                        .with(csrf())
                        .param("username", username)
                        .param("password", password)
                        .param("email", "reader@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered"));

        User savedUser = userRepository.findByUsername(username).orElseThrow();
        assertEquals(username, savedUser.getUsername());
        assertNotEquals(password, savedUser.getPassword());
        assertTrue(passwordEncoder.matches(password, savedUser.getPassword()));
        assertEquals("USER", savedUser.getRole());
    }

    @Test
    // Checks that an HTTP signup request rejects a username that already exists in PostgreSQL.
    void registerRejectsAnExistingUsername() throws Exception {
        String username = "reader-" + UUID.randomUUID();
        userRepository.saveAndFlush(new User(username, "existing-password", null, "USER"));

        mockMvc.perform(post("/signup")
                        .with(csrf())
                        .param("username", username)
                        .param("password", "secure-pass")
                        .param("email", "reader@example.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "username"));
    }
}
