import entities.SomeEntity;
import net.ttddyy.dsproxy.QueryCount;
import net.ttddyy.dsproxy.QueryCountHolder;
import org.junit.jupiter.api.Test;
import util.HibernateUtil;

import java.util.Date;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class EntityCacheTest {
    @Test
    void l2CacheIsUsedAcrossSessions() {
        final int entityId = 1;
        HibernateUtil hibernateUtil = new HibernateUtil();

        // First session: create the entity
        hibernateUtil.runInTransaction(
                (session) -> {
                    session.persist(new SomeEntity(entityId));
                });

        // Second session: read the entity (should trigger a select from DB)
        Date entityCreationDateA =
                hibernateUtil.runInTransaction(
                        (session) -> {
                            return session.get(SomeEntity.class, entityId).getCreatedDate();
                        });

        // Third session: read the entity again (should hit the L2 cache)
        Date entityCreationDateB =
                hibernateUtil.runInTransaction(
                        (session) -> {
                            return session.get(SomeEntity.class, entityId).getCreatedDate();
                        });

        assertThat(entityCreationDateB).isEqualTo(entityCreationDateA);

        final QueryCount grandTotal = QueryCountHolder.getGrandTotal();
        assertThat(grandTotal.getInsert()).isEqualTo(1);
        assertThat(grandTotal.getSelect()).isEqualTo(0); // Only one select due to L2 cache
    }
}
