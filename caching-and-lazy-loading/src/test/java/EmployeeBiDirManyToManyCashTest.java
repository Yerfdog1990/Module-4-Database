import model.lazyinitializationproblem.EmployeeBiDirManyToMany;
import model.lazyinitializationproblem.TasksBiDirManyToMany;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static repository.HibernateUtil.doWithSession;

public class EmployeeBiDirManyToManyCashTest {
    @Test
    void lazyInitializationProblem() {
        // Create tasks
        TasksBiDirManyToMany task1 = new TasksBiDirManyToMany("Task 1");
        TasksBiDirManyToMany task2 = new TasksBiDirManyToMany("Task 2");
        TasksBiDirManyToMany task3 = new TasksBiDirManyToMany("Task 3");
        TasksBiDirManyToMany task4 = new TasksBiDirManyToMany("Task 4");
        TasksBiDirManyToMany task5 = new TasksBiDirManyToMany("Task 5");

        // Create employees
        EmployeeBiDirManyToMany employee = new EmployeeBiDirManyToMany("Employee 1");
        employee.addTask(task1);
        employee.addTask(task2);
        employee.addTask(task3);
        employee.addTask(task4);
        employee.addTask(task5);

        // Save employees
        EmployeeBiDirManyToMany detachedEmployee = doWithSession(session -> {
            session.persist(employee);
            return employee;
        });

        // Retrieve employees
        EmployeeBiDirManyToMany retrievedEmployee = doWithSession(session -> session.find(EmployeeBiDirManyToMany.class, detachedEmployee.getId()));

        // Assert results
        assertThat(retrievedEmployee.getTasks()).hasSize(5);
    }
}
