package com.clinicwise.backend.specification;

import com.clinicwise.backend.common.list.filter.RoomsOccupancyFilter;
import com.clinicwise.backend.config.MySqlTestContainersConfig;
import com.clinicwise.backend.entity.Employee;
import com.clinicwise.backend.entity.Room;
import com.clinicwise.backend.entity.RoomOccupancy;
import com.clinicwise.backend.repository.RoomOccupancyRepository;
import com.clinicwise.backend.testdata.EmployeeBuilder;
import com.clinicwise.backend.testdata.RoomBuilder;
import com.clinicwise.backend.testdata.RoomOccupancyBuilder;
import com.clinicwise.backend.testdata.UserBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(MySqlTestContainersConfig.class)
class RoomOccupancySpecificationTest {
    @Autowired
    private RoomOccupancyRepository roomOccupancyRepository;
    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    @DisplayName("Where filter returns room occupancies for a given day")
    void whereFilterDate() {
        LocalDateTime matchingDate = LocalDateTime.of(2023, 1, 10, 1, 0);
        Employee employee = testEntityManager.persist(EmployeeBuilder.anEmployee().withUser(UserBuilder.aUser().build()).build());
        Room room = testEntityManager.persist(RoomBuilder.aRoom().build());
        RoomOccupancy matchingRoomOccupancy1 = testEntityManager.persist(
                RoomOccupancyBuilder
                        .aRoomOccupancy()
                        .withStartTime(matchingDate.plusHours(8))
                        .withEndTime(matchingDate.plusHours(8).plusMinutes(30))
                        .withEmployee(employee)
                        .withRoom(room)
                        .build());
        RoomOccupancy matchingRoomOccupancy2 = testEntityManager.persist(
                RoomOccupancyBuilder
                        .aRoomOccupancy()
                        .withStartTime(matchingDate.plusHours(10))
                        .withEndTime(matchingDate.plusHours(10).plusMinutes(30))
                        .withEmployee(employee)
                        .withRoom(room)
                        .build());
        testEntityManager.persist(
                RoomOccupancyBuilder
                        .aRoomOccupancy()
                        .withStartTime(matchingDate.plusDays(2))
                        .withEndTime(matchingDate.plusDays(2).plusMinutes(30))
                        .withEmployee(employee)
                        .withRoom(room)
                        .build());
        testEntityManager.persist(
                RoomOccupancyBuilder
                        .aRoomOccupancy()
                        .withStartTime(matchingDate.minusDays(2))
                        .withEndTime(matchingDate.minusDays(2).plusMinutes(30))
                        .withEmployee(employee)
                        .withRoom(room)
                        .build());
        testEntityManager.flush();
        testEntityManager.clear();

        RoomsOccupancyFilter roomsOccupancyFilter = new RoomsOccupancyFilter(20, 0, null, "", "2023-01-10", null);
        List<RoomOccupancy> roomOccupancyList = roomOccupancyRepository.findAll(RoomOccupancySpecification.whereFilter(roomsOccupancyFilter));
        List<Integer> roomOccupancyIds = roomOccupancyList.stream().map(RoomOccupancy::getId).toList();

        assertEquals(2, roomOccupancyIds.size());
        assertTrue(roomOccupancyIds.containsAll(List.of(matchingRoomOccupancy1.getId(), matchingRoomOccupancy2.getId())));
    }

