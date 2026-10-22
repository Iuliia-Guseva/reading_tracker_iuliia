package reading_tracker.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import reading_tracker.domain.Book;
import reading_tracker.domain.BookRepository;
import reading_tracker.domain.BookStatus;

@Controller
public class TrackerController {

    private final BookRepository bookRepository;

    public TrackerController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
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
    public String readingSession() {
        return "ReadingSession";
    }

    @GetMapping("/statistics")
    public String statistics() {
        return "Statistics";
    }

    @GetMapping("/settings")
    public String settings() {
        return "Settings";
    }
    
    @GetMapping("/my-books")
    public String myBooks(Model model) {
        model.addAttribute("books", getBooks());
        Book newBook = new Book();
        newBook.setStatus(BookStatus.TO_READ);
        model.addAttribute("newBook", newBook);
        model.addAttribute("bookStatuses", BookStatus.values());
        return "MyBooksList";
    }

    @PostMapping("/my-books")
    public String addBook(@ModelAttribute("newBook") Book book) {
        bookRepository.save(book);
        return "redirect:/my-books";
    }

    private List<Book> getBooks() {
        List<Book> books = new ArrayList<>();
        bookRepository.findAll().forEach(books::add);
        return books;
    }
}
