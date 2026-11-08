package reading_tracker.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class ReadingSession {

    @Id
    @GeneratedValue ( strategy =  GenerationType.IDENTITY)
    private  Long id;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer duration;

    public ReadingSession() {
    }

    public ReadingSession(LocalDateTime startTime,
                        LocalDateTime endTime,
                        Integer duration) {

        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
}
    // getters and setters
    public Long getId() {
    return id;
}

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
}

