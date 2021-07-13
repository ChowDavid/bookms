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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        mockMvc.perform(post("/books").contentType(MediaType.APPLICATION_JSON).content("{\"isbn\":\"1234567890123\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(
                        ""
                )));
        //verify(eventService,times(1)).bookCreate(any(Book.class));
    }


}