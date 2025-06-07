package nativequery;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import org.hibernate.model.criteriaAPI.City;
import org.hibernate.model.criteriaAPI.Person;
import org.hibernate.query.Query;
import org.hibernate.repository.HibernateUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PersonTest {
  @BeforeEach
  void setUp() {
    HibernateUtil.doWithSession(
        session -> {
          // Create a city and persist it in the database
          City paris = new City("Paris");
          City newYork = new City("New York");
          session.persist(paris);
          session.persist(newYork);

          // Create persons and persist them in the database
          Person person1 = new Person("John", 30, paris);
          Person person2 = new Person("Jane", 25, newYork);
          session.persist(person1);
          session.persist(person2);
          return null;
        });
  }

  @AfterEach
  void tearDown() {
    HibernateUtil.doWithSession(
        session -> {
          session.createQuery("delete from Person").executeUpdate();
          session.createQuery("delete from City").executeUpdate();
          return null;
        });
  }

  @Test
  void learnAboutNativeQuery() {
    String sqlQuery = "select * from Person where age > 25 and name like 'J%'";
    List<Person> people =
        HibernateUtil.doWithSession(
            session -> {
              Query<Person> query = session.createNativeQuery(sqlQuery, Person.class);
              return query.getResultList();
            });
    assertEquals(1, people.size());
  }

  @Test
  void hibernateEntityMappingTest() {
    List<Person> people =
        HibernateUtil.doWithSession(
            session -> {
              Query<Person> query =
                  session.createNativeQuery("SELECT * FROM person").addEntity(Person.class);
              return query.getResultList();
            });
    assertEquals(2, people.size());
  }

  @Test
  void JPAEntityMappingTest() {
    List<Person> people =
        HibernateUtil.doWithSession(
            session -> {
              Query<Person> query = session.createNativeQuery("SELECT * FROM person", Person.class);
              return query.getResultList();
            });
    assertEquals(2, people.size());
  }

  @Test
  void mappingMultipleClasses() {
    String hqlQuery = "select p from Person p join p.city c where c.name = 'Paris'";
    List<Person> people =
        HibernateUtil.doWithSession(
            session -> {
              Query<Person> query = session.createQuery(hqlQuery, Person.class);
              return query.getResultList();
            });
    assertEquals(1, people.size());
    assertEquals("John", people.get(0).getName());
    assertEquals(30, people.get(0).getAge());
    assertEquals("Paris", people.get(0).getCity().getName());
  }

  @Test
  void mappingMultipleClassesWithJoin() {
    String hqlQuery = "select p from Person p join fetch p.city c where c.name = 'Paris'";
    List<Person> people =
        HibernateUtil.doWithSession(
            session -> {
              Query<Person> query = session.createQuery(hqlQuery, Person.class);
              return query.getResultList();
            });
  }

  @Test
  void DTOMappingTest() {
    String hqlQuery = "select p.name, p.age from Person p";
    List<Object[]> people =
        HibernateUtil.doWithSession(
            session -> {
              Query<Object[]> query = session.createQuery(hqlQuery);
              return query.getResultList();
            });
    assertEquals(2, people.size());
    assertEquals("John", people.get(0)[0]);
    assertEquals(30, people.get(0)[1]);
    people.forEach(array -> System.out.println(Arrays.toString(array)));
  }
}
