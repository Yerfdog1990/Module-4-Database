package org.example.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "task")
@Data
@NoArgsConstructor
public class Tasks {
    @Id
    @GeneratedValue(generator = "task_id_seq")
    private int id;
    @Column(name = "description", unique = true)
    private String description;
    @ManyToMany(mappedBy = "tasks", cascade = CascadeType.ALL)
    private List<Employee> employees;
}
