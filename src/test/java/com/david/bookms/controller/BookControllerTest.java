package com.david.bookms.controller;


import com.david.bookms.model.Book;
import com.david.bookms.repository.BookRepository;
import com.david.bookms.service.EventService;
import org.junit.jupiter.api.Test;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class BookControllerTest {


    @Autowired
    BookController bookController;
    @Autowired
    MockMvc mockMvc;
    @MockBean
    private BookRepository bookRepository;
    @MockBean
    private EventService eventService;





    @Test
    public void getBookById() throws Exception {
        Book book = new Book();
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(
                        "{\"id\":0,\"title\":null,\"authors\":null,\"publicationDate\":null,\"ISBN\":null}"
                )));
        verify(eventService,times(1)).bookSearch(any(Book.class));
    }
    @Test
    public void getBookById_NoFound() throws Exception{
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());
        mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(
                        "{\"message\":\"Detail\",\"details\":[\"Book not found by id=1\"]}"
                )));
        verify(eventService,never()).bookSearch(any(Book.class));
    }
    @Test
    public void findAll_Empty() throws Exception{
        when(bookRepository.findAll()).thenReturn(Arrays.asList());
        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(
                        "[]"
                )));
        verify(eventService,never()).bookSearch(any(Book.class));
    }
    @Test
    public void findAll() throws Exception{
        Book book = new Book();
        when(bookRepository.findAll()).thenReturn(Arrays.asList(book,book));
        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(
                        "[{\"id\":0,\"title\":null,\"authors\":null,\"publicationDate\":null,\"ISBN\":null},{\"id\":0,\"title\":null,\"authors\":null,\"publicationDate\":null,\"ISBN\":null}]"
                )));
        verify(eventService,times(2)).bookSearch(any(Book.class));
    }
    @Test
    public void saveBook_error_length_12() throws Exception{
        mockMvc.perform(post("/books").contentType(MediaType.APPLICATION_JSON).content("{\"isbn\":\"123456789012\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(
                        "{\"message\":\"Validation Error\",\"details\":[\"Field isbn-ISBN must be 13 digital\"]}"
                )));
        verify(eventService,never()).bookCreate(any(Book.class));
    }
    @Test
    public void saveBook_error_length_14() throws Exception{
        mockMvc.perform(post("/books").contentType(MediaType.APPLICATION_JSON).content("{\"isbn\":\"12345678901234\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(
                        "{\"message\":\"Validation Error\",\"details\":[\"Field isbn-ISBN must be 13 digital\"]}"
                )));
        verify(eventService,never()).bookCreate(any(Book.class));
    }
    @Test
    public void saveBook_error() throws Exception{
        mockMvc.perform(post("/books").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(
                        "{\"message\":\"Validation Error\",\"details\":[\"Field isbn-must not be blank\"]}"
                )));
        verify(eventService,never()).bookCreate(any(Book.class));
    }
    @Test
    public void saveBook_ok() throws Exception{
        when(bookRepository.save(any(Book.class))).thenReturn(new Book());
        mockMvc.perform(post("/books").contentType(MediaType.APPLICATION_JSON).content("{\"isbn\":\"1234567890123\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(
                        ""
                )));
        verify(eventService,times(1)).bookCreate(any(Book.class));
        verify(bookRepository,times(1)).save(any(Book.class));
    }

    @Test
    public void deleteBook_empty() throws Exception{
        mockMvc.perform(delete("/books/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(
                        "{\"message\":\"Detail\",\"details\":[\"Book not found by id=1\"]}"
                )));
        verify(eventService,never()).bookCreate(any(Book.class));
        verify(bookRepository,never()).delete(any(Book.class));
        verify(bookRepository,times(1)).findById(eq(1L));
    }
    @Test
    public void deleteBook_OK() throws Exception{
        when(bookRepository.findById(eq(1L))).thenReturn(Optional.of(new Book()));
        mockMvc.perform(delete("/books/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(
                        "{\"id\":0,\"title\":null,\"authors\":null,\"publicationDate\":null,\"ISBN\":null}"
                )));
        verify(eventService,times(1)).bookDelete(any(Book.class));
        verify(bookRepository,times(1)).delete(any(Book.class));
        verify(bookRepository,times(1)).findById(eq(1L));
    }

    @Test
    public void updateBook_empty() throws Exception{
        mockMvc.perform(put("/books/1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(
                        "{\"message\":\"Exception\",\"details\":[\"Required request body is missing: public com.david.bookms.model.Book com.david.bookms.controller.BookController.updateBook(java.lang.Long,com.david.bookms.controller.dto.BookDto)\"]}"
                )));
        verify(bookRepository,never()).save(any(Book.class));
        verify(eventService,never()).bookModify(any(Book.class),any(Book.class));
    }
    @Test
    public void updateBook_notFound() throws Exception{
        mockMvc.perform(put("/books/1").contentType(MediaType.APPLICATION_JSON).content("{\"isbn\":\"1234567890123\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(
                        "{\"message\":\"Detail\",\"details\":[\"Book not found by id=1\"]}"
                )));
        verify(bookRepository,never()).save(any(Book.class));
        verify(eventService,never()).bookModify(any(Book.class),any(Book.class));
    }

    @Test
    public void updateBook() throws Exception{
        when(bookRepository.findById(1L)).thenReturn(Optional.of(new Book()));
        mockMvc.perform(put("/books/1").contentType(MediaType.APPLICATION_JSON).content("{\"isbn\":\"1234567890123\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(
                        "{\"id\":0,\"title\":null,\"authors\":null,\"publicationDate\":null,\"ISBN\":\"1234567890123\"}"
                )));
        verify(bookRepository,times(1)).save(any(Book.class));
        verify(eventService,times(1)).bookModify(any(Book.class),any(Book.class));
    }

    @Test
    public void searchByAuther_NotFound() throws Exception{
        when(bookRepository.findByAuthorsIgnoreCaseIn(Arrays.asList("hello"))).thenReturn(null);
        mockMvc.perform(get("/books/searchByAuthor/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(
                        "{\"message\":\"Detail\",\"details\":[\"Book not found by author=hello\"]}"
                )));
        verify(eventService,never()).bookSearch(any(Book.class));
    }

    @Test
    public void searchByTitle_NotFound() throws Exception{
        when(bookRepository.findByTitleIgnoreCase("java")).thenReturn(null);
        mockMvc.perform(get("/books/searchByTitle/java"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(
                        "{\"message\":\"Detail\",\"details\":[\"Book not found by title=java\"]}"
                )));
        verify(eventService,never()).bookSearch(any(Book.class));
    }
    @Test
    public void searchByIsbn_NotFound() throws Exception{
        when(bookRepository.findByIsbn("1234")).thenReturn(null);
        mockMvc.perform(get("/books/searchByISBN/1234"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(
                        "{\"message\":\"Detail\",\"details\":[\"Book not found by ISBN=1234\"]}"
                )));
        verify(eventService,never()).bookSearch(any(Book.class));
    }

    @Test
    public void searchByAuther() throws Exception{
        when(bookRepository.findByAuthorsIgnoreCaseIn(Arrays.asList("hello"))).thenReturn(Arrays.asList(new Book()));
        mockMvc.perform(get("/books/searchByAuthor/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(
                        "[{\"id\":0,\"title\":null,\"authors\":null,\"publicationDate\":null,\"ISBN\":null}]"
                )));
        verify(eventService,times(1)).bookSearch(any(Book.class));
    }

    @Test
    public void searchByTitle() throws Exception{
        when(bookRepository.findByTitleIgnoreCase("java")).thenReturn(Arrays.asList(new Book()));
        mockMvc.perform(get("/books/searchByTitle/java"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(
                        "[{\"id\":0,\"title\":null,\"authors\":null,\"publicationDate\":null,\"ISBN\":null}]"
                )));
        verify(eventService,times(1)).bookSearch(any(Book.class));
    }
    @Test
    public void searchByIsbn() throws Exception{
        when(bookRepository.findByIsbn("1234")).thenReturn(Arrays.asList(new Book()));
        mockMvc.perform(get("/books/searchByISBN/1234"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(
                        "[{\"id\":0,\"title\":null,\"authors\":null,\"publicationDate\":null,\"ISBN\":null}]"
                )));
        verify(eventService,times(1)).bookSearch(any(Book.class));
    }

    @Test
    public void seach_empry() throws Exception {
        mockMvc.perform(get("/books/search"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(
                        "{\"message\":\"Detail\",\"details\":[\"Book not found null null null\"]}"
                )));
        verify(bookRepository,times(1)).search(any(),any(),any());
        verify(eventService,never()).bookSearch(any(Book.class));
    }
    @Test
    public void seach() throws Exception {
        when(bookRepository.search(any(),any(),any())).thenReturn(Arrays.asList(new Book()));
        mockMvc.perform(get("/books/search?title=123&author=david&ISBN=123456789"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(
                        "[{\"id\":0,\"title\":null,\"authors\":null,\"publicationDate\":null,\"ISBN\":null}]"
                )));
        verify(bookRepository,times(1)).search(any(),any(),any());
        verify(eventService,times(1)).bookSearch(any(Book.class));
    }


}