package org.hibernate.inheritence.model.strategy.joinedtable;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@PrimaryKeyJoinColumn(name = "room2_id")
@Data
@NoArgsConstructor
public class Room2 extends House{
    private String roomColor;

    public Room2(String name, String roomColor) {
        super(name);
        this.roomColor = roomColor;
    }
}
