package model;

import static hibernate.repository.HibernateUtil.doWithSession;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import hibernate.model.typeannotation.Employee;
import org.hibernate.query.NativeQuery;
import org.junit.jupiter.api.Test;

public class EmployeeTest {
  @Test
  void testStringToNumberMapping() {
    Employee joeDoe =
        doWithSession(
            session -> {
              Employee employee = new Employee("Joe Doe", "123456789");
              return session.merge(employee);
            });

    doWithSession(
        session -> {
          // Make sure data is retrieved as expected
          Employee foundEmployee = session.find(Employee.class, joeDoe.getId());
          assertNotNull(foundEmployee);
          assertEquals("123456789", foundEmployee.getNumericCode()); // Changed to expect int

          // Check in the DB to have a numeric value
          NativeQuery<Integer> nativeQuery =
              session.createNativeQuery(
                  "select numeric_code from employee where id = " + joeDoe.getId(), Integer.class);
          Integer storedNumericCode = nativeQuery.getResultList().getFirst();
          assertNotNull(storedNumericCode);
          assertEquals(123456789, storedNumericCode);
          return null;
        });
  }
}
