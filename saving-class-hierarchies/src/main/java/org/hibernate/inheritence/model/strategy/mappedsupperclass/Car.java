package org.hibernate.inheritence.model.strategy.mappedsupperclass;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@MappedSuperclass
@Data
@NoArgsConstructor
public abstract class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String model;

    public Car(String name) {
        this.model = name;
    }
}
