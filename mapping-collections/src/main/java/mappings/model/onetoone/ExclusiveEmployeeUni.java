package mappings.model.onetoone;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "exclusive_employee")
@Data
@NoArgsConstructor
public class ExclusiveEmployeeUni {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToOne
    @JoinColumn(name = "task_id")
    private ExclusiveTaskUni task;
    // Constructor
    public ExclusiveEmployeeUni(String name) {
        this.name = name;
    }
}
