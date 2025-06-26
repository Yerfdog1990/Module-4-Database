package hibernate.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Data
@NoArgsConstructor
public class Document {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(name = "created_at", updatable = false)
  @CreationTimestamp
  private LocalDate createdDate;

  @Column(name = "updated_at")
  @UpdateTimestamp
  private LocalDate updatedDate;

  // Constructor
  public Document(String name) {
    this.name = name;
    this.createdDate = LocalDate.now();
  }
}
