package org.hibernate.inheritence.model.strategy.tableperclass;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class Peter extends Person{
    private String career;

    public Peter(String name, String career) {
        super(name);
        this.career = career;
    }
}
