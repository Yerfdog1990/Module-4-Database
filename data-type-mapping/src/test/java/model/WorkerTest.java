package model;

import static hibernate.repository.HibernateUtil.doWithSession;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import hibernate.model.enumannotation.Worker;
import hibernate.model.enumannotation.WorkerType;
import org.hibernate.query.NativeQuery;
import org.junit.jupiter.api.Test;

public class WorkerTest {
  /**
   * Test the enum mapping to string representation in a database. Verifies that WorkerType enum is
   * stored as a STRING in the database.
   */
  @Test
  void testEnumMappingToString() {
    // Create and persist a new Worker with the FULL_TIME type
    Worker johnDoe =
        doWithSession(
            session -> {
              Worker worker = new Worker("John Doe", WorkerType.FULL_TIME, null);
              return session.merge(worker);
            });
    // Verify the persisted worker type through a Hibernate and direct SQL query
    doWithSession(
        session -> {
          // First, verify through Hibernate entity
          Worker foundWorker = session.find(Worker.class, johnDoe.getId());
          assertNotNull(foundWorker);
          assertEquals(WorkerType.FULL_TIME, foundWorker.getWorkerTypeString());

          NativeQuery<String> nativeQuery =
              session
                  .createNativeQuery(
                      "SELECT worker_type_string FROM worker WHERE id = :workerId", String.class)
                  .setParameter("workerId", johnDoe.getId());
          String foundWorkerType = nativeQuery.getSingleResult();
          assertNotNull(foundWorkerType);
          assertEquals("FULL_TIME", foundWorkerType);
          return null;
        });
  }

  /**
   * Test enum mapping to integer representation in database. Verifies that WorkerType enum is
   * stored as an ORDINAL (integer) in the database.
   */
  @Test
  void testEnumMappingToInteger() {
    // Create and persist a new Worker with PART_TIME type
    Worker johnDoe =
        doWithSession(session -> session.merge(new Worker("John Doe", null, WorkerType.PART_TIME)));
    // Verify the worker type is correctly persisted through Hibernate
    doWithSession(
        session -> {
          Worker worker = session.find(Worker.class, johnDoe.getId());
          assertNotNull(worker);
          assertEquals(WorkerType.PART_TIME, worker.getWorkerTypeInteger());
          return session.merge(worker);
        });
    // Verify the worker type through both Hibernate and direct SQL query
    doWithSession(
        session -> {
          // Verify through Hibernate entity
          Worker foundWorker = session.find(Worker.class, johnDoe.getId());
          assertNotNull(foundWorker);
          assertEquals(WorkerType.PART_TIME, foundWorker.getWorkerTypeInteger());

          NativeQuery<Integer> nativeQuery =
              session
                  .createNativeQuery(
                      "SELECT worker_type_integer FROM worker WHERE id = :workerId", Integer.class)
                  .setParameter("workerId", johnDoe.getId());
          Integer storedWorkerType = nativeQuery.getSingleResult();
          assertNotNull(storedWorkerType);
          assertEquals(1, storedWorkerType);
          return null;
        });
  }
}
