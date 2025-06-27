package hibernate;

import hibernate.model.Customer;
import hibernate.model.PhoneNumber;
import org.junit.jupiter.api.Test;

import static hibernate.repository.HibernateUtils.doWithSession;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class CustomerTest {

    @Test
    void testPersistedPhoneNumber(){
        Customer savedCustomer = doWithSession(session -> {
            PhoneNumber phoneNumber = new PhoneNumber("+254", "723456789");
            Customer customer = new Customer("John", phoneNumber);
            session.persist(customer);
            return customer;
        });
        // Check that we can read the number back
        Customer loadedCustomer = doWithSession(session -> session.get(Customer.class, savedCustomer.getId()));
        System.out.println(loadedCustomer.getPhoneNumber());
        assertThat(loadedCustomer.getPhoneNumber()).isEqualTo(savedCustomer.getPhoneNumber());

    }
}
