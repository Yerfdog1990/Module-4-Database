package org.example.model;

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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;
    @ManyToMany
    @JoinTable(name = "employee_task",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "task_id"))
    private List<Tasks> tasks = new ArrayList<>();

    public Employee(String name, BigDecimal salary, Date hiringDate, Department department, List<Tasks> tasks) {
        this.name = name;
        this.salary = salary;
        this.hiringDate = hiringDate;
        this.department = department;
        this.tasks = tasks != null ? tasks : new ArrayList<>();
    }
}
