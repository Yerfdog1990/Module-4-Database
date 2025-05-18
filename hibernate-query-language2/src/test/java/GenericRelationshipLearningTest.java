import org.example.model.Department;
import org.example.model.Employee;
import org.example.model.Task;
import org.example.repository.HibernateUtil;
import org.hibernate.Session;
import org.assertj.core.api.Assertions;
import org.hibernate.query.Query;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class GenericRelationshipLearningTest {
    private Session session;
    @BeforeEach
    public void setUp(){

        HibernateUtil.doWithSession( session -> {
            Date date = new Date(LocalDate.now().minusDays(5).toEpochDay());
            // Create a department
            Department department = new Department("IT");
            session.persist(department);

            // Create an employee assigned to a department
            Employee employee1 = new Employee("John", new BigDecimal(100000), date, department);
            Employee employee2 = new Employee("Jane", new BigDecimal(100000), date, department);

            // Add tasks to both employees
            Task task1 = new Task("Task1");
            Task task2 = new Task("Task2");

            employee1.getTasks().add(task1);
            employee1.getTasks().add(task2);
            employee2.getTasks().add(task1);
            employee2.getTasks().add(task2);

            // Persist the tasks
            session.persist(task1);
            session.persist(task2);

            // Persist the employees
            session.persist(employee1);
            session.persist(employee2);

            return null;
        });
        this.session = HibernateUtil.getSession();
    }

    @AfterEach
    void tearDown(){
        HibernateUtil.doWithSession( session -> {
            session.createQuery("delete from Employee").executeUpdate();
            session.createQuery("delete from Department").executeUpdate();
            return null;
        });
    }



    @Test
    void sanityTets(){
        try {
            List<Employee> employees = session.createQuery("from Employee", Employee.class).list();
            assertThat(employees).hasSize(2);
            assertThat(employees).allMatch(employee -> "IT".equals(employee.getDepartment().getName()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            session.close();
        }
    }

    @Test
    void verifyFixture() {
        // Verify department
        Department department = session.createQuery("from Department where name = :name", Department.class)
                .setParameter("name", "IT")
                .uniqueResult();
        assertThat(department).isNotNull();
        assertThat(department.getName()).isEqualTo("IT");

        // Verify employees
        List<Employee> employees = session.createQuery("from Employee", Employee.class).list();
        assertThat(employees).hasSize(2);
        assertThat(employees).allMatch(e -> e.getDepartment().equals(department));
        assertThat(employees).extracting("name").contains("John", "Jane");

        // Verify tasks
        List<Task> tasks = session.createQuery("from Task", Task.class).list();
        assertThat(tasks).hasSize(2);
        assertThat(tasks).extracting("description").contains("Task1", "Task2");
        assertThat(employees).allMatch(e -> e.getTasks().size() == 2);
    }
    @Test
    void simpleJoinFiltering(){
        Department department = session.createQuery("from Department d where d.name = :name", Department.class)
                .setParameter("name", "IT")
                .uniqueResult();
        Query<Employee> query = session.createQuery("from Employee e where e.department = :department", Employee.class);
        query.setParameter("department", department);
        List<Employee> employees = query.list();
        assertThat(employees).hasSize(2);
        assertThat(employees).allMatch((Employee e) -> e.getDepartment().equals(department));
    }
}
