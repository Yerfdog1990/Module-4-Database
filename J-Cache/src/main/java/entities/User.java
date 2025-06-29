package entities;

import java.util.List;
import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {
  @Id private Integer id;

  private String name;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  @LazyCollection(value = LazyCollectionOption.TRUE)
  @OrderColumn(name = "comment_order")
  private List<Comment> comments;

  public User(int id, String name) {
    this.name = name;
    this.id = id;
  }

  public void addComment(Comment comment) {
    if (comments == null) {
      comments = new java.util.ArrayList<>();
    }
    comments.add(comment);
    comment.setUser(this);
  }
}
