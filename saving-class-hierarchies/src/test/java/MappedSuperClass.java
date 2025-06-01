import org.hibernate.inheritence.model.strategy.mappedsupperclass.BMW;
import org.hibernate.inheritence.model.strategy.mappedsupperclass.Benz;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.hibernate.inheritence.repository.HibernateUtil.doWithSession;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class MappedSuperClass {
    @BeforeEach
    void setup() {
        // Create Benz and BMW objects
        Benz benz = new Benz("Benz", "Black");
        BMW bmw = new BMW("BMW", "Blue");

        // Persist both objects
        doWithSession(session -> {
            session.persist(benz);
            session.persist(bmw);
            return null;
        });
    }
    @AfterEach
    void tearDown() {
        doWithSession(session -> {
            session.createQuery("delete from Benz ").executeUpdate();
            session.createQuery("delete from BMW ").executeUpdate();
            return null;
        });
    }
    @Test
    void learnAboutNonPolymorphicQueries() {
    doWithSession(session -> {
            // Non-polymorphic queries
            List<Benz> benz = session.createQuery("select b from Benz b", Benz.class).list();
            benz.forEach(System.out::println);
            assertFalse(benz.isEmpty());
            assertThat(benz).hasSize(1);
            assertThat(benz.get(0).getColor()).isEqualTo("Black");
            return benz;
        });
    doWithSession(session -> {
        List<BMW> bmw = session.createQuery("select b from BMW b", BMW.class).list();
        bmw.forEach(System.out::println);
        assertFalse(bmw.isEmpty());
        assertThat(bmw).hasSize(1);
        assertThat(bmw.get(0).getColor()).isEqualTo("Blue");
        return bmw;
    });
    }
}
