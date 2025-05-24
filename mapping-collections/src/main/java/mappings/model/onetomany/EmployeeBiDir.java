package mappings.model.onetomany;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "employee")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"department", "tasks"})
public class EmployeeBiDir {
    @Id
    @GeneratedValue(generator = "employee_id_seq")
    private int id;

    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private DepartmentBiDir department;

    public EmployeeBiDir(String name, DepartmentBiDir department) {
        this.name = name;
        this.department = department;
    }
}
