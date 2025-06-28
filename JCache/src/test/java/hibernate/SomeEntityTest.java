package hibernate;

import hibernate.model.SomeEntity;
import hibernate.repository.HibernateUtils;
import net.ttddyy.dsproxy.QueryCount;
import net.ttddyy.dsproxy.QueryCountHolder;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static hibernate.repository.HibernateUtils.runInTransaction;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class SomeEntityTest {

    @Test
    void l2CacheIsUsedAcrossSession(){
        final Integer id = 1;
        runInTransaction(session -> {
            SomeEntity entity = new SomeEntity(id);
            session.persist(entity);
            return entity;
        });

        // Read the entity (should trigger a query)
        SomeEntity loadedEntity1 = runInTransaction(session -> session.find(SomeEntity.class, id));

        // Update the entity again
        SomeEntity loadedEntity2 = runInTransaction(session -> {
            SomeEntity entity = session.find(SomeEntity.class, id);
            entity.setCreateDate(new Date());
            return entity;
        });
        // Read the entity again (should not trigger a query)
        SomeEntity loadedEntity3 = runInTransaction(session -> session.find(SomeEntity.class, id));

        QueryCount grandTotal = QueryCountHolder.getGrandTotal();
        assertThat(grandTotal.getInsert()).isEqualTo(1);
        assertThat(grandTotal.getUpdate()).isEqualTo(1);

    }
}
