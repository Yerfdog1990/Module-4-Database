package dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.model.criteriaAPI.Department;
import org.hibernate.model.criteriaAPI.Employee;
import org.hibernate.query.Query;
import org.hibernate.repository.HibernateUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EmployeeDAO<T> {
  private final Session session;
  private final Class<T> entityClass;

  public EmployeeDAO(Session session, Class<T> entityClass) {
    this.session = session;
    this.entityClass = entityClass;
  }

  public List<Employee> getEmployeeList(int from, int count) {
    String hqlQuery = "from Employee";
    Query<Employee> query = session.createQuery(hqlQuery, Employee.class);
    query.setFirstResult(from);
    query.setMaxResults(count);
    return query.getResultList();
  }

  public int getEmployeeCount() {
    String hqlQuery = "select count(*) from Employee";
    Query<Long> query = session.createQuery(hqlQuery, Long.class);
    return query.getSingleResult().intValue();
  }

  public Employee getEmployeeByUniqName(String name) {
    String hqlQuery = "from Employee where name = :name";
    Query<Employee> query = session.createQuery(hqlQuery, Employee.class);
    query.setParameter("name", name);
    return query.getSingleResult();
  }

  public T save(final T entity) {
    session.persist(entity);
    return entity;
  }

  public T update(final T entity) {
    return session.merge(entity);
  }

  public void delete(final T entity) {
    session.remove(entity);
  }

  public void deleteById(final long entityId) {
    T entity = session.get(entityClass, entityId);
    if (entity != null) {
      session.remove(entity);
    }
  }
}

public class EmployeeTestWithDao {
  private Department it;
  private Department hr;
  private Department marketing;
  private Department finance;
  private Department sales;

  @BeforeEach
  void setUp() {
    HibernateUtil.doWithSession(
        session -> {
          // Create new Department objects and persist them to the database
          it = new Department("IT");
          hr = new Department("HR");
          marketing = new Department("Marketing");
          finance = new Department("Finance");
          sales = new Department("Sales");

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

  @Test
  void learnDao() {
    HibernateUtil.doWithSession(
        session -> {
          EmployeeDAO<Employee> employeeDAO = new EmployeeDAO<>(session, Employee.class);

          // Test getEmployeeList
          List<Employee> employees = employeeDAO.getEmployeeList(0, 5);
          System.out.println("First 5 employees: " + employees.size());
          employees.forEach(System.out::println);
          assertEquals(5, employees.size());
          assertEquals("John", employees.get(0).getName());
          assertEquals("Software engineer", employees.get(0).getOccupation());
          assertEquals(160_000.0, employees.get(0).getSalary());

          // Test getEmployeeCount
          int count = employeeDAO.getEmployeeCount();
          System.out.println("Total employees: " + count);
          assertEquals(20, count);

          // Test getEmployeeByUniqName
          Employee john = employeeDAO.getEmployeeByUniqName("John");
          System.out.println("Found employee: " + john.getName());
          assertEquals("John", john.getName());

          // Test save
          Employee newEmployee = new Employee("TestEmployee", "Tester", 100_000.0, it);
          Employee savedEmployee = employeeDAO.save(newEmployee);
          assertEquals("TestEmployee", savedEmployee.getName());

          // Test update
          savedEmployee.setSalary(110_000.0);
          Employee updatedEmployee = employeeDAO.update(savedEmployee);
          assertEquals(110_000.0, updatedEmployee.getSalary());

          // Test delete
          employeeDAO.delete(updatedEmployee);
          assertEquals(20, employeeDAO.getEmployeeCount());

          // Test deleteById
          Employee employeeToDelete = employeeDAO.getEmployeeByUniqName("Jane");
          employeeDAO.deleteById(employeeToDelete.getId());
          assertEquals(19, employeeDAO.getEmployeeCount());

          return null;
        });
  }
}
