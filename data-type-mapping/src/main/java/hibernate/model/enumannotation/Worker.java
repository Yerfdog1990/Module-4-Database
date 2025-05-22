package hibernate.model.enumannotation;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "worker")
@NoArgsConstructor
@Getter
@Setter
public class Worker {
  @Id
  @jakarta.persistence.GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "worker_type_string")
  private WorkerType workerType;

  // No args constructor
  public Worker(String name, WorkerType workerType) {
    this.name = name;
    this.workerType = workerType;
  }
}
