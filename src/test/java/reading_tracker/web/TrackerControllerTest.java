package reading_tracker.web;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import reading_tracker.domain.Book;
import reading_tracker.domain.BookStatus;
import reading_tracker.domain.User;
import reading_tracker.repository.BookRepository;
import reading_tracker.repository.UserRepository;

@SpringBootTest(classes = reading_tracker.ReadingTrackerTestApplication.class)
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=none")
class TrackerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    // Checks that an authenticated HTTP request receives statistics based only on completed books.
    void statisticsCountsPagesFromCompletedBooksOnly() throws Exception {
        User reader = createUser("reader");
        Book completed = book(reader, "Finished", BookStatus.COMPLETED, 240);
        Book currentlyReading = book(reader, "In progress", BookStatus.READING, 150);
        Book completedWithoutPageCount = book(reader, "Unknown length", BookStatus.COMPLETED, null);
        bookRepository.saveAll(List.of(completed, currentlyReading, completedWithoutPageCount));

        mockMvc.perform(get("/statistics").with(user(reader.getUsername()).roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("Statistics"))
                .andExpect(model().attribute("pagesRead", 240))
                .andExpect(model().attributeExists("books", "sampleReadingSeconds"));
    }

    @Test
    // Checks that an authenticated user cannot retrieve a book cover they do not own.
    void bookCoverIsNotFoundWhenBookIsNotOwnedByCurrentUser() throws Exception {
        User owner = createUser("owner");
        User reader = createUser("reader");
        Book savedBook = bookRepository.save(book(owner, "Private cover", BookStatus.TO_READ, 100));

        mockMvc.perform(get("/my-books/{id}/cover", savedBook.getId())
                        .with(user(reader.getUsername()).roles("USER")))
                .andExpect(status().isNotFound());
    }

    @Test
    // Checks that Spring Security redirects unauthenticated requests for statistics to the login page.
    void statisticsRedirectsUnauthenticatedUsersToLogin() throws Exception {
        mockMvc.perform(get("/statistics"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    private User createUser(String prefix) {
        return userRepository.saveAndFlush(
                new User(prefix + "-" + UUID.randomUUID(), "test-password", null, "USER"));
    }

    private Book book(User owner, String title, BookStatus status, Integer pages) {
        Book book = new Book();
        book.setOwner(owner);
        book.setTitle(title);
        book.setStatus(status);
        book.setPages(pages);
        return book;
    }
}
