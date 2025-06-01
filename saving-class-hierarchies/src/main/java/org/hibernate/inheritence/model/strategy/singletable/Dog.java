package org.hibernate.inheritence.model.strategy.singletable;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("D")
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class Dog extends Animal {
    String breed;

    public Dog(String name, String breed) {
        super(name);
        this.breed = breed;
    }
}