package reading_tracker;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.persistence.autoconfigure.EntityScan;

import reading_tracker.config.DefaultDataInitializer;
import reading_tracker.security.SecurityConfig;
import reading_tracker.servive.UserDetailService;
import reading_tracker.web.AccountController;
import reading_tracker.web.TrackerController;

@SpringBootConfiguration
@EnableAutoConfiguration
@EntityScan("reading_tracker.domain")
@EnableJpaRepositories("reading_tracker.repository")
@Import({
        AccountController.class,
        TrackerController.class,
        DefaultDataInitializer.class,
        SecurityConfig.class,
        UserDetailService.class
})
public class ReadingTrackerTestApplication {
}
