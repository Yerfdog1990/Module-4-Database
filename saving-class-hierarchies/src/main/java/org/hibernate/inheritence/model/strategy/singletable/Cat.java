package org.hibernate.inheritence.model.strategy.singletable;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@DiscriminatorValue("cat")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Cat extends Animal {
    private Boolean likesMilk;

    public Cat(String name, Boolean likesMilk) {
        super(name);
        this.likesMilk = likesMilk;
    }
}
