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
public class John extends Person{
    private String citizenship;

    public John(String name, String citizenship) {
        super(name);
        this.citizenship = citizenship;
    }
}
