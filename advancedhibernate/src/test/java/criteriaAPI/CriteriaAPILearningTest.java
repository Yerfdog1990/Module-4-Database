package criteriaAPI;

import static org.hibernate.repository.HibernateUtil.doWithSession;
import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;
import org.hibernate.model.criteriaAPI.City;
import org.hibernate.model.criteriaAPI.Person;
import org.hibernate.query.Query;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CriteriaAPILearningTest {
  @BeforeEach
  void setUp() {
    doWithSession(
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
    doWithSession(
        session -> {
          session.createQuery("delete from Person").executeUpdate();
          session.createQuery("delete from City").executeUpdate();
          return null;
        });
  }

  @Test
  void simpleQuery() {
    List<Person> queryResult =
        doWithSession(
            session -> {
              CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
              CriteriaQuery<Person> criteriaQuery = criteriaBuilder.createQuery(Person.class);

              Root<Person> personRoot = criteriaQuery.from(Person.class);
              CriteriaQuery<Person> orderByName =
                  criteriaQuery.orderBy(criteriaBuilder.asc(personRoot.get("name")));
              CriteriaQuery<Person> olderThan26AndStartWithJ =
                  orderByName.where(
                      criteriaBuilder.and(
                          criteriaBuilder.greaterThan(personRoot.get("age"), 30),
                          criteriaBuilder.like(personRoot.get("name"), "J%")));
              Query<Person> finalQuery = session.createQuery(olderThan26AndStartWithJ);

              return finalQuery.getResultList();
            });
    assertEquals(0, queryResult.size());
  }
}
