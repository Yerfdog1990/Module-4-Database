import org.hibernate.inheritence.model.strategy.tableperclass.John;
import org.hibernate.inheritence.model.strategy.tableperclass.Person;
import org.hibernate.inheritence.model.strategy.tableperclass.Peter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.hibernate.inheritence.repository.HibernateUtil.doWithSession;

public class TablePerClass {
    @BeforeEach
    void setup() {
        // Create John and Peter objects
        John john = new John("John", "USA");
        Peter peter = new Peter("Peter", "Software Engineer");

        // Persist both objects
        doWithSession(session -> {
            session.persist(john);
            session.persist(peter);
            return null;
        });
    }
    @AfterEach
    void tearDown() {
        doWithSession(session -> {
            session.createQuery("delete from Person").executeUpdate();
            return null;
        });
    }
    @Test
    void learnAboutPolymorphicQueries() {
        doWithSession(session -> {
            List<Person> allPeople = session.createQuery("select p from Person p", Person.class).list();
            allPeople.forEach(System.out::println);
            assertThat(allPeople).hasSize(2);
            assertThat(allPeople).anyMatch(p -> p instanceof John);
            return null;
        });
    }
    @Test
    void learnAboutNonPolymorphicQueries() {
        doWithSession(session -> {
            List<John> john = session.createQuery("select j from John j", John.class).list();
            assertThat(john).hasSize(1);
            assertThat(john.get(0).getCitizenship()).isEqualTo("USA");
            john.forEach(System.out::println);
            return john;
        });
        doWithSession(session -> {
            List<Peter> peter = session.createQuery("select p from Peter p", Peter.class).list();
            assertThat(peter).hasSize(1);
            assertThat(peter.get(0).getCareer()).isEqualTo("Software Engineer");
            peter.forEach(System.out::println);
            return peter;
        });
    }
}
