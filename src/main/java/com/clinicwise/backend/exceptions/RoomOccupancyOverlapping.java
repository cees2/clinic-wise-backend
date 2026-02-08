package com.clinicwise.backend.exceptions;

import java.time.LocalDateTime;

public class RoomOccupancyOverlapping extends RuntimeException {
    public RoomOccupancyOverlapping(LocalDateTime requestStartTime, LocalDateTime requestEndTime, LocalDateTime DBStartTime, LocalDateTime DBEndTime) {
        super(
                "An user or a room has a conflicted room occupancy. Room occupancy start time " +
                requestStartTime + " and end time: " + requestEndTime +
                " overlaps existing room occupancy with start time " +
                DBStartTime + " and end time " + DBEndTime
        );
    }
}
