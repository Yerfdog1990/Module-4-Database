package model.Nplus1problem;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "book")
@Data
@NoArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", unique = true)
    private String name;

    @OneToMany(mappedBy = "book")
    private List<Author> authors = new ArrayList<>();

    // Constructor
    public Book(String name) {
        this.name = name;
        this.authors = new ArrayList<>();
        if (authors != null)
            authors.forEach(this::addAuthor);
    }
    // Add author
    public void addAuthor(Author author) {
        authors.add(author);
        author.setBook(this);
    }
}
