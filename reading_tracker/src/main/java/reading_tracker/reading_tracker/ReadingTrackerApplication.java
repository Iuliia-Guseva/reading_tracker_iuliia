package reading_tracker.reading_tracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "reading_tracker")
@EntityScan(basePackages = "reading_tracker.domain")
@EnableJpaRepositories(basePackages = "reading_tracker.domain")
public class ReadingTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReadingTrackerApplication.class, args);
	}

}
