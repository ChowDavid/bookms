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
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
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
     * All books
     * @return
     */
    @GetMapping()
    public List<Book> findAll(){
        List<Book> books = bookRepository.findAll();
        books.stream().forEach(b->eventService.bookSearch(b));
        return books;
    }

    /**
     * Create Book
     * @param book
     * @return
     */
    @PostMapping()
    public Book saveBook(@RequestBody @Valid BookDto book){
        log.info("saveBook");
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
        log.info("bookSearch by Id {}",id);
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
        log.info("book delete by id {}",id);
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
        log.info("book update by id {}",id);
        Book dbBook = bookRepository.findById(id).orElse(null);
        Book before = new Book();
        if (dbBook!=null) {
            BeanUtils.copyProperties(dbBook,before);
            BeanUtils.copyProperties(book,dbBook,"id");
            bookRepository.save(dbBook);
            eventService.bookModify(before,dbBook);
            return dbBook;
        } else {
            throw new NoBookFoundExcpetion("Book not found by id="+id);
        }
    }

    @GetMapping("/searchByAuthor/{author}")
    public List<Book> searchByAuthor(@PathVariable("author") String author){
        log.info("search by author {}",author);
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
        log.info("search by title {}",title);
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
        log.info("search by Isbn {}",isbn);
        List<Book> dbBooks = bookRepository.findByIsbn(isbn);
        if (dbBooks!=null) {
            dbBooks.stream().forEach(b->eventService.bookSearch(b));
            return dbBooks;
        } else {
            throw new NoBookFoundExcpetion("Book not found by ISBN="+ isbn);
        }
    }

    @GetMapping("/search")
    public List<Book> search(
            @RequestParam(name="title",required = false) String title,
            @RequestParam(name="author",required = false) String author,
            @RequestParam(name="isbn",required = false) String isbn
            ){
        log.info("Search {} {} {}",title,author,isbn);
        List<Book> dbBooks = bookRepository.search(author,title,isbn);
        if (dbBooks==null || dbBooks.isEmpty()){
            throw new NoBookFoundExcpetion(String.format("Book not found %s %s %s",title,author,isbn));
        } else {
            dbBooks.stream().forEach(b->eventService.bookSearch(b));
            return dbBooks;
        }
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public final ResponseEntity<ErrorResponse> validationErrorException(MethodArgumentNotValidException ex, WebRequest request){
        log.warn("validationErrorException found {}",ex.getMessage());
        List<String> details = new ArrayList<>();
        details.addAll(ex.getBindingResult().getAllErrors().stream().map(e->(FieldError)e).map(e->"Field "+e.getField()+"-"+e.getDefaultMessage()).collect(Collectors.toList()));
        ErrorResponse error = new ErrorResponse("Validation Error", details);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoBookFoundExcpetion.class)
    public final ResponseEntity<ErrorResponse> noBookFound(NoBookFoundExcpetion ex, WebRequest request) {
        log.warn("noBookFound found {}",ex.getMessage());
        List<String> details = new ArrayList<>();
        details.add(ex.getMessage());
        ErrorResponse error = new ErrorResponse("Detail", details);
        return new ResponseEntity<>(error, HttpStatus.OK);
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
