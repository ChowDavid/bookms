package com.david.bookms.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String title;
    @ElementCollection
    @CollectionTable(name = "author")
    private List<String> authors;
    @JsonProperty(value = "ISBN")
    @Column(name = "ISBN", length = 13)
    @Pattern(message="ISBN must be 13 digital" , regexp="^\\d{13}$")
    private String isbn;
    @JsonFormat(pattern = "dd/MM/yyyy")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate publicationDate;


}
