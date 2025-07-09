package com.hibernate.domain;

import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(schema="movie", name = "city")
@Data
public class City {
    @Id
    @Column(name = "city_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private short id;

    private String city;

    @Column(name = "last_update")
    @UpdateTimestamp
    private LocalDateTime localDateTime;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;
}
