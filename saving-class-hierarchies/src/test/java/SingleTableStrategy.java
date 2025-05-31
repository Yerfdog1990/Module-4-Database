import org.hibernate.inheritence.model.strategy.singletable.Animal;
import org.hibernate.inheritence.model.strategy.singletable.Cat;
import org.hibernate.inheritence.model.strategy.singletable.Dog;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.hibernate.inheritence.repository.HibernateUtil.doWithSession;

public class SingleTableStrategy {
    @BeforeEach
    void setUP() {
        Cat garfield = new Cat("Garfield", true);
        Dog spot = new Dog("Spot", "Bulldog");

        // Persist both objects
        doWithSession(session -> {
            session.persist(garfield);
            session.persist(spot);
            return null;
        });
    }
    @AfterEach
    void tearDown() {
        doWithSession(session -> {
            session.createQuery("delete from Animal").executeUpdate();
            return null;
        });
    }
    @Test
    void learnAboutPolymorphicQueries() {
        doWithSession(session -> {
            // Polymorphic queries
            List<Animal> allAnimals = session.createQuery("select a from Animal a", Animal.class).list();
            assertThat(allAnimals).hasSize(2);
            assertThat(allAnimals).anyMatch(a -> a instanceof Cat);
            assertThat(allAnimals).anyMatch(a -> a instanceof Dog);
            allAnimals.forEach(System.out::println);
            return allAnimals;
        });
    }
    @Test
    void learnAboutNonPolymorphicQueries() {
        doWithSession(session -> {
            List<Cat> cats = session.createQuery("select c from Cat c", Cat.class).list();
            assertThat(cats).hasSize(1);
            cats.forEach(System.out::println);
            return cats;
        });
        doWithSession(session -> {
            List<Dog> dogs = session.createQuery("select d from Dog d", Dog.class).list();
            assertThat(dogs).hasSize(1);
            dogs.forEach(System.out::println);
            return dogs;
        });
    }
}
