package model;

import static hibernate.repository.HibernateUtil.doWithSession;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import hibernate.model.temporalannotation.Meeting;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;

public class MeetingTest {
  @Test
  void testTimeZoneStorage() {
    Meeting meeting =
        doWithSession(
            session -> {
              Meeting newMeeting =
                  new Meeting(
                      ZonedDateTime.parse("2021-01-01T13:50:00+07:00[Asia/Bangkok]"),
                      ZonedDateTime.parse("2021-01-01T14:00:00+07:00[Asia/Bangkok]"));
              return session.merge(newMeeting);
            });
    doWithSession(
        session -> {
          Meeting foundMeeting = session.find(Meeting.class, meeting.getId());
          assertNotNull(foundMeeting);
          ZoneOffset expectedOffset = ZoneOffset.of("+07:00");
          assertEquals(expectedOffset, foundMeeting.getStartTime().getZone());
          int actualHour = foundMeeting.getEndTime().getHour();
          assertEquals(14, actualHour);
          int actualStartMinute = foundMeeting.getStartTime().getMinute();
          assertEquals(50, actualStartMinute);
          return null;
        });
  }
}
