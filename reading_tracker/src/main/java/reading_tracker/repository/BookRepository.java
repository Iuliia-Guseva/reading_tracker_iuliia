package reading_tracker.repository;

import org.springframework.data.repository.CrudRepository;

import reading_tracker.domain.Book;

public interface BookRepository extends CrudRepository<Book, Long> {

}