package com.hibernate.domain;

import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(schema="movie", name = "film_text")
@Data
public class FilmText {
    @Id
    @Column(name = "film_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private short id;

    // TODO: @Id
    @OneToOne
    @JoinColumn(name = "film_id", referencedColumnName = "film_id")
    @MapsId
    private Film film;

    private String title;

    @Column(columnDefinition = "text")
    @Type(type = "text")
    private String description;
}
