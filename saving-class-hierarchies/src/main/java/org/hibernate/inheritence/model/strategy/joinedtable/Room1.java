package org.hibernate.inheritence.model.strategy.joinedtable;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@PrimaryKeyJoinColumn(name = "room1_id")
@Data
@NoArgsConstructor
public class Room1 extends House{
    private String roomColor;

    public Room1(String name, String roomColor) {
        super(name);
        this.roomColor = roomColor;
    }
}
