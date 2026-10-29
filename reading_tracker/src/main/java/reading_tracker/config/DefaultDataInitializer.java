package reading_tracker.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import reading_tracker.domain.Book;
import reading_tracker.domain.BookRepository;
import reading_tracker.domain.BookStatus;

@Component
public class DefaultDataInitializer implements ApplicationRunner {

    // Default reading time shown by the Statistics page.
    private static final long DEFAULT_READING_SECONDS = 15 * 60 * 60;

    private final BookRepository bookRepository;

    public DefaultDataInitializer(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public long getDefaultReadingSeconds() {
        return DEFAULT_READING_SECONDS;
    }

    @Override
    public void run(ApplicationArguments args) throws IOException {
        // Add the default books only once, without replacing books the user has saved.
        if (bookRepository.count() == 0) {
            bookRepository.saveAll(List.of(
                    sampleBook("Flowers for Algernon", "Daniel Keyes", 1966, 311,
                            5, true, BookStatus.COMPLETED, "flowers-for-algernon.jpg"),
                    sampleBook("1984", "George Orwell", 1949, 328,
                            4, false, BookStatus.READING, "1984.jpeg"),
                    sampleBook("Clean Code", "Robert C. Martin", 2008, 464,
                            0, false, BookStatus.TO_READ, "clean-code.jpeg")));
        }
    }

    private Book sampleBook(String title, String author, int publicationYear, int pages,
                            int rating, boolean favorite, BookStatus status, String coverFile)
            throws IOException {
        Book book = new Book(title, author, publicationYear, pages, rating, favorite, status);
        ClassPathResource cover = new ClassPathResource("static/images/" + coverFile);
        try (InputStream input = cover.getInputStream()) {
            book.setCoverImage(input.readAllBytes());
        }
        book.setCoverContentType("image/jpeg");
        return book;
    }
}