    @Test
    @DisplayName("Where filter returns room occupancies for given rooms")
    void whereFilterRooms() {
        LocalDateTime matchingDate = LocalDateTime.of(2023, 1, 10, 1, 0);
        Employee employee = testEntityManager.persist(EmployeeBuilder.anEmployee().withUser(UserBuilder.aUser().build()).build());
        Room room1 = testEntityManager.persist(RoomBuilder.aRoom().build());
        Room room2 = testEntityManager.persist(RoomBuilder.aRoom().build());
        Room room3 = testEntityManager.persist(RoomBuilder.aRoom().build());
        RoomOccupancy matchingRoomOccupancy1 = testEntityManager.persist(
                RoomOccupancyBuilder
                        .aRoomOccupancy()
                        .withStartTime(matchingDate.plusHours(8))
                        .withEndTime(matchingDate.plusHours(8).plusMinutes(30))
                        .withEmployee(employee)
                        .withRoom(room1)
                        .build());
        RoomOccupancy matchingRoomOccupancy2 = testEntityManager.persist(
                RoomOccupancyBuilder
                        .aRoomOccupancy()
                        .withStartTime(matchingDate.plusHours(10))
                        .withEndTime(matchingDate.plusHours(10).plusMinutes(30))
                        .withEmployee(employee)
                        .withRoom(room1)
                        .build());
        RoomOccupancy matchingRoomOccupancy3 = testEntityManager.persist(
                RoomOccupancyBuilder
                        .aRoomOccupancy()
                        .withStartTime(matchingDate.plusHours(11))
                        .withEndTime(matchingDate.plusHours(11).plusMinutes(30))
                        .withEmployee(employee)
                        .withRoom(room2)
                        .build());
        testEntityManager.persist(
                RoomOccupancyBuilder
                        .aRoomOccupancy()
                        .withStartTime(matchingDate.minusDays(2))
                        .withEndTime(matchingDate.minusDays(2).plusMinutes(30))
                        .withEmployee(employee)
                        .withRoom(room3)
                        .build());
        testEntityManager.flush();
        testEntityManager.clear();

        RoomsOccupancyFilter roomsOccupancyFilter = new RoomsOccupancyFilter(20, 0, null, "", "2023-01-10", room1.getId().toString() + "," + room2.getId().toString());
        List<RoomOccupancy> roomOccupancyList = roomOccupancyRepository.findAll(RoomOccupancySpecification.whereFilter(roomsOccupancyFilter));
        List<Integer> roomOccupancyIds = roomOccupancyList.stream().map(RoomOccupancy::getId).toList();

        assertEquals(3, roomOccupancyIds.size());
        assertTrue(roomOccupancyIds.containsAll(List.of(matchingRoomOccupancy1.getId(), matchingRoomOccupancy2.getId(), matchingRoomOccupancy3.getId())));
    }

    @Test
    @DisplayName("Overlapping room occupancies - overlapping both the date and the employee")
    void overlappingDatesAndEmployee() {
        Employee employee = testEntityManager.persist(EmployeeBuilder.anEmployee().withUser(UserBuilder.aUser().build()).build());
        Employee otherEmployee = testEntityManager.persist(EmployeeBuilder.anEmployee().withUser(UserBuilder.aUser().build()).build());
        Room room = testEntityManager.persist(RoomBuilder.aRoom().build());
        Room otherRoom = testEntityManager.persist(RoomBuilder.aRoom().build());
        LocalDateTime overlappingDate = LocalDateTime.of(2023, 1, 10, 11, 0);
        RoomOccupancy overlappingRoomOccupancy = testEntityManager.persist(
                RoomOccupancyBuilder
                        .aRoomOccupancy()
                        .withStartTime(overlappingDate)
                        .withEndTime(overlappingDate.plusMinutes(30))
                        .withEmployee(employee)
                        .withRoom(otherRoom)
                        .build()
        );
        testEntityManager.persist(
                RoomOccupancyBuilder
                        .aRoomOccupancy()
                        .withStartTime(overlappingDate.minusHours(2))
                        .withEndTime(overlappingDate.minusHours(1))
                        .withEmployee(employee)
                        .withRoom(otherRoom)
                        .build()
        );
        testEntityManager.persist(
                RoomOccupancyBuilder
                        .aRoomOccupancy()
                        .withStartTime(overlappingDate)
                        .withEndTime(overlappingDate.plusMinutes(30))
                        .withEmployee(otherEmployee)
                        .withRoom(otherRoom)
                        .build()
        );
        RoomOccupancy roomOccupancyToBeConflicted = testEntityManager.persist(
                RoomOccupancyBuilder
                        .aRoomOccupancy()
                        .withStartTime(overlappingDate.plusMinutes(15))
                        .withEndTime(overlappingDate.plusMinutes(45))
                        .withEmployee(employee)
                        .withRoom(room)
                        .build()
        );
        testEntityManager.flush();
        testEntityManager.clear();

        List<RoomOccupancy> overlappingRoomOccupancies = roomOccupancyRepository.findAll(RoomOccupancySpecification.overlappingDatesAndRoomOrEmployee(roomOccupancyToBeConflicted));
        List<Integer> overlappingRoomOccupancyIds = overlappingRoomOccupancies.stream().map(RoomOccupancy::getId).toList();

        assertEquals(List.of(overlappingRoomOccupancy.getId()), overlappingRoomOccupancyIds);
    }

