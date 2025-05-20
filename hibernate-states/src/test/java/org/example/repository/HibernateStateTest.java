package org.example.repository;

import jakarta.persistence.LockModeType;
import org.example.model.Department;
import org.example.model.Employee;
import org.example.model.Tasks;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class HibernateStateTest {
    private int employeeId;
    private int departmentId;

    @Before
    public void setUp() {
        HibernateUtil.doWithSession(session -> {
            Department department = new Department("IT");
            Employee employee = new Employee("John", new BigDecimal("100000"), Date.valueOf(LocalDate.now()), department, null);
            session.save(department); // Save the department first due to the relationship
            session.save(employee);
            departmentId = department.getId();
            employeeId = employee.getId();
            return employee.getId(); // Return the actual employee ID
        });
    }

    @Test
    public void learnAboutDetached() {
        HibernateUtil.doWithSession(session -> {
            Employee employee = session.get(Employee.class, employeeId); // Hibernate proxied entity
            assertNotNull(employee);

            Department department = employee.getDepartment();
            assertNotNull(department.getName());
            return null;
        });
    }

    @Test
    public void learnOneToManyRelationship() {
        HibernateUtil.doWithSession(session -> {
            Department department = session.get(Department.class, departmentId);
            assertNotNull(String.valueOf(department), "Department should not be null");

            List<Employee> departmentEmployees = department.getEmployees();
            assertNotNull(departmentEmployees.toString(), "Employees should not be null");
            assertEquals(1, departmentEmployees.size());

            Employee employee = departmentEmployees.get(0);
            assertEquals("John", employee.getName());
            return null;
        });
    }

    @Test
    public void learnEvict() {
        HibernateUtil.doWithSession(session -> {
            Employee employee = session.get(Employee.class, employeeId);
            session.evict(employee); // Evict the entity from the session cache
            //assertThrows(LazyInitializationException.class, () -> employee.getDepartment().getName());
            return null;
        });
    }

    @Test
    public void learnPersistentStateSync() {
        HibernateUtil.doWithSession(session -> {
            Employee employee1 = session.get(Employee.class, employeeId);
            employee1.setName("Jane");
            //session.update(employee1); // - strictly unnecessary here since Hibernate automatically tracks changes to managed entities
            assertEquals("Jane", employee1.getName());
            return null;
        });
        HibernateUtil.doWithSession(session -> {
            Employee employee2 = session.get(Employee.class, employeeId);
            employee2.setName("Johnny");
            //session.update(employee2); // - strictly unnecessary here since Hibernate automatically tracks changes to managed entities
            assertEquals("Johnny", employee2.getName());
            return null;
        });
    }

    @Test
    public void learningUpdates() {
        HibernateUtil.doWithSession(session -> {
            // Create and save initial entities
            Tasks task = new Tasks();
            task.setDescription("Initial Task");
            session.save(task);

            Department newDepartment = new Department("HR");
            session.save(newDepartment);

            Employee employee = session.get(Employee.class, employeeId);
            employee.setDepartment(newDepartment);
            employee.setSalary(new BigDecimal("120000"));
            employee.getTasks().add(task);

            // Merge changes
            session.merge(employee);

            // Verify changes
            Employee verifiedEmployee = session.get(Employee.class, employeeId);
            assertEquals("HR", verifiedEmployee.getDepartment().getName());
            assertEquals(new BigDecimal("120000"), verifiedEmployee.getSalary());
            assertEquals(1, verifiedEmployee.getTasks().size());
            assertEquals("Initial Task", verifiedEmployee.getTasks().get(0).getDescription());

            return null;
        });
    }

    @Test
    public void learnTransientState() {
        Employee employee = new Employee("Bob", new BigDecimal("75000"), Date.valueOf(LocalDate.now()), null, null);
        //The employee is in a transient state - not associated with a Hibernate session
        assertNotNull(employee);
        assertEquals("Bob", employee.getName());
    }

    @Test
    public void learnPersistentState() {
        HibernateUtil.doWithSession(session -> {
            Employee employee = new Employee("Alice", new BigDecimal("85000"), Date.valueOf(LocalDate.now()), null, null);
            session.persist(employee); // now employee is in a persistent state
            employee.setName("Alice Updated"); // will be automatically synchronized

            Employee loadedEmployee = session.get(Employee.class, employee.getId());
            assertEquals("Alice Updated", loadedEmployee.getName());
            return null;
        });
    }

    @Test
    public void learnDetachedState() {
        // Test merging transient entity
        Employee transientEmployee = new Employee("TransientBob", new BigDecimal("60000"), Date.valueOf(LocalDate.now()), null, null);
        HibernateUtil.doWithSession(session -> {
            Employee persistentEmployee = (Employee) session.merge(transientEmployee);
            assertNotNull(persistentEmployee.getId()); // Should have an ID after merge
            return null;
        });

        // Test merging detached entity with modifications
        Employee detachedEmployee = HibernateUtil.doWithSession(session -> {
            Employee employee = session.get(Employee.class, employeeId);
            session.evict(employee); // explicitly detaching the entity
            employee.setName("ModifiedJohn"); // Modify the detached entity
            return employee;
        });

        HibernateUtil.doWithSession(session -> {
            Employee mergedEmployee = (Employee) session.merge(detachedEmployee);
            assertEquals("ModifiedJohn", mergedEmployee.getName());
            assertNotNull(mergedEmployee.getId());
            return null;
        });

        // Test merging persistent entity
        HibernateUtil.doWithSession(session -> {
            Employee persistentEmployee = session.get(Employee.class, employeeId);
            Employee mergedEmployee = (Employee) session.merge(persistentEmployee);
            assertEquals(persistentEmployee, mergedEmployee); // Should return the same instance
            return null;
        });
    }

    @Test
    public void learnRemovedState() {
        HibernateUtil.doWithSession(session -> {
            Employee employee = session.get(Employee.class, employeeId);
            session.delete(employee); // entity is in the removed state

            Employee deletedEmployee = session.get(Employee.class, employeeId);
            assertEquals(null, deletedEmployee);
            return null;
        });
    }

    @Test
    public void learnUpdateBehavior() {
        HibernateUtil.doWithSession(session -> {
            // Create a new employee - this will throw an exception if we try to update it
            Employee newEmployee = new Employee("UpdateTest", new BigDecimal("50000"),
                    Date.valueOf(LocalDate.now()), null, null);

            // Get existing employee
            Employee existingEmployee = session.get(Employee.class, employeeId);
            existingEmployee.setName("UpdatedName");

            // Using update() method
            session.update(existingEmployee);

            // Verify the update
            Employee verifyEmployee = session.get(Employee.class, employeeId);
            assertEquals("UpdatedName", verifyEmployee.getName());
            return null;
        });
    }

    @Test
    public void learnSaveOrUpdateBehavior() {
        HibernateUtil.doWithSession(session -> {
            // Create a new employee - saveOrUpdate() will perform INSERT
            Employee newEmployee = new Employee("SaveOrUpdateTest", new BigDecimal("55000"),
                    Date.valueOf(LocalDate.now()), null, null);
            session.saveOrUpdate(newEmployee);
            assertNotNull(newEmployee.getId());

            // Modify existing employee - saveOrUpdate() will perform UPDATE
            Employee existingEmployee = session.get(Employee.class, employeeId);
            existingEmployee.setName("SaveOrUpdateName");
            session.saveOrUpdate(existingEmployee);

            // Verify both operations
            Employee verifyNew = session.get(Employee.class, newEmployee.getId());
            assertEquals("SaveOrUpdateTest", verifyNew.getName());

            Employee verifyExisting = session.get(Employee.class, employeeId);
            assertEquals("SaveOrUpdateName", verifyExisting.getName());
            return null;
        });
    }

    @Test
    public void learnRetrieveObjects() {
        HibernateUtil.doWithSession(session -> {
            // Test get() method
            Employee employeeGet = session.get(Employee.class, employeeId);
            assertNotNull(employeeGet);
            assertEquals("John", employeeGet.getName());

            // Test load() method - returns proxy
            Employee employeeLoad = session.load(Employee.class, employeeId);
            // At this point, no database query has been executed
            assertNotNull(employeeLoad);
            // Accessing properties will trigger the database query
            assertEquals("John", employeeLoad.getName());

            // Test find() method
            Employee employeeFind = session.find(Employee.class, employeeId);
            assertNotNull(employeeFind);
            assertEquals("John", employeeFind.getName());

            // Test find() with non-existent ID
            Employee nonExistentEmployee = session.find(Employee.class, -1);
            assertEquals(null, nonExistentEmployee);

            return null;
        });
    }

    @Test
    public void learnRefresh() {
        // First transaction: Create and persist the employee
        Employee savedEmployee = HibernateUtil.doWithSession(session -> {
            // Create and persist a new employee
            Employee employee = new Employee("RefreshTest", new BigDecimal("70000"),
                    Date.valueOf(LocalDate.now()), null, null);
            session.save(employee);
            assertNotNull(employee.getId());
            return employee;
        });
        // Second transaction: Update via SQL
        HibernateUtil.doWithSession(session -> {
            // Modify the employee's name directly in the database
            session.createNativeQuery("UPDATE employee SET name = 'UpdatedViaSQL' WHERE id = :id")
                    .setParameter("id", employeeId)
                    .executeUpdate();

            // Object still has old values
            assertEquals("RefreshTest", savedEmployee.getName());
            return null;
        });
        // Third transaction: Verify the refresh
        HibernateUtil.doWithSession(session -> {
            Employee employee = session.get(Employee.class, employeeId);
            assertEquals("UpdatedViaSQL", employee.getName());
            // Refresh the object from the database
            session.refresh(employee);

            // Now the object should have the updated values
            assertEquals("UpdatedViaSQL", employee.getName());
            return null;
        });
    }

    @Test
    public void learnLock() {
        // First transaction: Create and persist the employee
        Employee savedEmployee = HibernateUtil.doWithSession(session -> {
            // Create and persist a new employee
            Employee employee = new Employee("LockTest", new BigDecimal("70000"),
                    Date.valueOf(LocalDate.now()), null, null);
            session.save(employee);
            assertNotNull(employee.getId());
            assertEquals("LockTest", employee.getName());
            return employee;
        });
        // Second transaction: Update via SQL
        HibernateUtil.doWithSession(session -> {
            // Modify the employee's name directly in the database
            session.createNativeQuery("UPDATE employee SET name = 'UpdatedViaSQL' WHERE id = :id")
                    .setParameter("id", employeeId)
                    .executeUpdate();
            return null;
        });
        // Third transaction: Verify the refresh
        HibernateUtil.doWithSession(session -> {
            Employee employee = session.get(Employee.class, employeeId);
            assertEquals("UpdatedViaSQL", employee.getName());
            // Lock the object from the database
            session.lock(employee, LockModeType.PESSIMISTIC_WRITE);
            // Now the object should have the updated values
            assertEquals("UpdatedViaSQL", employee.getName());
            // Verify the object is still locked
            assertEquals(LockModeType.PESSIMISTIC_WRITE, session.getLockMode(employee));
            return null;
        });
    }

    @Test
    public void learnDelete() {
        // Test safe department deletion
        HibernateUtil.doWithSession(session -> {
            Department department = session.get(Department.class, 1);
            department.getEmployees().clear(); // Remove all employee associations
            session.remove(department);
            session.flush();
            assertNull(session.find(Department.class, 1));
            return null;
        });

        // Test remove() deletion
        HibernateUtil.doWithSession(session -> {
            Employee employee = new Employee("ToBeRemoved", new BigDecimal("50000"),
                    Date.valueOf(LocalDate.now()), null, null);
            session.save(employee);
            session.remove(employee);
            session.flush();
            assertNull(session.find(Employee.class, employee.getId()));
            return null;
        });

        // Test cascade deletion
        HibernateUtil.doWithSession(session -> {
            Department department = new Department("DeptToDelete");
            Employee employee = new Employee("EmpInDeletedDept", new BigDecimal("60000"),
                    Date.valueOf(LocalDate.now()), department, null);
            department.getEmployees().add(employee);
            session.save(department);
            session.remove(department);
            session.flush();
            assertNull(session.find(Department.class, department.getId()));
            assertNull(session.find(Employee.class, employee.getId()));
            return null;
        });

        // Test orphan removal
        HibernateUtil.doWithSession(session -> {
            Department department = new Department("DeptWithOrphan");
            Employee employee = new Employee("OrphanEmp", new BigDecimal("70000"),
                    Date.valueOf(LocalDate.now()), department, null);
            department.getEmployees().add(employee);
            session.save(department);
            department.getEmployees().remove(employee);
            session.flush();
            assertNull(session.find(Employee.class, employee.getId()));
            return null;
        });

        // Test JPQL deletion
        HibernateUtil.doWithSession(session -> {
            Employee employee = new Employee("JPQLDelete", new BigDecimal("80000"),
                    Date.valueOf(LocalDate.now()), null, null);
            session.persist(employee);
            session.flush(); // Add this line to ensure the entity is saved before deletion
            session.clear(); // Add this to ensure the session is clean

            // Execute delete query in the same transaction
            int deletedCount = session.createQuery("delete from Employee where name = :name")
                    .setParameter("name", "JPQLDelete")
                    .executeUpdate();

            assertEquals(1, deletedCount);
            return null;
        });

        // Test native query deletion
        HibernateUtil.doWithSession(session -> {
            Employee employee = new Employee("NativeDelete", new BigDecimal("90000"),
                    Date.valueOf(LocalDate.now()), null, null);
            session.persist(employee);
            session.flush(); // Ensures employee is saved to the database
            // Now you can execute queries that depend on the employee being in the database
            int deletedCount = session.createNativeQuery("DELETE FROM employee WHERE name = :name")
                    .setParameter("name", "NativeDelete")
                    .executeUpdate();
            assertEquals(1, deletedCount);
            return null;
        });

        // Test soft deletion (simulated with a status flag)
        HibernateUtil.doWithSession(session -> {
            Employee employee = new Employee("SoftDelete", new BigDecimal("100000"),
                    Date.valueOf(LocalDate.now()), null, null);
            session.save(employee);
            // Simulate soft delete by updating a status (assuming we had such a field)
            session.createQuery("update Employee set name = concat(name, '_DELETED') where id = :id")
                    .setParameter("id", employee.getId())
                    .executeUpdate();
            session.clear(); // Clear the session to force a refresh from the database
            Employee softDeleted = session.get(Employee.class, employee.getId());
            assertEquals("SoftDelete_DELETED", softDeleted.getName());
            return null;
        });
    }
}


