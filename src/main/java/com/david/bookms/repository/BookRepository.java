package com.david.bookms.repository;

import com.david.bookms.model.Book;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends CrudRepository<Book,Long>, CustomBookRepository {

    List<Book> findByTitleIgnoreCase(String title);
    List<Book> findByAuthorsIgnoreCaseIn(List<String> author);
    List<Book> findByIsbn(String isbn);
    List<Book> findAll();
}
