package criteriaAPI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.persistence.criteria.*;
import java.util.List;
import org.hibernate.Session;
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

  @Test
  void advancedWorkWithCriteriaAPI() {
    HibernateUtil.doWithSession(
        session -> {
          CriteriaBuilder builder = session.getCriteriaBuilder();
          CriteriaQuery<Employee> query = builder.createQuery(Employee.class);
          Root<Employee> root = query.from(Employee.class);

          Predicate salaryGreaterThan150k = builder.gt(root.get("salary"), 150000.0);
          Predicate managerPosition = builder.like(root.get("occupation"), "%manager%");
          query.select(root).where(builder.or(salaryGreaterThan150k, managerPosition));

          List<Employee> result = session.createQuery(query).getResultList();

          assertThat(result).hasSize(7);
          return result;
        });
  }

  @Test
  void sortingTest() {
    HibernateUtil.doWithSession(
        session -> {
          CriteriaBuilder builder = session.getCriteriaBuilder();
          CriteriaQuery<Employee> query = builder.createQuery(Employee.class);
          Root<Employee> root = query.from(Employee.class);

          query
              .select(root)
              .orderBy(builder.desc(root.get("salary")), builder.asc(root.get("name")));

          List<Employee> result = session.createQuery(query).getResultList();

          assertThat(result).hasSize(20);
          assertThat(result.get(0).getSalary()).isEqualTo(180_000.0);
          assertThat(result.get(0).getName()).isEqualTo("Michael");

          return result;
        });
  }

  @Test
  void groupingAndAggregation() {
    HibernateUtil.doWithSession(
        session -> {
          CriteriaBuilder builder = session.getCriteriaBuilder();

          // Count the total number of employees
          CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
          countQuery.select(builder.count(countQuery.from(Employee.class)));
          Long employeeCount = session.createQuery(countQuery).getSingleResult();
          assertThat(employeeCount).isEqualTo(20L);

          // Calculate average salary
          CriteriaQuery<Double> avgQuery = builder.createQuery(Double.class);
          Root<Employee> root = avgQuery.from(Employee.class);
          avgQuery.select(builder.avg(root.get("salary")));
          Double averageSalary = session.createQuery(avgQuery).getSingleResult();
          assertThat(averageSalary).isEqualTo(130750.0);

          return null;
        });
  }

  private CriteriaUpdate<Employee> updateSalariesForLowEarners(
      Session session, double percentageIncrease, double salaryThreshold) {
    CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
    CriteriaUpdate<Employee> salaryUpdate = criteriaBuilder.createCriteriaUpdate(Employee.class);
    Root<Employee> employeeRoot = salaryUpdate.from(Employee.class);
    double salaryMultiplier = 1 + (percentageIncrease / 100);

    salaryUpdate
        .set(
            employeeRoot.<Double>get("salary"),
            criteriaBuilder.prod(employeeRoot.get("salary"), salaryMultiplier))
        .where(criteriaBuilder.lt(employeeRoot.get("salary"), salaryThreshold));

    // Execute the update query
    session.createQuery(salaryUpdate).executeUpdate();

    return salaryUpdate;
  }

  @Test
  void updateDatabase() {
    HibernateUtil.doWithSession(
        session -> {
          updateSalariesForLowEarners(session, 10.0, 100000.0); // 10% increase for salaries < 100k
          return null;
        });
  }

  @Test
  void testDeleteHighSalaryEmployees() {
    HibernateUtil.doWithSession(
        session -> {
          // Count employees before deletion
          CriteriaBuilder builder = session.getCriteriaBuilder();
          CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
          countQuery.select(builder.count(countQuery.from(Employee.class)));
          Long beforeCount = session.createQuery(countQuery).getSingleResult();

          // Delete high salary employees
          deleteEmployeesWithSalaryGreaterThan100000(session);

          // Count employees after deletion
          Long afterCount = session.createQuery(countQuery).getSingleResult();

          // Verify deletion
          assertThat(beforeCount).isEqualTo(20L);
          assertThat(afterCount).isEqualTo(2L);
          return null;
        });
  }

  void deleteEmployeesWithSalaryGreaterThan100000(Session session) {
    CriteriaBuilder builder = session.getCriteriaBuilder();
    CriteriaDelete<Employee> delete = builder.createCriteriaDelete(Employee.class);
    Root<Employee> root = delete.from(Employee.class);
    delete.where(builder.gt(root.get("salary"), 100000.0));
    session.createQuery(delete).executeUpdate();
  }

  @Test
  void paginationTest() {
    HibernateUtil.doWithSession(
        session -> {
          CriteriaBuilder builder = session.getCriteriaBuilder();
          CriteriaQuery<Employee> query = builder.createQuery(Employee.class);
          Root<Employee> root = query.from(Employee.class);

          query.select(root).orderBy(builder.asc(root.get("name")));

          // Get first page (5 employees)
          List<Employee> firstPage =
              session.createQuery(query).setFirstResult(0).setMaxResults(5).getResultList();

          // Get the second page (next 5 employees)
          List<Employee> secondPage =
              session.createQuery(query).setFirstResult(5).setMaxResults(5).getResultList();

          assertThat(firstPage).hasSize(5);
          assertThat(secondPage).hasSize(5);
          assertThat(firstPage.get(0).getName()).isEqualTo("Alexander");
          assertThat(secondPage.get(0).getName()).isEqualTo("Isabella");

          return null;
        });
  }
}
