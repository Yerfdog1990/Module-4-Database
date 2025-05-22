package model;

import hibernate.model.temporalannotation.Event;
import hibernate.repository.HibernateUtil;
import java.sql.Date;
import java.util.Calendar;
import org.junit.jupiter.api.Test;

public class EventTest {
  @Test
  void verifyDateAndTimeAnnotations() {
    Event event = getEvent();

    // Persist the event
    HibernateUtil.doWithSession(session -> session.merge(event));

    // Retrieve saved events
    Event foundEvent =
        HibernateUtil.doWithSession(session -> session.find(Event.class, event.getId()));
    System.out.println("Event name: " + foundEvent.getEventName());
    System.out.println("Event date: " + foundEvent.getEventDate());
    System.out.println("Event time: " + foundEvent.getEventTime());
    System.out.println("Event time date: " + foundEvent.getEventTimeDate());
  }

  public static Event getEvent() {
    // Create date, time, and timestamp values using calendar
    Calendar calendar = Calendar.getInstance();

    // Set only the date (year, month, day)
    calendar.set(2020, 0, 1);
    Date eventDate = new Date(calendar.getTimeInMillis());

    // Set only the time (hour, minute, second)
    calendar.set(calendar.HOUR_OF_DAY, 13);
    calendar.set(calendar.MINUTE, 30);
    calendar.set(calendar.SECOND, 0);
    Date eventTime = new Date(calendar.getTimeInMillis());

    // Set both date and time
    calendar.set(2020, Calendar.JANUARY, 1, 13, 30, 0);
    Date eventTimeDate = new Date(calendar.getTimeInMillis());

    // Create an event with the date, time, and timestamp values
    Event event = new Event("New year", eventDate, eventTime, eventTimeDate);
    return event;
  }
}
