import static org.assertj.core.api.Assertions.assertThat;

import entities.SomeEntity;
import net.ttddyy.dsproxy.QueryCount;
import net.ttddyy.dsproxy.QueryCountHolder;
import org.hibernate.query.Query;
import org.junit.jupiter.api.Test;
import util.HibernateUtil;

public class QueryCacheTest {
    @Test
    void queryCacheIsUsed() {
        final int entityId = 2;
        HibernateUtil hibernateUtil = new HibernateUtil();

        // First session: create the entity
        hibernateUtil.runInTransaction(
                (session) -> {
                    session.persist(new SomeEntity(entityId));
                });

        // Second session: execute the query (should trigger a select from DB)
        hibernateUtil.runInTransaction(
                (session) -> {
                    Query<SomeEntity> query =
                            session.createQuery("FROM SomeEntity WHERE id = :id", SomeEntity.class);
                    query.setParameter("id", entityId);
                    query.setCacheable(true); // Enable query caching
                    SomeEntity entity = query.getSingleResult();
                    assertThat(entity).isNotNull();
                });

        // Third session: execute the same query (should hit the query cache)
        hibernateUtil.runInTransaction(
                (session) -> {
                    Query<SomeEntity> query =
                            session.createQuery("FROM SomeEntity WHERE id = :id", SomeEntity.class);
                    query.setParameter("id", entityId);
                    query.setCacheable(true); // Enable query caching
                    SomeEntity entity = query.getSingleResult();
                    assertThat(entity).isNotNull();
                });

        // Query count assertions
        QueryCount grandTotal = QueryCountHolder.getGrandTotal();
        assertThat(grandTotal.getInsert()).isEqualTo(1); // One insert
        assertThat(grandTotal.getSelect())
                .isEqualTo(1); // Only one select, second query should hit cache
    }
}

// ASSUMPTION -> Entity is annotated with @Cacheable and @Cache
// 1- We retrieve an entity by id using get/find on the session -> Cached by default
// 2 - If we want a Hibernate query instance to use the cache, use query.setCacheable(true);

