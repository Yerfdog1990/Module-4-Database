import jakarta.transaction.Transactional;
import mappings.model.onetomany.DepartmentBiDir;
import mappings.model.onetomany.EmployeeBiDir;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static mappings.repository.HibernateUtil.doWithSession;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EmployeeBiDirTest {
    private DepartmentBiDir department;
    private EmployeeBiDir employee;
    @BeforeEach
    void setUp() {
       department = new DepartmentBiDir("IT");
       employee = new EmployeeBiDir("John", department);
        doWithSession(session -> {
            session.persist(department);
            session.persist(employee);
            return null;
        });
    }
    @AfterEach
    void tearDown() {
        doWithSession(session -> {
            session.createQuery("DELETE FROM EmployeeBiDir").executeUpdate();
            session.createQuery("DELETE FROM DepartmentBiDir").executeUpdate();
            return null;
        });
    }
    @Test
    void testBiDirectionalOneToManyMapping() {
        doWithSession(session ->{
        DepartmentBiDir retrievedDepartment =  session.get(DepartmentBiDir.class, department.getId());
        assertNotNull(retrievedDepartment);
        assertNotNull(retrievedDepartment.getEmployees());
        assertEquals("IT", retrievedDepartment.getName());
        assertEquals(1, retrievedDepartment.getId());


        EmployeeBiDir retrievedEmployee = session.get(EmployeeBiDir.class, employee.getId());
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
