package mappings.model.manytomany;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "employee")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"department", "tasks"})
public class EmployeeBiDirManyToMany {
    @Id
    @GeneratedValue(generator = "employee_id_seq")
    private int id;

    @Column(name = "name")
    private String name;

    @ManyToMany
    @JoinTable(name = "employee_task",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "task_id"))
    private List<TasksBiDirManyToMany> tasks = new ArrayList<>();

    public EmployeeBiDirManyToMany(String name, List<TasksBiDirManyToMany> tasks) {
        this.name = name;
        this.tasks = tasks != null ? tasks : new ArrayList<>();
    }
}
