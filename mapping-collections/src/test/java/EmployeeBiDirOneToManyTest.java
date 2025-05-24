import mappings.model.onetomany.DepartmentBiDirOneToMany;
import mappings.model.onetomany.EmployeeBiDirOneToMany;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static mappings.repository.HibernateUtil.doWithSession;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EmployeeBiDirOneToManyTest {
    private DepartmentBiDirOneToMany department;
    private EmployeeBiDirOneToMany employee;
    @BeforeEach
    void setUp() {
        department = new DepartmentBiDirOneToMany("IT");
        employee = new EmployeeBiDirOneToMany("John", department);
        doWithSession(session -> {
            session.persist(department);
            session.persist(employee);
            return null;
        });
    }
    @AfterEach
    void tearDown() {
        doWithSession(session -> {
            session.createQuery("DELETE FROM EmployeeBiDirOneToMany").executeUpdate();
            session.createQuery("DELETE FROM DepartmentBiDirOneToMany").executeUpdate();
            return null;
        });
    }
    @Test
    void testBiDirectionalOneToManyMapping() {
        doWithSession(session ->{
            // Verify department
            DepartmentBiDirOneToMany retrievedDepartment =  session.get(DepartmentBiDirOneToMany.class, department.getId());
            assertNotNull(retrievedDepartment);
            assertNotNull(retrievedDepartment.getEmployees());
            assertEquals("IT", retrievedDepartment.getName());
            assertEquals(1, retrievedDepartment.getId());

            // Verify employee
            EmployeeBiDirOneToMany retrievedEmployee = session.get(EmployeeBiDirOneToMany.class, employee.getId());
            assertNotNull(retrievedEmployee);
            assertNotNull(retrievedEmployee.getDepartment());
            assertEquals("John", retrievedEmployee.getName());
            assertEquals(1, retrievedEmployee.getId());
            assertEquals("IT", retrievedEmployee.getDepartment().getName());
            assertEquals(1, retrievedEmployee.getDepartment().getId());
            return null;
        });
    }
}
