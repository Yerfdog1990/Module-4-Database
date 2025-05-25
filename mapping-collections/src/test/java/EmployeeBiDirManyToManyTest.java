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
        TasksBiDirManyToMany task1 = new TasksBiDirManyToMany("Task 1");
        TasksBiDirManyToMany task2 = new TasksBiDirManyToMany("Task 2");

        // Create employees
        EmployeeBiDirManyToMany emp1 = new EmployeeBiDirManyToMany("Employee 1", null);
        EmployeeBiDirManyToMany emp2 = new EmployeeBiDirManyToMany("Employee 2", null);

        emp1.addTask(task1);
        emp1.addTask(task2);
        emp2.addTask(task2);

        doWithSession(session -> {
            session.persist(task1);
            session.persist(task2);
            session.persist(emp1);
            session.persist(emp2);
            return null;
        });
    }

    @AfterEach
    void tearDown() {
        doWithSession(session -> {
            session.createQuery("delete from EmployeeBiDirManyToMany").executeUpdate();
            session.createQuery("delete from TasksBiDirManyToMany").executeUpdate();
            return null;
        });
    }
    @Test
    void testBiDirectionalOneToManyMapping() {
        // Verify employees
        doWithSession(session -> {
            List<EmployeeBiDirManyToMany> employees = session.createQuery("from EmployeeBiDirManyToMany", EmployeeBiDirManyToMany.class).list();
            assertEquals(2, employees.size());

            // Verify tasks
            List<TasksBiDirManyToMany> tasks = session.createQuery("from TasksBiDirManyToMany", TasksBiDirManyToMany.class).list();
            assertEquals(2, tasks.size());

            // Verify relationships
            EmployeeBiDirManyToMany emp1 = employees.get(0);
            assertEquals(2, emp1.getTasks().size());
            assertEquals("Employee 1", emp1.getName());

            EmployeeBiDirManyToMany emp2 = employees.get(1);
            assertEquals(1, emp2.getTasks().size());
            assertEquals("Employee 2", emp2.getName());

            TasksBiDirManyToMany task2 = tasks.get(1);
            assertEquals(2, task2.getEmployees().size());
            assertNotNull(task2.getEmployees().get(0));
            assertNotNull(task2.getEmployees().get(1));
            assertEquals(emp1, task2.getEmployees().get(0));
            assertEquals(emp2, task2.getEmployees().get(1));
            assertEquals("Employee 1", task2.getEmployees().get(0).getName());
            assertEquals("Employee 2", task2.getEmployees().get(1).getName());
            return null;
        });
    }
}
