package reading_tracker.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import reading_tracker.domain.Book;

public interface BookRepository extends CrudRepository<Book, Long> {

    List<Book> findAllByOwner_Username(String username);

    Optional<Book> findByIdAndOwner_Username(Long id, String username);
}