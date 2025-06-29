package entities;

import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
public class Comment {
  @Id private Integer id;

  private String text;

  @ManyToOne
  @JoinColumn(name = "user_id")
  @ToString.Exclude
  private User user;

  public Comment(int id, String text) {
    this.text = text;
    this.id = id;
  }
}
