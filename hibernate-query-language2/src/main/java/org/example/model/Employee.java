package org.example.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "employee")
@Data
@NoArgsConstructor
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "salary")
    private BigDecimal salary;
    @Temporal(TemporalType.DATE)
    private Date hiringDate;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "employees")
    @JoinTable(name = "employee_task", joinColumns = @JoinColumn(name = "employee_id"), inverseJoinColumns = @JoinColumn(name = "task_id"))
    private List<Task> tasks = new ArrayList<>();

    // No arg constructor is needed for JPA
    public Employee(String name, BigDecimal salary, Date hiringDate, Department department) {
        this.name = name;
        this.salary = salary;
        this.hiringDate = hiringDate;
        this.department = department;
    }
}