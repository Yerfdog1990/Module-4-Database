package mappings.model.manytomany;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "employee")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"department", "tasks"})
public class Employee {
    @Id
    @GeneratedValue(generator = "employee_id_seq")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "salary")
    private BigDecimal salary;

    @Column(name = "hiring_data")
    @Temporal(TemporalType.DATE)
    private Date hiringDate;

    @ManyToMany
    @JoinTable(name = "employee_task",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "task_id"))
    private List<Tasks> tasks = new ArrayList<>();

    public Employee(String name, BigDecimal salary, Date hiringDate, List<Tasks> tasks) {
        this.name = name;
        this.salary = salary;
        this.hiringDate = hiringDate;
        this.tasks = tasks != null ? tasks : new ArrayList<>();
    }
}
