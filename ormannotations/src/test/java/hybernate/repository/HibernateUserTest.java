package hybernate.repository;

import static org.junit.jupiter.api.Assertions.*;

import hibernate.model.UserHibernate;
import hibernate.repository.IUserRepository;
import hibernate.repository.UserRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HibernateUserTest {
  private IUserRepository userRepository;

  @BeforeEach
  void setUp() {
    userRepository = new UserRepositoryImpl();
  }

  @Test
  void testSave() {
    UserHibernate user = new UserHibernate(1, "user1", "password1", true);
    userRepository.save(user);

    // Use Hibernate to verify the save operation
    UserHibernate savedUser = userRepository.findById(1);

    assertNotNull(savedUser);
    assertEquals(1, savedUser.getId());
    assertEquals("user1", savedUser.getUsername());
    assertEquals("password1", savedUser.getPassword());
    assertTrue(savedUser.isActive());
  }
}