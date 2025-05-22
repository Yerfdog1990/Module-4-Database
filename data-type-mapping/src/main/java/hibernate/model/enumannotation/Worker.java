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
  private WorkerType workerTypeString;

  @Enumerated(EnumType.ORDINAL)
  @Column(name = "worker_type_integer")
  private WorkerType workerTypeInteger;

  // No args constructor
  public Worker(String name, WorkerType workerTypeString, WorkerType workerTypeInteger) {
    this.name = name;
    this.workerTypeString = workerTypeString;
    this.workerTypeInteger = workerTypeInteger;
  }
}
