package com.david.bookms.repository;

import com.david.bookms.model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CustomBookRepositoryImpl implements  CustomBookRepository {

    @Autowired
    EntityManager em;

    @Override
    public List<Book> search(String author, String title, String isbn) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Book> cq = cb.createQuery(Book.class);
        Root<Book> book = cq.from(Book.class);
        List<Predicate> predicates = new ArrayList<>();
        if (title != null) {
            predicates.add(cb.equal(book.get("title"), title));
        }
        if (author != null) {
            predicates.add(cb.isMember(author,book.get("authors")));
        }
        if (isbn !=null){
            predicates.add(cb.equal(book.get("isbn"), isbn));
        }
        cq.where(predicates.toArray(new Predicate[0]));
        return em.createQuery(cq).getResultList();
    }
}
