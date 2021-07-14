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

import static org.junit.jupiter.api.Assertions.assertEquals;


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
        assertEquals(1,dbBook.getId());
        bookRepository.delete(dbBook);
        assertEquals(Optional.empty(),bookRepository.findById(1L));
    }
    @Test
    public void findByTitle(){
        bookRepository.saveAll(getAllBooks());
        List<Book> books= bookRepository.findByTitleIgnoreCase("Java");
        assertEquals(1,books.size());
        assertEquals("Java",books.get(0).getTitle());
    }
    @Test
    public void findByAuthor(){
        bookRepository.saveAll(getAllBooks());
        List<Book> books= bookRepository.findByAuthorsIgnoreCaseIn(Arrays.asList("David"));
        assertEquals(1,books.size());
        assertEquals("David",books.get(0).getAuthors().get(0));
    }
    @Test
    public void findByISBN(){
        bookRepository.saveAll(getAllBooks());
        List<Book> books= bookRepository.findByIsbn("1234567890123");
        assertEquals(3,books.size());
        assertEquals("1234567890123",books.get(0).getIsbn());
    }

    @Test
    public void search(){
        bookRepository.saveAll(getAllBooks());
        assertEquals(1,bookRepository.search("David",null,null).size());
        assertEquals(1,bookRepository.search(null,"Java",null).size());
        assertEquals(3,bookRepository.search(null,null,"1234567890123").size());

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