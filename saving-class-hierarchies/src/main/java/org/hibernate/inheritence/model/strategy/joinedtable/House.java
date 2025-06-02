package org.hibernate.inheritence.model.strategy.joinedtable;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "houses")
@Inheritance(strategy = jakarta.persistence.InheritanceType.JOINED)
@Data
@NoArgsConstructor
public abstract class House {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;

    public House(String name) {
        this.name = name;
    }
}
