package reading_tracker.config;

import java.io.IOException;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import reading_tracker.domain.User;
import reading_tracker.repository.UserRepository;

@Component
public class DemoUserInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DefaultDataInitializer defaultDataInitializer;

    public DemoUserInitializer(UserRepository userRepository,
                               PasswordEncoder passwordEncoder,
                               DefaultDataInitializer defaultDataInitializer) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.defaultDataInitializer = defaultDataInitializer;
    }

    @Override
    public void run(ApplicationArguments args) throws IOException {
        User user = userRepository.findByUsername("user").orElse(null);
        boolean newUser = user == null;
        if (newUser) {
            user = new User("user", "", null, "USER");
        }
        user.setPassword(passwordEncoder.encode("user"));
        user.setRole("USER");
        user = userRepository.save(user);

        if (newUser) {
            defaultDataInitializer.initializeFor(user);
        }
    }
}
