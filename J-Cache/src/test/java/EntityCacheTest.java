import entities.SomeEntity;
import net.ttddyy.dsproxy.QueryCount;
import net.ttddyy.dsproxy.QueryCountHolder;
import org.hibernate.query.Query;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static util.HibernateUtil.runInTransaction;

public class EntityCacheTest {
    @Test
    void l2CacheIsUsedAcrossSessions() {
        final Integer entityId = 1;
        // First session: create the entity
        runInTransaction(
                (session) -> {
                    session.persist(new SomeEntity(entityId));
                });

        // Second session: read the entity (should trigger a select from DB)
        Date entityCreationDateA =
                runInTransaction(
                        (session) -> {
                            return session.get(SomeEntity.class, entityId).getCreatedDate();
                        });

        // Third session: reread the entity (should hit the L2 cache)
        Date entityCreationDateB =
                runInTransaction(
                        (session) -> {
                            return session.get(SomeEntity.class, entityId).getCreatedDate();
                        });

        assertThat(entityCreationDateB).isEqualTo(entityCreationDateA);

        final QueryCount grandTotal = QueryCountHolder.getGrandTotal();
        assertThat(grandTotal.getInsert()).isEqualTo(1);
        assertThat(grandTotal.getSelect()).isEqualTo(0); // Only one select due to L2 cache
    }

    @Test
    void queryCacheIsUsedAcrossSessions() {
        final int entityId = 2;
        runInTransaction(session -> {
            session.persist(new SomeEntity(entityId));
        });

        // In a different session, execute a query that triggers a select statement
        runInTransaction(session -> {
            Query<SomeEntity> query = session.createQuery("FROM SomeEntity WHERE id = :id", SomeEntity.class);
            query.setParameter("id", entityId);
            query.setCacheable(true);
            SomeEntity entity = query.getSingleResult();
            assertThat(entity).isNotNull();
        });

        // Execute the same query to trigger another select statement
        runInTransaction(session -> {
            Query<SomeEntity> query = session.createQuery("FROM SomeEntity WHERE id = :id", SomeEntity.class);
            query.setParameter("id", entityId);
            query.setCacheable(true);
            SomeEntity entity = query.getSingleResult();
            assertThat(entity).isNotNull();
        });
        // Count the number of select query statements executed
        QueryCount grandTotal = QueryCountHolder.getGrandTotal();
        System.out.printf("Number of select statements executed: %d%n", grandTotal.getSelect());
        System.out.printf("Number of insert statements executed: %d%n", grandTotal.getInsert());
        assertThat(grandTotal.getSelect()).isEqualTo(1);
    }
}
