import static org.junit.jupiter.api.Assertions.*;

import entities.Employee;
import java.util.function.Function;
import org.hibernate.Session;
import org.junit.jupiter.api.Test;
import util.HibernateUtil;

public class L1CacheTest {
    @Test
    void proveLevel1Cache() {
        Employee employee = new Employee(1, "John Doe");

        HibernateUtil hibernateUtil = new HibernateUtil();
        hibernateUtil.runInTransaction(
                session -> {
                    session.persist(employee);
                });

        hibernateUtil.runInTransaction(
                session -> {
                    Integer employeeId = employee.getId();
                    Employee retrievedEmployee = session.find(Employee.class, employeeId);
                    Employee retrievedEmployee2 = session.find(Employee.class, employeeId);
                    assertSame(retrievedEmployee2, retrievedEmployee);
                });
    }

    @Test
    void proveLevel1CacheDoesNotOperateBetweenSessions() {
        Employee employee = new Employee(2, "John Doe");
        HibernateUtil hibernateUtil = new HibernateUtil();
        hibernateUtil.runInTransaction(
                session -> {
                    session.persist(employee);
                });

        Integer employeeId = employee.getId();

        Function<Session, Employee> fx = session -> session.get(Employee.class, employeeId);

        Employee employee1 = hibernateUtil.runInTransaction(fx);
        Employee employee2 = hibernateUtil.runInTransaction(fx);

        assertNotSame(employee1, employee2);
        assertEquals(employee1, employee2);
    }
}