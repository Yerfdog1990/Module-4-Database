import static org.junit.jupiter.api.Assertions.*;

import hql.model.SchoolUser;
import hql.repository.IUserRepository;
import hql.repository.UserRepositoryImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserRepositoryImplTest {
  private IUserRepository userRepository;
  private SchoolUser user;

  @BeforeEach
  void setUp() {
    userRepository = new UserRepositoryImpl();
    user = new SchoolUser();
    user.setName("Test School");
    user.setEmail("test@school.com");
    user.setPhone("+15555555555");
    user.setPhysicalAddress("123 Test St");
    user.setPopulation(1000);
  }

  @AfterEach
  void tearDown() {
    try {
      userRepository.delete(user);
    } catch (Exception ignored) {
      // Ignore deletion errors in teardown
    }
  }

  @Test
  void testCreate() {
    userRepository.create(user);
    SchoolUser foundUser = userRepository.findByEmail("test@school.com");
    assertNotNull(foundUser);
    assertEquals("Test School", foundUser.getName());
  }

  @Test
  void testRead() {
    userRepository.create(user);
    SchoolUser foundUser = userRepository.read(user);
    assertNotNull(foundUser);
    assertEquals("Test School", foundUser.getName());
  }

  @Test
  void testUpdate() {
    userRepository.create(user);
    user.setName("Updated School");
    userRepository.update(user);
    SchoolUser foundUser = userRepository.findById(user.getId());
    assertNotNull(foundUser);
    assertEquals("Updated School", foundUser.getName());
  }

  @Test
  void testDelete() {
    userRepository.create(user);
    userRepository.delete(user);
    SchoolUser foundUser = userRepository.findById(user.getId());
    assertNull(foundUser);
  }

  @Test
  void testFindByName() {
    userRepository.create(user);
    SchoolUser foundUser = userRepository.findByName("Test School");
    assertNotNull(foundUser);
    assertEquals("Test School", foundUser.getName());
  }

  @Test
  void testFindByEmail() {
    userRepository.create(user);
    SchoolUser foundUser = userRepository.findByEmail("test@school.com");
    assertNotNull(foundUser);
    assertEquals("test@school.com", foundUser.getEmail());
  }

  @Test
  void testFindByPhone() {
    userRepository.create(user);
    SchoolUser foundUser = userRepository.findByPhone("+15555555555");
    assertNotNull(foundUser);
    assertEquals("+15555555555", foundUser.getPhone());
  }

  @Test
  void testFindByAddress() {
    userRepository.create(user);
    SchoolUser foundUser = userRepository.findByAddress("123 Test St");
    assertNotNull(foundUser);
    assertEquals("123 Test St", foundUser.getPhysicalAddress());
  }

  @Test
  void testFindById() {
    userRepository.create(user);
    SchoolUser foundUser = userRepository.findById(user.getId());
    assertNotNull(foundUser);
    assertEquals(user.getId(), foundUser.getId());
  }
}