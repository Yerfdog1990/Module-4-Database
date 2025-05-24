import mappings.model.collections.Users;
import org.junit.jupiter.api.Test;

import java.util.*;

import static mappings.repository.HibernateUtil.doWithSession;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserTest {
    private List<String> messagesList;
    private Map<String, String> messagesMap;
    private Set<String> messagesSet;
    @Test
    void testList(){
        messagesList = new ArrayList<>();
        messagesList.add("message1");
        messagesList.add("message2");
        messagesList.add("message3");
        messagesList.add("message4");
        Users users = new Users(messagesList, null, null);
        Users persistedUser = doWithSession(session -> session.merge(users));
        assertNotNull(persistedUser);
        assertEquals(messagesList, persistedUser.getMessagesList());
        assertEquals(4, persistedUser.getMessagesList().size());
        assertEquals(2, persistedUser.getId());
        assertEquals("message1", persistedUser.getMessagesList().get(0));
        assertEquals("message2", persistedUser.getMessagesList().get(1));
        assertEquals("message3", persistedUser.getMessagesList().get(2));
        assertEquals("message4", persistedUser.getMessagesList().get(3));
        assertThat(persistedUser.getId()).isNotNull();
        assertThat(persistedUser.getMessagesList()).isNotNull();
    }
    @Test
    void testMap(){
        messagesMap = new HashMap<>();
        messagesMap.put("John", "Kenya");
        messagesMap.put("Jane", "Nigeria");
        messagesMap.put("Jill", "USA");
        Users users = new Users(messagesList,messagesMap, null);
        Users persistedUser = doWithSession(session -> session.merge(users));
        assertNotNull(persistedUser);
        assertEquals(messagesMap, persistedUser.getMessageMap());
        assertEquals(3, persistedUser.getMessageMap().size());
        assertEquals("John", persistedUser.getMessageMap().keySet().toArray()[0]);
        assertEquals("Kenya", persistedUser.getMessageMap().values().toArray()[0]);
    }

    void testSet(){
        messagesSet = new HashSet<>();
        messagesSet.add("John");
        messagesSet.add("Jane");
        messagesSet.add("Jill");
        Users users = new Users(messagesList,messagesMap, messagesSet);
        Users persistedUser = doWithSession(session -> session.merge(users));
        assertNotNull(persistedUser);
        assertEquals(messagesSet, persistedUser.getMessageSet());
        assertEquals(3, persistedUser.getMessageSet().size());
        assertEquals("John", persistedUser.getMessageSet().toArray()[0]);
        assertEquals("Jane", persistedUser.getMessageSet().toArray()[1]);
        assertEquals("Jill", persistedUser.getMessageSet().toArray()[2]);
    }
}
