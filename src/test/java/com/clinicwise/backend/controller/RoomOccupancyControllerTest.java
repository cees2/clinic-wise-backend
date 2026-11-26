package com.clinicwise.backend.controller;

import com.clinicwise.backend.config.MyUserDetailsService;
import com.clinicwise.backend.config.SecurityConfig;
import com.clinicwise.backend.dto.request.CreateRoomOccupancyRequest;
import com.clinicwise.backend.service.JWTService;
import com.clinicwise.backend.service.RoomOccupancyService;
import com.clinicwise.backend.testdata.RoomOccupancyRequestBuilder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(RoomOccupancyController.class)
@Import(SecurityConfig.class)
class RoomOccupancyControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JsonMapper jsonMapper;
    @MockitoBean
    private JWTService jwtService;
    @MockitoBean
    private MyUserDetailsService myUserDetailsService;
    @MockitoBean
    private RoomOccupancyService roomOccupancyService;

    @ParameterizedTest
    @MethodSource("createRoomOccupancyValidationSource")
    @WithMockUser
    void createRoomOccupancyValidation(String field, String message, CreateRoomOccupancyRequest createRoomOccupancyRequest) throws Exception {
        mockMvc.perform(post("/room_occupancies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(createRoomOccupancyRequest)))
                .andExpectAll(
                        MockMvcResultMatchers.jsonPath("$.code").value("VALIDATION_ERROR"),
                        MockMvcResultMatchers.jsonPath("$.message").value("Incorrect input provided"),
                        MockMvcResultMatchers.jsonPath("$.validation_errors[0].fields[0]").value(field),
                        MockMvcResultMatchers.jsonPath("$.validation_errors[0].message").value(message)
                );
    }

    private static Stream<Arguments> createRoomOccupancyValidationSource() {
        return Stream.of(
                Arguments.of("room_id", "must not be null", RoomOccupancyRequestBuilder.aRoomOccupancyRequestBuilder().withRoomId(null).build()),
                Arguments.of("start_time", "must not be null", RoomOccupancyRequestBuilder.aRoomOccupancyRequestBuilder().withStartTime(null).build()),
                Arguments.of("start_time", "must be a date in the present or in the future", RoomOccupancyRequestBuilder.aRoomOccupancyRequestBuilder().withStartTime(LocalDateTime.now().minusDays(3)).build()),
                Arguments.of("end_time", "must not be null", RoomOccupancyRequestBuilder.aRoomOccupancyRequestBuilder().withEndTime(null).build()),
                Arguments.of("end_time", "must be a date in the present or in the future", RoomOccupancyRequestBuilder.aRoomOccupancyRequestBuilder().withEndTime(LocalDateTime.now().minusDays(3)).build()),
                Arguments.of("employee_id", "must not be null", RoomOccupancyRequestBuilder.aRoomOccupancyRequestBuilder().withEmployeeId(null).build())
        );
    }
}