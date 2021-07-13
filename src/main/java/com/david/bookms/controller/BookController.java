package com.david.bookms.controller;

import com.david.bookms.controller.dto.BookDto;
import com.david.bookms.exception.NoBookFoundExcpetion;
import com.david.bookms.handler.ErrorResponse;
import com.david.bookms.model.Book;
import com.david.bookms.repository.BookRepository;
import com.david.bookms.service.EventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequestMapping("/books")
@RestController
public class BookController {

    @Autowired
    private EventService eventService;
    @Autowired
    private BookRepository bookRepository;

    /**
     * Create Book
     * @param book
     * @return
     */
    @PostMapping()
    public Book saveBook(@RequestBody @Valid BookDto book){
        Book dbBook = new Book();
        BeanUtils.copyProperties(book,dbBook);
        Book savedBook = bookRepository.save(dbBook);
        eventService.bookCreate(savedBook);
        return savedBook;
    }

    /**
     * lookup book
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Book getBookById(@PathVariable("id") Long id){
        Book book = bookRepository.findById(id).orElse(null);
        if (book!=null) {
            eventService.bookSearch(book);
            return book;
        } else {
            throw new NoBookFoundExcpetion("Book not found by id="+id);
        }
    }

    /**
     * Delete Book
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Book deleteBook(@PathVariable("id") Long id){
        Book book = bookRepository.findById(id).orElse(null);
        if (book!=null) {
            bookRepository.delete(book);
            eventService.bookDelete(book);
            return book;
        } else {
            throw new NoBookFoundExcpetion("Book not found by id="+id);
        }
    }

    /**
     * update book
     * @return
     */
    @PutMapping("/{id}")
    public Book updateBook(@PathVariable("id") Long id,@RequestBody @Valid BookDto book){
        Book dbBook = bookRepository.findById(id).orElse(null);
        if (book!=null) {
            BeanUtils.copyProperties(book,dbBook,"id");
            bookRepository.save(dbBook);
            eventService.bookModify(dbBook);
            return dbBook;
        } else {
            throw new NoBookFoundExcpetion("Book not found by id="+id);
        }
    }

    @GetMapping("/searchByAuthor/{author}")
    public List<Book> searchByAuthor(@PathVariable("author") String author){
        List<Book> dbBooks = bookRepository.findByAuthorsIgnoreCaseIn(Arrays.asList(author));
        if (dbBooks!=null) {
            dbBooks.stream().forEach(b->eventService.bookSearch(b));
            return dbBooks;
        } else {
            throw new NoBookFoundExcpetion("Book not found by author="+author);
        }
    }

    @GetMapping("/searchByTitle/{title}")
    public List<Book> searchByTitle(@PathVariable("title") String title){
        List<Book> dbBooks = bookRepository.findByTitleIgnoreCase(title);
        if (dbBooks!=null) {
            dbBooks.stream().forEach(b->eventService.bookSearch(b));
            return dbBooks;
        } else {
            throw new NoBookFoundExcpetion("Book not found by title="+title);
        }
    }
    @GetMapping("/searchByISBN/{isbn}")
    public List<Book> searchByIsbn(@PathVariable("isbn") String isbn){
        List<Book> dbBooks = bookRepository.findByIsbn(isbn);
        if (dbBooks!=null) {
            dbBooks.stream().forEach(b->eventService.bookSearch(b));
            return dbBooks;
        } else {
            throw new NoBookFoundExcpetion("Book not found by ISBN="+ isbn);
        }
    }


    @GetMapping()
    public Book get(){
        return new Book();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public final ResponseEntity<ErrorResponse> validationErrorException(MethodArgumentNotValidException ex, WebRequest request){
        log.error("validationErrorException found {}",ex.getMessage());
        List<String> details = new ArrayList<>();
        details.addAll(ex.getBindingResult().getAllErrors().stream().map(e->(FieldError)e).map(e->"Field "+e.getField()+"-"+e.getDefaultMessage()).collect(Collectors.toList()));
        ErrorResponse error = new ErrorResponse("Validation Error", details);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ErrorResponse> handAllOtherException(Exception ex, WebRequest request) {
        log.error("handAllOtherException found {}",ex.getMessage());
        List<String> details = new ArrayList<>();
        details.add(ex.getMessage());
        ErrorResponse error = new ErrorResponse("Exception", details);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

}
