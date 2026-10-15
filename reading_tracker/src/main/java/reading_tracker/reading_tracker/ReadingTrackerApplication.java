package reading_tracker.reading_tracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "reading_tracker")
public class ReadingTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReadingTrackerApplication.class, args);
	}

}
