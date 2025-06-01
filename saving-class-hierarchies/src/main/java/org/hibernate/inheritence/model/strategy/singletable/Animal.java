package org.hibernate.inheritence.model.strategy.singletable;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.Setter;
import org.hibernate.annotations.DiscriminatorFormula;

@Entity
@Table(name = "animals")
@Getter
@Setter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING, length = 10)
//@DiscriminatorFormula(
//                "CASE WHEN 'CAT' IS NOT NULL THEN 'C' ELSE" +
//                "(CASE WHEN 'DOG' IS NOT NULL THEN 'D' ELSE 'A' END)" +
//                "END"
//)
public abstract class Animal {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String name;

    public Animal(String name) {
        this.name = name;
    }
}