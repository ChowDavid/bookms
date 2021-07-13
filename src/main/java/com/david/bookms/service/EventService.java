package com.david.bookms.service;

import com.david.bookms.model.Book;

public interface EventService {
    void bookCreate(Book book);
    void bookModify(Book book);
    void bookDelete(Book book);
    void bookSearch(Book book);

}
