package mappings.model.manytomany;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "task")
@Data
@NoArgsConstructor
public class TasksBiDirManyToMany {
    @Id
    @GeneratedValue(generator = "task_id_seq")
    private int id;
    @Column(name = "description", unique = true)
    private String description;
    @ManyToMany(mappedBy = "tasks", cascade = CascadeType.ALL)
    private List<EmployeeBiDirManyToMany> employees;

    // Constructor
    public TasksBiDirManyToMany(String description, List<EmployeeBiDirManyToMany> employees) {
        this.description = description;
        this.employees = employees != null ? employees : List.of();
    }
}
