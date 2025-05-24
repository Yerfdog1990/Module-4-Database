import mappings.model.onetoone.ExclusiveEmployeeUni;
import mappings.model.onetoone.ExclusiveTaskUni;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static mappings.repository.HibernateUtil.doWithSession;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ExclusiveEmployeeTest {
    private ExclusiveEmployeeUni persistedEmployee;
    @BeforeEach
    public void setUp() {
        ExclusiveTaskUni task = new ExclusiveTaskUni("Task 1");
        ExclusiveEmployeeUni employee = new ExclusiveEmployeeUni("John");
        employee.setTask(task);

        persistedEmployee = doWithSession(session -> {
            session.persist(task);
            session.persist(employee);
            return employee;
        });
    }

    @AfterEach
    void tearDown() {
        doWithSession(session -> {
            session.createQuery("DELETE FROM ExclusiveEmployeeUni").executeUpdate();
            session.createQuery("DELETE FROM ExclusiveTaskUni").executeUpdate();
            return null;
        });
    }
    @Test
    void testUnidirectionalOneToOneMapping() {
        ExclusiveEmployeeUni retrievedEmployee = doWithSession(session -> session.get(ExclusiveEmployeeUni.class, persistedEmployee.getId()));
        assertNotNull(retrievedEmployee);
        assertNotNull(retrievedEmployee.getTask());
        assertEquals("Task 1", retrievedEmployee.getTask().getDescription());
        assertEquals("John", retrievedEmployee.getName());

        ExclusiveTaskUni retrievedTask = doWithSession(session -> session.get(ExclusiveTaskUni.class, retrievedEmployee.getTask().getId()));
        assertNotNull(retrievedTask);
        assertNotNull(retrievedTask.getDescription());
        assertEquals("Task 1", retrievedTask.getDescription());
    }
}
