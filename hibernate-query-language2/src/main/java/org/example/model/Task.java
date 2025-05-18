package org.example.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "task")
@Data
@NoArgsConstructor
public class Task {
    @Id
    @GeneratedValue(generator = "task_id_seq", strategy = GenerationType.SEQUENCE)
    private int id;
    private String description;

    @ManyToMany(mappedBy = "tasks")
    private List<Employee> employees;

    // No arg constructor is needed for JPA
    public Task(String description) {
        this.description = description;
    }
}
