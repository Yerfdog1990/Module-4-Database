import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.hibernate.annotations.QueryHints;
import org.hibernate.cache.jcache.ConfigSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.model.Citizenship;
import org.hibernate.model.DenormalizedEmployee;
import org.hibernate.model.Department;
import org.hibernate.model.Employee;
import org.hibernate.model.Profession;
import org.hibernate.repository.HibernateUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class DatabaseDesignTest {

  // Test creating database indexes for employee and department tables
  @Test
  void demonstratingDatabaseIndexes() {
    HibernateUtil.doWithSession(
        session -> {
          int result =
              session
                  .createNativeQuery(
                      """
                CREATE INDEX idx_employee_email ON employee(email);
                CREATE INDEX idx_employee_name ON employee(name);
                CREATE INDEX idx_department_name ON department(name);
                """)
                  .executeUpdate();
          assertEquals(0, result);
          assertEquals(true, session.isOpen());
          assertThat(result).isZero();
          assertThat(session.isConnected()).isTrue();
          return null;
        });
  }

  // Test query optimization using read-only mode and fetch size configuration
  @Test
  void optimizingQueries() {
    HibernateUtil.doWithSession(
        session -> {
          List<Employee> employees =
              session
                  .createQuery(
                      "SELECT e FROM Employee e "
                          + "WHERE e.email LIKE :email "
                          + "ORDER BY e.name",
                      Employee.class)
                  .setParameter("email", "%@example.com")
                  .setHint(QueryHints.READ_ONLY, true)
                  .setHint(QueryHints.FETCH_SIZE, "50")
                  .getResultList();
          assertEquals(0, employees.size());
          assertEquals(true, employees instanceof List);
          assertThat(employees).isInstanceOf(List.class);
          return null;
        });
  }

  // Test second-level cache configuration with EhCache provider
  @Test
  void configureDatabaseCaching() {
    Configuration configuration = new Configuration();
    configuration
        .setProperty("hibernate.cache.use_second_level_cache", "true")
        .setProperty(
            "hibernate.cache.region.factory_class",
            "org.hibernate.cache.jcache.JCacheRegionFactory")
        .setProperty(ConfigSettings.CACHE_MANAGER, "org.ehcache.jsr107.EhcacheCachingProvider")
        .setProperty("hibernate.javax.cache.missing_cache_strategy", "create");
    assertEquals("true", configuration.getProperty("hibernate.cache.use_second_level_cache"));
    assertEquals(
        "create", configuration.getProperty("hibernate.javax.cache.missing_cache_strategy"));
  }

  // Test client-side query caching configuration
  @Test
  void configureClientSideCaching() {
    HibernateUtil.doWithSession(
        session -> {
          List<Employee> employees =
              session
                  .createQuery("SELECT e FROM Employee e", Employee.class)
                  .setCacheable(true)
                  .setCacheRegion("employee.list")
                  .setHint(QueryHints.CACHE_MODE, "NORMAL")
                  .getResultList();

          assertEquals(0, employees.size());
          assertEquals(true, employees instanceof List);
          assertThat(employees).isInstanceOf(List.class);
          assertThat(employees.size()).isZero();
          return null;
        });
  }

  // Test creation of a normalized database schema with separate tables
  @Test
  void createNormalizedDatabase() {
    HibernateUtil.doWithSession(
        session -> {
          Department itDepartment = new Department("IT");
          Department hrDepartment = new Department("HR");
          Department financeDepartment = new Department("Finance");
          Department marketingDepartment = new Department("Marketing");
          Department salesDepartment = new Department("Sales");
          session.persist(itDepartment);
          session.persist(hrDepartment);
          session.persist(financeDepartment);
          session.persist(marketingDepartment);
          session.persist(salesDepartment);

          Profession developer = new Profession("Software Developer");
          Profession manager = new Profession("Manager");
          Profession analyst = new Profession("Business Analyst");
          Profession designer = new Profession("Designer");
          Profession accountant = new Profession("Accountant");
          session.persist(developer);
          session.persist(manager);
          session.persist(analyst);
          session.persist(designer);
          session.persist(accountant);

          Citizenship usCitizenship = new Citizenship("USA");
          Citizenship ukCitizenship = new Citizenship("UK");
          Citizenship canadaCitizenship = new Citizenship("Canada");
          Citizenship germanyCitizenship = new Citizenship("Germany");
          Citizenship franceCitizenship = new Citizenship("France");
          session.persist(usCitizenship);
          session.persist(ukCitizenship);
          session.persist(canadaCitizenship);
          session.persist(germanyCitizenship);
          session.persist(franceCitizenship);

          Employee john = new Employee("John Doe", "john@example.com");
          Employee jane = new Employee("Jane Smith", "jane@example.com");
          Employee bob = new Employee("Bob Johnson", "bob@example.com");
          Employee alice = new Employee("Alice Brown", "alice@example.com");
          Employee charlie = new Employee("Charlie Wilson", "charlie@example.com");

          session.persist(john);
          session.persist(jane);
          session.persist(bob);
          session.persist(alice);
          session.persist(charlie);

          assertEquals("IT", itDepartment.getName());
          assertEquals("Software Developer", developer.getName());
          assertThat(john.getName()).isEqualTo("John Doe");
          assertThat(usCitizenship.getCountry()).isEqualTo("USA");
          return null;
        });
  }

  // Clean up test data after each test execution
  @AfterEach
  void tearDown() {
    HibernateUtil.doWithSession(
        session -> {
          session.createQuery("delete from Employee").executeUpdate();
          session.createQuery("delete from Department").executeUpdate();
          session.createQuery("delete from Profession").executeUpdate();
          session.createQuery("delete from Citizenship").executeUpdate();
          session.createQuery("delete from DenormalizedEmployee").executeUpdate();
          session.flush();
          return null;
        });
  }

  // Test creation of denormalized database schema in single table
  @Test
  void createDenomalizedDatabase() {
    HibernateUtil.doWithSession(
        session -> {
          DenormalizedEmployee john =
              new DenormalizedEmployee(
                  "John Doe", "john@example.com", "IT", "Software Developer", "USA");
          DenormalizedEmployee jane =
              new DenormalizedEmployee("Jane Smith", "jane@example.com", "HR", "Manager", "UK");
          DenormalizedEmployee bob =
              new DenormalizedEmployee(
                  "Bob Johnson", "bob@example.com", "Finance", "Accountant", "Canada");
          DenormalizedEmployee alice =
              new DenormalizedEmployee(
                  "Alice Brown", "alice@example.com", "Marketing", "Designer", "Germany");
          DenormalizedEmployee charlie =
              new DenormalizedEmployee(
                  "Charlie Wilson", "charlie@example.com", "Sales", "Business Analyst", "France");

          session.persist(john);
          session.persist(jane);
          session.persist(bob);
          session.persist(alice);
          session.persist(charlie);

          assertEquals("John Doe", john.getName());
          assertEquals("john@example.com", john.getEmail());
          assertEquals("IT", john.getDepartment());
          assertEquals("Software Developer", john.getProfession());
          assertEquals("USA", john.getCitizenship());

          assertThat(jane.getName()).isEqualTo("Jane Smith");
          assertThat(jane.getDepartment()).isEqualTo("HR");
          assertThat(jane.getProfession()).isEqualTo("Manager");
          assertThat(jane.getCitizenship()).isEqualTo("UK");

          return null;
        });
  }
}
