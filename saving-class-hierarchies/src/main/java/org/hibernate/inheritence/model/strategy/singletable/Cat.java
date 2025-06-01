package org.hibernate.inheritence.model.strategy.singletable;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@DiscriminatorValue("C")
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class Cat extends Animal {
    private Boolean likesMilk;

    public Cat(String name, Boolean likesMilk) {
        super(name);
        this.likesMilk = likesMilk;
    }
}