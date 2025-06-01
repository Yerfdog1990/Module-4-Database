package org.hibernate.inheritence.model.strategy.mappedsupperclass;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class BMW extends Car{
    private String color;

    public BMW(String name, String color) {
        super(name);
        this.color = color;
    }
}
