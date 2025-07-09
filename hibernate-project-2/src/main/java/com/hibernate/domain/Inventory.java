package com.hibernate.domain;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(schema="movie", name = "inventory")
@Data
public class Inventory {

    @Id
    @Column(name = "inventory_id", columnDefinition = "mediumint")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "film_id")
    private Film film;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;
}
