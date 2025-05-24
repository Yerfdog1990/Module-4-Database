package mappings.model.collections;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ElementCollection
    @CollectionTable(name = "users_messages")
    @Column(name = "message_list")
    private List<String> messagesList = new ArrayList<>();

    @ElementCollection
    @MapKeyColumn(name = "map_key")
    @Column(name = "map_value")
    @CollectionTable(name = "users_map")
    private Map<String, String> messageMap = new HashMap<>();

    @ElementCollection
    @CollectionTable(name = "users_set")
    @Column(name = "set_list")
    private Set<String> messageSet = new HashSet<>();
    // Constructor
    public Users(List<String> messagesList, Map<String, String> messageMap, Set<String> messageSet) {
        this.messagesList = messagesList;
        this.messageMap = messageMap;
        this.messageSet = messageSet;
    }
}

