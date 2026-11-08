package reading_tracker.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import reading_tracker.domain.Book;
import reading_tracker.domain.BookStatus;
import reading_tracker.domain.User;
import reading_tracker.repository.BookRepository;

@Component
public class DefaultDataInitializer {

    // Default reading time shown by the Statistics page.
    private static final long DEFAULT_READING_SECONDS = 15 * 60 * 60;

    private final BookRepository bookRepository;

    public DefaultDataInitializer(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public long getDefaultReadingSeconds() {
        return DEFAULT_READING_SECONDS;
    }

    public void initializeFor(User user) throws IOException {
        List<Book> books = List.of(
                sampleBook("Flowers for Algernon", "Daniel Keyes", 1966, 311,
                        5, true, BookStatus.COMPLETED, "flowers-for-algernon.jpg", user),
                sampleBook("1984", "George Orwell", 1949, 328,
                        4, false, BookStatus.READING, "1984.jpeg", user),
                sampleBook("Clean Code", "Robert C. Martin", 2008, 464,
                        0, false, BookStatus.TO_READ, "clean-code.jpeg", user));
        bookRepository.saveAll(books);
    }

    private Book sampleBook(String title, String author, int publicationYear, int pages,
                            int rating, boolean favorite, BookStatus status, String coverFile, User owner)
            throws IOException {
        Book book = new Book(title, author, publicationYear, pages, rating, favorite, status);
        book.setOwner(owner);
        ClassPathResource cover = new ClassPathResource("static/images/" + coverFile);
        try (InputStream input = cover.getInputStream()) {
            book.setCoverImage(input.readAllBytes());
        }
        book.setCoverContentType("image/jpeg");
        return book;
    }
}
