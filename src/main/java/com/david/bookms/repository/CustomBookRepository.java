package com.david.bookms.repository;

import com.david.bookms.model.Book;

import java.util.List;

public interface CustomBookRepository {
    List<Book> search(String author, String title, String isbn);
}
