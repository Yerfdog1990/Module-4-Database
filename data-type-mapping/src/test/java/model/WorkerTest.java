package model;

import static hibernate.repository.HibernateUtil.doWithSession;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import hibernate.model.enumannotation.Worker;
import hibernate.model.enumannotation.WorkerType;
import org.hibernate.query.NativeQuery;
import org.junit.jupiter.api.Test;

public class WorkerTest {
  @Test
  void testStringToEnumMapping() {
    Worker johnDoe =
        doWithSession(
            session -> {
              Worker worker = new Worker("John Doe", WorkerType.FULL_TIME);
              return session.merge(worker);
            });
    doWithSession(
        session -> {
          Worker foundWorker = session.find(Worker.class, johnDoe.getId());
          // Check just mapping
          assertNotNull(foundWorker);
          assertEquals(WorkerType.FULL_TIME, foundWorker.getWorkerType());

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
}
