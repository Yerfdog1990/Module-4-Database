import org.hibernate.inheritence.model.strategy.joinedtable.House;
import org.hibernate.inheritence.model.strategy.joinedtable.Room1;
import org.hibernate.inheritence.model.strategy.joinedtable.Room2;
import org.hibernate.inheritence.repository.HibernateUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class JoinedTable {
    @BeforeEach
    void setup() {
        // Create room objects
        Room1 room1 = new Room1("Bed room", "Ocean blue");
        Room1 room2 = new Room1("Living room", "Jungle green");

        // Persist both objects
        HibernateUtil.doWithSession(session -> {
            session.persist(room1);
            session.persist(room2);
            return null;
        });
    }
    @AfterEach
    void tearDown() {
        HibernateUtil.doWithSession(session -> {
            session.createQuery("delete from House").executeUpdate();
            return null;
        });
    }
    @Test
    void learnAboutPolymorphicQueries() {
        HibernateUtil.doWithSession(session -> {
            List<House> houses = session.createQuery("select h from House h", House.class).list();
            houses.forEach(System.out::println);
            assertFalse(houses.isEmpty());
            assertThat(houses).hasSize(2);
            assertThat(houses).anyMatch(h -> h instanceof Room1);
            assertThat(houses).anyMatch(h -> h instanceof Room2);
            assertThat(houses).allMatch(h -> h instanceof House);
            return houses;
        });
    }
    @Test
    void learnAboutNonPolymorphicQueries() {
        HibernateUtil.doWithSession(session -> {
            List<Room1> room1 = session.createQuery("select r from Room1 r", Room1.class).list();
            room1.forEach(System.out::println);
            assertFalse(room1.isEmpty());
            assertThat(room1).hasSize(1);
            assertThat(room1).anyMatch(r -> r.getName().equals("Bed room"));
            assertThat(room1).anyMatch(r -> r.getRoomColor().equals("Ocean blue"));
            assertThat(room1).allMatch(r -> r instanceof Room1);
            return room1;
        });
        HibernateUtil.doWithSession(session -> {
            List<Room2> room2 = session.createQuery("select r from Room2 r", Room2.class).list();
            room2.forEach(System.out::println);
            assertFalse(room2.isEmpty());
            assertThat(room2).hasSize(1);
            assertThat(room2).anyMatch(r -> r.getName().equals("Living room"));
            assertThat(room2).anyMatch(r -> r.getRoomColor().equals("Jungle green"));
            assertThat(room2).allMatch(r -> r instanceof Room2);
            return room2;
        });
    }
}
