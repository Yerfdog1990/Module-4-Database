package mappings.model.embeddedannotation;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "person")
@Setter
@Getter
@NoArgsConstructor
public class Citizenship {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(name = "name")
  private String name;

  @Embedded private Address address;

  // Constructor
  public Citizenship(String name, Address address) {
    this.name = name;
    this.address = address;
  }
}
