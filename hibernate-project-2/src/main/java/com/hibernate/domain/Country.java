package com.hibernate.domain;

import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(schema="movie", name = "country")
@Data
public class Country {
    @Id
    @Column(name = "country_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private short id;

    @Column(name = "country")
    private String title;

    @Column(name = "last_update")
    @UpdateTimestamp
    private LocalDateTime localDateTime;
}
