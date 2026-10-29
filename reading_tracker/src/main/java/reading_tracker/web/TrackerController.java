package reading_tracker.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;

import reading_tracker.config.DefaultDataInitializer;
import reading_tracker.domain.Book;
import reading_tracker.domain.BookRepository;
import reading_tracker.domain.BookStatus;

@Controller
public class TrackerController {

    private static final int MAX_COVER_SIZE = 5 * 1024 * 1024;
    private static final Set<String> ALLOWED_COVER_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp");

    private final BookRepository bookRepository;
    private final DefaultDataInitializer defaultDataInitializer;

    public TrackerController(BookRepository bookRepository, DefaultDataInitializer defaultDataInitializer) {
        this.bookRepository = bookRepository;
        this.defaultDataInitializer = defaultDataInitializer;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/tracker";
    }
    
    @GetMapping("/tracker")
    public String main_page(Model model) {
        model.addAttribute("books", getBooks());
        return "reading_tracker_page";
    }

    @GetMapping("/reading-session")
    public String readingSession(Model model) {
        model.addAttribute("sampleReadingSeconds", defaultDataInitializer.getDefaultReadingSeconds());
        return "ReadingSession";
    }

    @GetMapping("/statistics")
    public String statistics(Model model) {
        model.addAttribute("sampleReadingSeconds", defaultDataInitializer.getDefaultReadingSeconds());
        return "Statistics";
    }

    @GetMapping("/settings")
    public String settings() {
        return "Settings";
    }
    
    @GetMapping("/my-books")
    public String myBooks(Model model) {
        prepareMyBooksPage(model, new Book());
        return "MyBooksList";
    }

    @GetMapping("/my-books/{id}/edit")
    public String editBookPage(@PathVariable Long id, Model model) {
        Book book = findBook(id);
        prepareEditPage(model, book, book);
        return "MyBooksList";
    }

    @PostMapping("/my-books")
    public String addBook(@ModelAttribute("newBook") Book book,
                          @RequestParam(name = "cover", required = false) MultipartFile cover,
                          Model model) throws IOException {
        if (cover != null && !cover.isEmpty()) {
            byte[] image = cover.getBytes();
            String contentType = cover.getContentType();
            // Check the size and actual image signature before saving the uploaded file.
            if (!isValidCover(image, contentType)) {
                prepareMyBooksPage(model, book);
                model.addAttribute("coverError",
                        "Choose a JPEG, PNG, GIF or WebP image smaller than 5 MB.");
                return "MyBooksList";
            }
            book.setCoverImage(image);
            book.setCoverContentType(contentType);
        }
        bookRepository.save(book);
        return "redirect:/my-books";
    }

    @PostMapping("/my-books/{id}/edit")
    public String updateBook(@PathVariable Long id,
                             @ModelAttribute("newBook") Book formBook,
                             @RequestParam(name = "cover", required = false) MultipartFile cover,
                             Model model) throws IOException {
        Book savedBook = findBook(id);
        // Keep the saved cover unless a valid replacement was uploaded.
        if (cover != null && !cover.isEmpty()) {
            byte[] image = cover.getBytes();
            String contentType = cover.getContentType();
            if (!isValidCover(image, contentType)) {
                prepareEditPage(model, formBook, savedBook);
                model.addAttribute("coverError",
                        "Choose a JPEG, PNG, GIF or WebP image smaller than 5 MB.");
                return "MyBooksList";
            }
            savedBook.setCoverImage(image);
            savedBook.setCoverContentType(contentType);
        }

        // Update only fields exposed by the form; retain other saved book details.
        savedBook.setTitle(formBook.getTitle());
        savedBook.setAuthor(formBook.getAuthor());
        savedBook.setPublicationYear(formBook.getPublicationYear());
        savedBook.setPages(formBook.getPages());
        savedBook.setStatus(formBook.getStatus());
        bookRepository.save(savedBook);
        return "redirect:/my-books";
    }

    @GetMapping("/my-books/{id}/cover")
    public ResponseEntity<byte[]> bookCover(@PathVariable Long id) {
        Book book = findBook(id);
        // Return stored image bytes using their validated media type.
        if (book.getCoverImage() == null || book.getCoverContentType() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(book.getCoverContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .header("X-Content-Type-Options", "nosniff")
                .body(book.getCoverImage());
    }

    private void prepareMyBooksPage(Model model, Book newBook) {
        model.addAttribute("books", getBooks());
        if (newBook.getStatus() == null) {
            newBook.setStatus(BookStatus.TO_READ);
        }
        model.addAttribute("newBook", newBook);
        model.addAttribute("bookStatuses", BookStatus.values());
        model.addAttribute("bookFormAction", "/my-books");
        model.addAttribute("bookFormTitle", "Add book");
    }

    private void prepareEditPage(Model model, Book formBook, Book savedBook) {
        prepareMyBooksPage(model, formBook);
        formBook.setId(savedBook.getId());
        // Restore the saved cover for the edit preview and after validation errors.
        formBook.setCoverImage(savedBook.getCoverImage());
        formBook.setCoverContentType(savedBook.getCoverContentType());
        model.addAttribute("editBookId", savedBook.getId());
        model.addAttribute("bookFormAction", "/my-books/" + savedBook.getId() + "/edit");
        model.addAttribute("bookFormTitle", "Edit book");
    }

    private Book findBook(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    private boolean isValidCover(byte[] image, String contentType) {
        return image.length <= MAX_COVER_SIZE && isSupportedImage(image, contentType);
    }

    private boolean isSupportedImage(byte[] image, String contentType) {
        if (contentType == null || !ALLOWED_COVER_TYPES.contains(contentType)) {
            return false;
        }
        // Verify each format's file signature so a renamed non-image file is rejected.
        return switch (contentType) {
            case "image/jpeg" -> image.length >= 3
                    && (image[0] & 0xFF) == 0xFF && (image[1] & 0xFF) == 0xD8
                    && (image[2] & 0xFF) == 0xFF;
            case "image/png" -> image.length >= 8
                    && (image[0] & 0xFF) == 0x89 && image[1] == 0x50
                    && image[2] == 0x4E && image[3] == 0x47
                    && image[4] == 0x0D && image[5] == 0x0A
                    && image[6] == 0x1A && image[7] == 0x0A;
            case "image/gif" -> image.length >= 6
                    && image[0] == 'G' && image[1] == 'I' && image[2] == 'F'
                    && image[3] == '8' && (image[4] == '7' || image[4] == '9')
                    && image[5] == 'a';
            case "image/webp" -> image.length >= 12
                    && image[0] == 'R' && image[1] == 'I' && image[2] == 'F'
                    && image[3] == 'F' && image[8] == 'W' && image[9] == 'E'
                    && image[10] == 'B' && image[11] == 'P';
            default -> false;
        };
    }

    private List<Book> getBooks() {
        List<Book> books = new ArrayList<>();
        bookRepository.findAll().forEach(books::add);
        return books;
    }
}
