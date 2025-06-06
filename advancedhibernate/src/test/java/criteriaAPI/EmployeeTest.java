package criteriaAPI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;
import org.hibernate.model.criteriaAPI.Department;
import org.hibernate.model.criteriaAPI.Employee;
import org.hibernate.repository.HibernateUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EmployeeTest {
  @BeforeEach
  void setUp() {
    HibernateUtil.doWithSession(
        session -> {
          // Create new Department objects and persist them to the database
          Department it = new Department("IT");
          Department hr = new Department("HR");
          Department marketing = new Department("Marketing");
          Department finance = new Department("Finance");
          Department sales = new Department("Sales");

          session.persist(it);
          session.persist(hr);
          session.persist(marketing);
          session.persist(finance);
          session.persist(sales);
          System.out.println("Departments persisted");

          // Create 20 new Employees and persist them to the database
          Employee john = new Employee("John", "Software engineer", 160_000.0, it);
          Employee jane = new Employee("Jane", "UX Designer", 150_000.0, it);
          Employee jack = new Employee("Jack", "Marketing manager", 130_000.0, marketing);
          Employee johnson = new Employee("Johnson", "Sales manager", 120_000.0, sales);
          Employee peter = new Employee("Peter", "Sales manager", 120_000.0, sales);
          Employee michael = new Employee("Michael", "Software Architect", 180_000.0, it);
          Employee sarah = new Employee("Sarah", "HR Manager", 140_000.0, hr);
          Employee robert = new Employee("Robert", "Financial Analyst", 125_000.0, finance);
          Employee emily = new Employee("Emily", "Marketing Specialist", 110_000.0, marketing);
          Employee david = new Employee("David", "Sales Representative", 115_000.0, sales);
          Employee lisa = new Employee("Lisa", "HR Specialist", 95_000.0, hr);
          Employee william = new Employee("William", "DevOps Engineer", 155_000.0, it);
          Employee olivia = new Employee("Olivia", "Content Manager", 120_000.0, marketing);
          Employee james = new Employee("James", "Financial Controller", 135_000.0, finance);
          Employee emma = new Employee("Emma", "Technical Lead", 170_000.0, it);
          Employee daniel = new Employee("Daniel", "HR Coordinator", 85_000.0, hr);
          Employee sophia = new Employee("Sophia", "Marketing Analyst", 105_000.0, marketing);
          Employee lucas = new Employee("Lucas", "Account Manager", 125_000.0, sales);
          Employee isabella = new Employee("Isabella", "Risk Analyst", 130_000.0, finance);
          Employee alexander = new Employee("Alexander", "System Administrator", 145_000.0, it);

          session.persist(john);
          session.persist(jane);
          session.persist(jack);
          session.persist(johnson);
          session.persist(peter);
          session.persist(michael);
          session.persist(sarah);
          session.persist(robert);
          session.persist(emily);
          session.persist(david);
          session.persist(lisa);
          session.persist(william);
          session.persist(olivia);
          session.persist(james);
          session.persist(emma);
          session.persist(daniel);
          session.persist(sophia);
          session.persist(lucas);
          session.persist(isabella);
          session.persist(alexander);

          return null;
        });
  }

  @AfterEach
  void tearDown() {
    HibernateUtil.doWithSession(
        session -> {
          session.createQuery("delete from Employee").executeUpdate();
          session.createQuery("delete from Department").executeUpdate();
          return null;
        });
  }

  // Query 1: Get all employees with a salary greater than 100,000
  @Test
  void salaryGreaterThan100000() {
    HibernateUtil.doWithSession(
        session -> {
          CriteriaBuilder builder = session.getCriteriaBuilder();
          CriteriaQuery<Employee> query = builder.createQuery(Employee.class);
          Root<Employee> root = query.from(Employee.class);
          query.select(root).where(builder.gt(root.get("salary"), 100000.0));
          List<Employee> result = session.createQuery(query).getResultList();
          assertThat(result).hasSize(18);
          assertThat(result.get(0).getName()).isEqualTo("John");
          assertThat(result.get(0).getOccupation()).isEqualTo("Software engineer");
          assertThat(result.get(0).getSalary()).isEqualTo(160_000.0);
          assertThat(result.get(0).getDepartment().getName()).isEqualTo("IT");
          return result;
        });
  }

  // Query 2: Get all employees with a salary less than 50,000
  @Test
  void salaryLessThan50000() {
    HibernateUtil.doWithSession(
        session -> {
          CriteriaBuilder builder = session.getCriteriaBuilder();
          CriteriaQuery<Employee> query = builder.createQuery(Employee.class);
          Root<Employee> root = query.from(Employee.class);
          query.select(root).where(builder.lt(root.get("salary"), 50000.0));
          List<Employee> result = session.createQuery(query).getResultList();
          assertEquals(0, result.size());
          return result;
        });
  }

  // Query 3: Get all employees whose occupation contains the word "test"
  @Test
  void occupationContainsTest() {
    HibernateUtil.doWithSession(
        session -> {
          CriteriaBuilder builder = session.getCriteriaBuilder();
          CriteriaQuery<Employee> query = builder.createQuery(Employee.class);
          Root<Employee> root = query.from(Employee.class);
          query.select(root).where(builder.like(root.get("occupation"), "%test%"));
          List<Employee> result = session.createQuery(query).getResultList();
          assertEquals(0, result.size());
          return result;
        });
  }

  // Query 4: Get all employees with salary between 10,000 and 50,000
  @Test
  void salaryBetween10000And50000() {
    HibernateUtil.doWithSession(
        session -> {
          CriteriaBuilder builder = session.getCriteriaBuilder();
          CriteriaQuery<Employee> query = builder.createQuery(Employee.class);
          Root<Employee> root = query.from(Employee.class);
          query.select(root).where(builder.between(root.get("salary"), 10000.0, 50000.0));
          List<Employee> result = session.createQuery(query).getResultList();
          assertEquals(0, result.size());
          return result;
        });
  }

  // Query 5: Get all employees with null name
  @Test
  void employeesWithNullName() {
    HibernateUtil.doWithSession(
        session -> {
          CriteriaBuilder builder = session.getCriteriaBuilder();
          CriteriaQuery<Employee> query = builder.createQuery(Employee.class);
          Root<Employee> root = query.from(Employee.class);
          query.select(root).where(builder.isNull(root.get("name")));
          List<Employee> result = session.createQuery(query).getResultList();
          assertEquals(0, result.size());
          return result;
        });
  }

  // Query 6: Get all employees with non-null name
  @Test
  void employeesWithNonNullName() {
    HibernateUtil.doWithSession(
        session -> {
          CriteriaBuilder builder = session.getCriteriaBuilder();
          CriteriaQuery<Employee> query = builder.createQuery(Employee.class);
          Root<Employee> root = query.from(Employee.class);
          query.select(root).where(builder.isNotNull(root.get("name")));
          List<Employee> result = session.createQuery(query).getResultList();
          assertEquals(20, result.size());
          return result;
        });
  }
}
