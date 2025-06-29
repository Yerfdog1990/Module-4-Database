import entities.Comment;
import entities.User;
import java.util.List;
import org.hibernate.query.Query;
import org.junit.jupiter.api.Test;
import util.HibernateUtil;

import static org.assertj.core.api.Assertions.assertThat;


public class JoinFetchTest {
    @Test
    void proveJoinFetch() {
        // Create a user with a comment
        User user = new User(1, "John");
        Comment comment = new Comment(1, "Hello");
        user.addComment(comment);

        // Persist the user
        HibernateUtil hibernateUtil = new HibernateUtil();
        hibernateUtil.runInTransaction(
                (session) -> {
                    session.persist(user);
                });

        // Expect 2 insertions; one for the user and one for the comment
        assertThat(hibernateUtil.getNumOfInserts()).isEqualTo(2);

        // Prove that the relationship is loaded lazily by default
        hibernateUtil.runInTransaction(
                (session) -> {
                    User userFromDb = session.get(User.class, user.getId());
                    List<Comment> comments = userFromDb.getComments();
                    assertThat(comments).hasSize(1);
                    assertThat(hibernateUtil.getNumOfSelects()).isEqualTo(2);
                });

        // Prove now that join fetch loads the relationship eagerly
        hibernateUtil.runInTransaction(
                (session) -> {
                    Query<User> query =
                            session.createQuery("FROM User u JOIN FETCH u.comments WHERE u.id = :id", User.class);
                    query.setParameter("id", user.getId());
                    User singleResult = query.getSingleResult();
                    // We get, again, 1 comment
                    assertThat(singleResult.getComments()).hasSize(1);
                    // And now only 1 select is added to the count, proving that getting the comments on the
                    // object didn't trigger another select
                    assertThat(hibernateUtil.getNumOfSelects()).isEqualTo(3);
                });
    }
}