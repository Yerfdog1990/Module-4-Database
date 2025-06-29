import entities.SomeEntity;
import net.ttddyy.dsproxy.QueryCount;
import net.ttddyy.dsproxy.QueryCountHolder;
import org.junit.jupiter.api.Test;
import util.HibernateUtil;

import java.util.Date;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class CacheEvictionTest {
    @Test
    void cacheEviction() {
        HibernateUtil hibernateUtil = new HibernateUtil();

        // First session: create and persist the entity
        SomeEntity entity = new SomeEntity();
        entity.setId(1);
        entity.setCreatedDate(new Date());

        hibernateUtil.runInTransaction(
                session -> {
                    session.persist(entity);
                });

        // Second session: read the entity (should trigger a select from DB)
        Date entityCreationDateA =
                hibernateUtil.runInTransaction(
                        (session) -> {
                            return session.get(SomeEntity.class, 1).getCreatedDate();
                        });

        // Third session: read the entity again (should hit the L2 cache)
        Date entityCreationDateB =
                hibernateUtil.runInTransaction(
                        (session) -> {
                            return session.get(SomeEntity.class, 1).getCreatedDate();
                        });

        assertThat(entityCreationDateB).isEqualTo(entityCreationDateA);

        // Fourth session: evict the entity
        hibernateUtil.evictEntity(SomeEntity.class);

        // Fifth session: read the entity again (should trigger a select from DB)
        Date entityCreationDateC =
                hibernateUtil.runInTransaction(
                        (session) -> {
                            return session.get(SomeEntity.class, 1).getCreatedDate();
                        });

        assertThat(entityCreationDateC.getTime()).isEqualTo(entityCreationDateA.getTime());

        final QueryCount grandTotal = QueryCountHolder.getGrandTotal();
        assertThat(grandTotal.getInsert()).isEqualTo(1);
        assertThat(grandTotal.getSelect()).isEqualTo(1);
    }
}