    @Test
    @DisplayName("Overlapping room occupancies - overlapping both the date and the room")
    void overlappingDatesAndRoom() {
        Employee employee = testEntityManager.persist(EmployeeBuilder.anEmployee().withUser(UserBuilder.aUser().build()).build());
        Employee otherEmployee = testEntityManager.persist(EmployeeBuilder.anEmployee().withUser(UserBuilder.aUser().build()).build());
        Room room = testEntityManager.persist(RoomBuilder.aRoom().build());
        Room otherRoom = testEntityManager.persist(RoomBuilder.aRoom().build());
        LocalDateTime overlappingDate = LocalDateTime.of(2023, 1, 10, 11, 0);
        RoomOccupancy overlappingRoomOccupancy = testEntityManager.persist(
                RoomOccupancyBuilder
                        .aRoomOccupancy()
                        .withStartTime(overlappingDate)
                        .withEndTime(overlappingDate.plusMinutes(30))
                        .withEmployee(otherEmployee)
                        .withRoom(room)
                        .build()
        );
        testEntityManager.persist(
                RoomOccupancyBuilder
                        .aRoomOccupancy()
                        .withStartTime(overlappingDate.minusHours(2))
                        .withEndTime(overlappingDate.minusHours(1))
                        .withEmployee(otherEmployee)
                        .withRoom(room)
                        .build()
        );
        testEntityManager.persist(
                RoomOccupancyBuilder
                        .aRoomOccupancy()
                        .withStartTime(overlappingDate)
                        .withEndTime(overlappingDate.plusMinutes(30))
                        .withEmployee(otherEmployee)
                        .withRoom(otherRoom)
                        .build()
        );
        RoomOccupancy roomOccupancyToBeConflicted = testEntityManager.persist(
                RoomOccupancyBuilder
                        .aRoomOccupancy()
                        .withStartTime(overlappingDate.plusMinutes(15))
                        .withEndTime(overlappingDate.plusMinutes(45))
                        .withEmployee(employee)
                        .withRoom(room)
                        .build()
        );
        testEntityManager.flush();
        testEntityManager.clear();

        List<RoomOccupancy> overlappingRoomOccupancies = roomOccupancyRepository.findAll(RoomOccupancySpecification.overlappingDatesAndRoomOrEmployee(roomOccupancyToBeConflicted));
        List<Integer> overlappingRoomOccupancyIds = overlappingRoomOccupancies.stream().map(RoomOccupancy::getId).toList();

        assertEquals(List.of(overlappingRoomOccupancy.getId()), overlappingRoomOccupancyIds);
    }

    @Test
    @DisplayName("Overlapping room occupancies - overlapping should exclude self room occupancy while updating")
    void overlappingExcludeSelf() {
        Employee employee = testEntityManager.persist(EmployeeBuilder.anEmployee().withUser(UserBuilder.aUser().build()).build());
        Room room = testEntityManager.persist(RoomBuilder.aRoom().build());
        LocalDateTime overlappingDate = LocalDateTime.of(2023, 1, 10, 11, 0);
        RoomOccupancy roomOccupancyFromDB = testEntityManager.persist(
                RoomOccupancyBuilder
                        .aRoomOccupancy()
                        .withStartTime(overlappingDate)
                        .withEndTime(overlappingDate.plusMinutes(30))
                        .withEmployee(employee)
                        .withRoom(room)
                        .build()
        );
        RoomOccupancy roomOccupancyToBeUpdated = RoomOccupancyBuilder
                        .aRoomOccupancy()
                        .withStartTime(overlappingDate.plusMinutes(15))
                        .withEndTime(overlappingDate.plusMinutes(45))
                        .withEmployee(employee)
                        .withRoom(room)
                        .withId(roomOccupancyFromDB.getId())
                        .build();
        testEntityManager.flush();
        testEntityManager.clear();

        List<RoomOccupancy> overlappingRoomOccupancies = roomOccupancyRepository.findAll(RoomOccupancySpecification.overlappingDatesAndRoomOrEmployee(roomOccupancyToBeUpdated));

        assertTrue(overlappingRoomOccupancies.isEmpty());
    }
}