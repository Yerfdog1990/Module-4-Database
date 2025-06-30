package entities;

import java.util.Date;
import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "some_entity")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)  // or NON-STRICT_READ_WRITE, depending on your use case
@Data
@NoArgsConstructor
public class SomeEntity {

  @Id private Integer id;

  private Date createdDate;

  public SomeEntity(final int id) {
    setId(id);
    setCreatedDate(new Date());
  }
}
