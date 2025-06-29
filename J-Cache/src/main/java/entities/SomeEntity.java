package entities;

import java.util.Date;
import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)  // or NONSTRICT_READ_WRITE, depending on
// your use case
public class SomeEntity {

  public SomeEntity() {}

  public SomeEntity(final int id) {
    setId(id);
    setCreatedDate(new Date());
  }

  @Id private Integer id;

  private Date createdDate;

  public Date getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }
}
