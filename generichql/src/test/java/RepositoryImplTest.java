import static java.lang.System.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.example.model.Person;
import org.example.model.UserDTO;
import org.example.repository.HibernateUtil;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RepositoryImplTest {
  private Session session;
  private Transaction transaction;

  @BeforeEach
  protected void setUp() {
    session = HibernateUtil.getSessionFactory().openSession();
    transaction = session.beginTransaction();
    addUser(session);
    // Add test user for specific tests
    Person john =
        new Person("John", "john@email.com", "123456", "Test Address", "Test Citizenship");
    session.persist(john);
    transaction.commit();
  }

  @AfterEach
  protected void tearDown() {
    if (session != null && session.isOpen()) {
      transaction = session.beginTransaction();
      // Delete all records
      session.createQuery("delete from Person").executeUpdate();
      // Reset the sequence
      session
          .createNativeQuery("ALTER TABLE Person ALTER COLUMN id RESTART WITH 1")
          .executeUpdate();
      transaction.commit();
      session.close();
    }
  }

  private void addUser(Session session) {
    for (int i = 1; i < 50; i++) {
      Person person =
          new Person(
              "name" + i, "email" + i + "@test.com", "phone" + i, "address" + i, "citizenship" + i);
      session.persist(person);
    }
  }

  @Test
  void testGetAllUsers() {
    List<Person> queryResult = session.createQuery("from Person", Person.class).getResultList();
    assertThat(queryResult).hasSize(50); // Changed to assertThat style
    queryResult.forEach(out::println);
  }

  @Test
  void testGetUserByUsername() {
    List<Person> queryResult =
        session
            .createQuery("from Person where name = :name", Person.class)
            .setParameter("name", "John")
            .getResultList();

    assertThat(queryResult).hasSize(1);
    assertThat(queryResult.get(0).getName()).isEqualTo("John");
  }

  @Test
  void testGetUserByEmail() {
    try (Session testSession = HibernateUtil.getSession()) {
      String email =
          testSession
              .createQuery("select p.email from Person p where p.name = :name", String.class)
              .setParameter("name", "John")
              .getSingleResult();

      assertThat(email).isEqualTo("john@email.com");
    }
  }

  @Test
  void testGetUserByCitizenship() {
    Session session = HibernateUtil.getSession();
    Query<String> query = session.createQuery("select p.citizenship from Person p");
    Stream<String> stream = query.getResultStream();
    int resultSize = stream.collect(Collectors.toSet()).size();
    assertThat(resultSize).isEqualTo(50);
  }

  @Test
  void testGetUserByAddress() {
    Session session = HibernateUtil.getSession();
    Query<String> query = session.createQuery("select p.address from Person p");
    try (ScrollableResults<String> scrollableResults = query.scroll()) {
      while (scrollableResults.next()) {
        String address = scrollableResults.get();
        assertThat(address).isNotNull();
      }
    }
  }

  @Test
  void testGetUserByPhone() {
    Session session = HibernateUtil.getSession();
    List<Person> queryResult =
        session
            .createQuery("from Person where phone = :phone", Person.class)
            .setParameter("phone", "phone1")
            .getResultList();
    assertThat(queryResult).hasSize(1);
    assertThat(queryResult.get(0).getPhone()).isEqualTo("phone1");
  }

  @Test
  void testColumnDataTypes() {
    Session session = HibernateUtil.getSession();
    List<Object[]> queryResult =
        session.createQuery("select name, address from Person", Object[].class).list();
    assertThat(queryResult)
        .allMatch(
            array -> {
              boolean expectedLengthMatches = array.length == 2;
              boolean expectedTypeMatch = array[0] instanceof String && array[1] instanceof String;
              return expectedLengthMatches && expectedTypeMatch;
            });
  }

  @Test
  void mapTwoAttributesWithDTO() {
    /*
    This code demonstrates a common pattern in enterprise applications where you want to transfer only specific data (name and email in this case) rather than entire entities.
    The UserDTO (Data Transfer Object) pattern helps to:
      - Reduce network traffic by transferring only necessary data
      - Protect sensitive information by excluding certain fields
      - Improve performance by fetching only required attributes
    The test ensures this mapping works correctly and maintains the non-null constraints from the original entity.
         */
    Session session = HibernateUtil.getSession();
    Query<UserDTO> query =
        session.createQuery("select new UserDTO(p.name, p.email) from Person p", UserDTO.class);
    List<UserDTO> queryResult = query.list();
    assertThat(queryResult)
        .allMatch(
            dto -> {
              boolean expectedLengthMatches = (dto.name() != null) && (dto.email() != null);
              return expectedLengthMatches;
            });
  }
}
