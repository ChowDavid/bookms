package com.david.bookms.repository;

import com.david.bookms.model.Book;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    public void cleanup(){
        bookRepository.deleteAll();
    }

    @Test
    public void save(){
        Book book = new Book();
        Book dbBook = bookRepository.save(book);
        Assertions.assertTrue(dbBook.getId()>0);
    }
    @Test
    public void delete(){
        Book book = new Book();
        Book dbBook = bookRepository.save(book);
        Assertions.assertEquals(1,dbBook.getId());
        bookRepository.delete(dbBook);
        Assertions.assertEquals(Optional.empty(),bookRepository.findById(1L));
    }
    @Test
    public void findByTitle(){
        bookRepository.saveAll(getAllBooks());
        List<Book> books= bookRepository.findByTitleIgnoreCase("Java");
        Assertions.assertEquals(1,books.size());
        Assertions.assertEquals("Java",books.get(0).getTitle());
    }
    @Test
    public void findByAuthor(){
        bookRepository.saveAll(getAllBooks());
        List<Book> books= bookRepository.findByAuthorsIgnoreCaseIn(Arrays.asList("David"));
        Assertions.assertEquals(1,books.size());
        Assertions.assertEquals("David",books.get(0).getAuthors().get(0));
    }
    @Test
    public void findByISBN(){
        bookRepository.saveAll(getAllBooks());
        List<Book> books= bookRepository.findByIsbn("1234567890123");
        Assertions.assertEquals(3,books.size());
        Assertions.assertEquals("1234567890123",books.get(0).getIsbn());
    }


    private List<Book> getAllBooks() {
        Book book1 = new Book();
        book1.setIsbn("1234567890123");
        book1.setTitle("Java");
        Book book2 = new Book();
        book2.setIsbn("1234567890123");
        book2.setAuthors(Arrays.asList("David"));
        Book book3 = new Book();
        book3.setIsbn("1234567890123");
        return Arrays.asList(book1,book2,book3);
    }

}