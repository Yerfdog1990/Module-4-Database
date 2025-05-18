package org.example.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "department")
public class Department {
    @Id
    @GeneratedValue(generator = "department_id_seq")
    private int id;
    private String name;

    // No arg constructor is needed for JPA
    public Department(String name) {
        this.name = name;
    }
}
