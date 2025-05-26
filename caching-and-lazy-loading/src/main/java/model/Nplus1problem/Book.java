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
    private String title;

    @ManyToMany(mappedBy = "books", fetch = FetchType.LAZY)
    private List<Author> authors = new ArrayList<>();

    // Constructor
    public Book(String title) {
        this.title = title;
    }
    // Add author
    public void addAuthor(Author author) {
        authors.add(author);
        author.getBooks().add(this);
    }
}
