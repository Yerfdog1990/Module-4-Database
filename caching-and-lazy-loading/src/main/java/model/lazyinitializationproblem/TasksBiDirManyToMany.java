package model.lazyinitializationproblem;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
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
    private String name;
    @ManyToMany(mappedBy = "tasks")
    private List<EmployeeBiDirManyToMany> employees = new ArrayList<>();

    // Constructor
    public TasksBiDirManyToMany(String name) {
        this.name = name;
    }
}
