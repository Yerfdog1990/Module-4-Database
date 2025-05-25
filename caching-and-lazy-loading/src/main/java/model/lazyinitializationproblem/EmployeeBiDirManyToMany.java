package model.lazyinitializationproblem;

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

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @JoinTable(name = "employee_task",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "task_id", referencedColumnName = "id"))
    private List<TasksBiDirManyToMany> tasks = new ArrayList<>();

    public void addTask(TasksBiDirManyToMany task) {
        tasks.add(task);
        task.getEmployees().add(this);
    }

    public EmployeeBiDirManyToMany(String name) {
        this.name = name;
        this.tasks = new ArrayList<>();
        if (tasks != null)
            tasks.forEach(this::addTask);
    }
}
