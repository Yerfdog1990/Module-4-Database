package hibernate.model.temporalannotation;

import jakarta.persistence.*;
import java.util.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "event")
@Getter
@Setter
@NoArgsConstructor
public class Event {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private int id;

  @Column(name = "event_name", nullable = false)
  private String eventName;

  @Column(name = "event_date", nullable = false)
  @Temporal(TemporalType.DATE)
  private Date eventDate;

  @Column(name = "event_time", nullable = false)
  @Temporal(TemporalType.TIME)
  private Date eventTime;

  @Column(name = "event_time_date", nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date eventTimeDate;

  // Constructor
  public Event(String eventName, Date eventDate, Date eventTime, Date eventTimeDate) {
    this.eventName = eventName;
    this.eventDate = eventDate;
    this.eventTime = eventTime;
    this.eventTimeDate = eventTimeDate;
  }
}
