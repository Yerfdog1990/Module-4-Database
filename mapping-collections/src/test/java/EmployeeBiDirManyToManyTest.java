import mappings.model.manytomany.TasksBiDirManyToMany;
import mappings.model.manytomany.EmployeeBiDirManyToMany;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static mappings.repository.HibernateUtil.doWithSession;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EmployeeBiDirManyToManyTest {
    private List<EmployeeBiDirManyToMany> employee;
    private List<TasksBiDirManyToMany> tasks;
    
    @BeforeEach
    void setUp() {
        // Create tasks
        TasksBiDirManyToMany task1 = new TasksBiDirManyToMany("Task 1", employee);
        TasksBiDirManyToMany task2 = new TasksBiDirManyToMany("Task 2", employee);
        TasksBiDirManyToMany task3 = new TasksBiDirManyToMany("Task 3", employee);
        tasks = List.of(task1, task2, task3);
        
        // Create employees
        doWithSession(session -> {
            session.persist(task1);
            session.persist(task2);
            session.persist(task3);
            return null;
        });

        EmployeeBiDirManyToMany employee1 = new EmployeeBiDirManyToMany("John", tasks);
        EmployeeBiDirManyToMany employee2 = new EmployeeBiDirManyToMany("Jane", tasks);
        EmployeeBiDirManyToMany employee3 = new EmployeeBiDirManyToMany("Jill", tasks);
        employee = List.of(employee1, employee2, employee3);
        doWithSession(session -> {
            session.persist(employee1);
            session.persist(employee2);
            session.persist(employee3);
            return null;
        });
    }
    
    @AfterEach
    void tearDown() {
        doWithSession(session -> {
            session.createQuery("DELETE FROM EmployeeBiDirManyToMany").executeUpdate();
            session.createQuery("DELETE FROM TasksBiDirManyToMany").executeUpdate();
            return null;
        });
    }
    @Test
    void testBiDirectionalOneToManyMapping() {
        doWithSession(session -> {
            // Verify employees
            for (EmployeeBiDirManyToMany emp : employee) {
                EmployeeBiDirManyToMany retrievedEmployee = session.get(EmployeeBiDirManyToMany.class, emp.getId());
                assertNotNull(retrievedEmployee);
                assertEquals(emp.getName(), retrievedEmployee.getName());
                assertEquals(3, retrievedEmployee.getTasks().size());
                assertEquals("Task 1", retrievedEmployee.getTasks().get(0).getDescription());
                assertEquals("Task 2", retrievedEmployee.getTasks().get(1).getDescription());
                assertEquals("Task 3", retrievedEmployee.getTasks().get(2).getDescription());
            }

            // Verify tasks
            for (TasksBiDirManyToMany task : tasks) {
                TasksBiDirManyToMany retrievedTask = session.get(TasksBiDirManyToMany.class, task.getId());
                assertNotNull(retrievedTask);
                assertEquals(task.getDescription(), retrievedTask.getDescription());
                assertEquals(3, retrievedTask.getEmployees().size());
                assertEquals("John", retrievedTask.getEmployees().get(0).getName());
                assertEquals("Jane", retrievedTask.getEmployees().get(1).getName());
                assertEquals("Jill", retrievedTask.getEmployees().get(2).getName());
            }

            // Verify relationships
            EmployeeBiDirManyToMany firstEmployee = session.get(EmployeeBiDirManyToMany.class, employee.get(0).getId());
            List<TasksBiDirManyToMany> employeeTasks = firstEmployee.getTasks();
            assertEquals(3, employeeTasks.size());
            assertEquals("Task 1", employeeTasks.get(0).getDescription());
            assertEquals("Task 2", employeeTasks.get(1).getDescription());
            assertEquals("Task 3", employeeTasks.get(2).getDescription());
            assertEquals("John", employeeTasks.get(0).getEmployees().get(0).getName());
            assertEquals("Jane", employeeTasks.get(1).getEmployees().get(1).getName());
            assertEquals("Jill", employeeTasks.get(2).getEmployees().get(2).getName());

            return null;
        });
    }
}
