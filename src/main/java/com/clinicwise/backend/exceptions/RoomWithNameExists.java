package com.clinicwise.backend.exceptions;

public class RoomWithNameExists extends RuntimeException {
    public RoomWithNameExists(String roomName) {
        super("A room with name '" + roomName + "' already exists");
    }
}
