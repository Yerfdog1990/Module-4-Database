package mappings.model.onetoone;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "exclusive_task")
@Data
@NoArgsConstructor
public class ExclusiveTaskUni {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Column(name = "description")
    String description;

    // Constructor
    public ExclusiveTaskUni(String description) {
        this.description = description;
    }
}
