package com.clinicwise.backend.testdata;


import com.clinicwise.backend.entity.Room;

import java.time.LocalDateTime;

public class RoomBuilder {
    private Integer id = 1;
    private LocalDateTime createdAt = LocalDateTime.now().plusMinutes(30);
    private String name = "A01";

    public static RoomBuilder aRoom(){return new RoomBuilder();}
    public RoomBuilder withId(Integer id){this.id = id; return this;}
    public RoomBuilder withCreatedAt(LocalDateTime createdAt){this.createdAt = createdAt; return this;}
    public RoomBuilder withName(String name){this.name = name; return this;}

    public Room build(){
        Room room = new Room();
        room.setId(id);
        room.setCreatedAt(createdAt);
        room.setName(name);
        return room;
    }
}
