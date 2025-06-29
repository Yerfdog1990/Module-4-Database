import static org.assertj.core.api.Assertions.assertThat;

import entities.Comment;
import entities.User;
import org.junit.jupiter.api.Test;
import util.HibernateUtil;

public class LazyCollectionTest {
    @Test
    void proveLazyCollection() {
        User user = new User(1, "John");
        user.addComment(new Comment(1, "Hello"));
        user.addComment(new Comment(2, "World"));
        user.addComment(new Comment(3, "Goodbye"));

        HibernateUtil hibernateUtil = new HibernateUtil();

        hibernateUtil.runInTransaction(
                (session) -> {
                    session.persist(user);
                });

        assertThat(hibernateUtil.getNumOfInserts()).isEqualTo(4);
        assertThat(hibernateUtil.getNumOfSelects()).isEqualTo(0);

        hibernateUtil.runInTransaction(
                (session) -> {
                    User userFromDb = session.get(User.class, user.getId());
                    assertThat(userFromDb.getComments().size()).isEqualTo(3);
                });

        assertThat(hibernateUtil.getNumOfSelects()).isEqualTo(2);
    }
}

