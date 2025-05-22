package model;

import static hibernate.repository.HibernateUtil.doWithSession;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import hibernate.model.embeddedannotation.Address;
import hibernate.model.embeddedannotation.Citizenship;
import org.junit.jupiter.api.Test;

public class CitizenshipTest {
    @Test
    void verifyEmbeddedAnnotations() {
        Citizenship citizenship = doWithSession(session -> {
            Address address = new Address("123 Main St", "Anytown", "NY", "10001");
            Citizenship kenya = new Citizenship("John Doe", address);
            return session.merge(kenya);
        });
        doWithSession(session -> {
            Citizenship foundCitizenship = session.find(Citizenship.class, citizenship.getId());
            System.out.println(foundCitizenship);
            assertNotNull(foundCitizenship);
            assertEquals("John Doe", foundCitizenship.getName());
            assertEquals("123 Main St", foundCitizenship.getAddress().getStreet());
            assertEquals("Anytown", foundCitizenship.getAddress().getCity());
            assertEquals("NY", foundCitizenship.getAddress().getState());
            assertEquals("10001", foundCitizenship.getAddress().getZip());
            return null;
        });
    }
}
