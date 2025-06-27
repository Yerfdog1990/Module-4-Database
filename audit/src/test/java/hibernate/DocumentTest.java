package hibernate;

import static hibernate.repository.hibernateUtils.doWithSession;
import static org.assertj.core.api.Assertions.assertThat;

import hibernate.model.Document;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;

public class DocumentTest {

  @Test
  void testCreationStamps() {
    Document savedDocument =
        doWithSession(
            session -> {
              Document document = new Document("First document");
              return session.merge(document);
            });

    // Check if the audit fields have been correctly updated
    Document foundDocument =
        doWithSession(session -> session.get(Document.class, savedDocument.getId()));
    System.out.println("Document creation time: " + foundDocument.getCreatedDate());
    System.out.println("Document update time: " + foundDocument.getUpdatedDate());
    assertThat(foundDocument.getCreatedDate().truncatedTo(ChronoUnit.SECONDS))
        .isEqualToIgnoringNanos(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
  }

  @Test
  void checkPersistedEntitiesStayInSync() {
    Document savedDocument =
        doWithSession(
            session -> {
              Document document = new Document("Second document");
              return session.merge(document);
            });
    Document updatedDocument =
        doWithSession(
            session -> {
              Document doc = session.get(Document.class, savedDocument.getId());
              doc.setName("Second document modified");
              return doc;
            });
    // Check if changes have been persisted
    Document documentAfterUpdate =
        doWithSession(session -> session.find(Document.class, updatedDocument.getId()));
    assertThat(updatedDocument.getName()).isEqualTo("Second document modified");
    assertThat(documentAfterUpdate.getName()).isEqualTo("Second document modified");
  }

  @Test
  void testUpdateStamps() throws InterruptedException {
    Document savedDocument =
        doWithSession(
            session -> {
              Document document = new Document("Second document");
              return session.merge(document);
            });

    Thread.sleep(2000L);

    Document updatedDocument =
        doWithSession(
            session -> {
              Document doc = session.get(Document.class, savedDocument.getId());
              doc.setName("Second document modified");
              return doc;
            });
    // Check if the audit fields have been correctly updated
    Document updatedDocumentReloaded =
        doWithSession(session -> session.get(Document.class, updatedDocument.getId()));
    System.out.println("Document creation time: " + updatedDocumentReloaded.getCreatedDate());
    System.out.println("Document update time: " + updatedDocumentReloaded.getUpdatedDate());
    assertThat(updatedDocumentReloaded.getCreatedDate()).isEqualTo(savedDocument.getCreatedDate());
    assertThat(updatedDocumentReloaded.getUpdatedDate().truncatedTo(ChronoUnit.SECONDS))
        .isEqualToIgnoringNanos(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
  }
}
