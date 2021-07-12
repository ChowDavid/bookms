package com.david.bookms.repository;

import com.david.bookms.model.Book;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "book", path="books")
public interface BookRepository extends PagingAndSortingRepository<Book,Long> {

    List<Book> findByTitle(@Param("title") String title);
    List<Book> findByAuthorsIn(@Param("authors") List<String> author);
    List<Book> findByIsbn(@Param("ISBN") String isbn);
}
