package mappings.model.temporalannotation;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.TimeZoneStorage;
import org.hibernate.annotations.TimeZoneStorageType;

import java.time.ZonedDateTime;

@Entity
@Table(name = "meeting")
@Data
@NoArgsConstructor
public class Meeting {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(name = "start_time", nullable = false)
  @TimeZoneStorage(TimeZoneStorageType.COLUMN)
  private ZonedDateTime startTime;

  @Column(name = "end_time", nullable = false)
  @TimeZoneStorage(TimeZoneStorageType.COLUMN)
  private ZonedDateTime endTime;

  // Constructor
  public Meeting(ZonedDateTime startTime, ZonedDateTime endTime) {
    this.startTime = startTime;
    this.endTime = endTime;
  }
}
