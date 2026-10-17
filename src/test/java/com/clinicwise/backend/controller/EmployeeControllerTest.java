package com.clinicwise.backend.controller;

import com.clinicwise.backend.api.response.ApiResponse;
import com.clinicwise.backend.api.response.ListResponse;
import com.clinicwise.backend.config.MyUserDetailsService;
import com.clinicwise.backend.config.SecurityConfig;
import com.clinicwise.backend.dto.request.CreateEmployeeRequest;
import com.clinicwise.backend.dto.response.EmployeeResponse;
import com.clinicwise.backend.dto.response.PersonalDetailsResponse;
import com.clinicwise.backend.entity.Employee;
import com.clinicwise.backend.enums.Country;
import com.clinicwise.backend.enums.EmployeeRole;
import com.clinicwise.backend.enums.Gender;
import com.clinicwise.backend.service.EmployeeService;
import com.clinicwise.backend.service.JWTService;
import com.clinicwise.backend.testdata.EmployeeRequestBuilder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.context.annotation.Import;
import tools.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeController.class)
@Import(SecurityConfig.class)
class EmployeeControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockitoBean
    JWTService jwtService;
    @MockitoBean
    EmployeeService employeeService;
    @MockitoBean
    MyUserDetailsService myUserDetailsService;
    @Autowired
    private JsonMapper jsonMapper;

    @Test
    @WithMockUser
    @DisplayName("Should return employees list")
    void getAllEmployeesOk() throws Exception {
        List<EmployeeResponse> employees = List.of(
                new EmployeeResponse(1, LocalDate.now(), EmployeeRole.DOCTOR, null),
                new EmployeeResponse(2, LocalDate.now(), EmployeeRole.DOCTOR, null),
                new EmployeeResponse(3, LocalDate.now(), EmployeeRole.DOCTOR, null)
        );
        when(employeeService.getAllEmployees(any())).thenReturn(ListResponse.toResponse(employees, true));

        mockMvc.perform(
                get("/employees")
                        .param("page", "0")
                        .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(3));
    }

    @Test
    @WithMockUser
    @DisplayName("Should return an employee")
    void getUserOk() throws Exception {
        LocalDate startDate = LocalDate.now();
        String firstname = "Damian";
        String lastname = "Mirek";
        Gender gender = Gender.FEMALE;
        String address = "ddd";
        LocalDate dateOfBirth = LocalDate.now();
        String documentId = "AA 001";
        Country nationality = Country.FRANCE;
        String phoneNumber = "123123123";
        EmployeeResponse employee = new EmployeeResponse(1, startDate, EmployeeRole.DOCTOR, new PersonalDetailsResponse(firstname, lastname, gender, address, dateOfBirth, documentId, nationality, phoneNumber));
        when(employeeService.getEmployee(1)).thenReturn(ApiResponse.toResponse(employee));

        mockMvc.perform(get("/employees/1"))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.data.id").value(1),
                        jsonPath("$.data.start_date").value(startDate.toString()),
                        jsonPath("$.data.role").value(EmployeeRole.DOCTOR.name()),
                        jsonPath("$.data.user.firstname").value(firstname),
                        jsonPath("$.data.user.lastname").value(lastname),
                        jsonPath("$.data.user.gender").value(gender.name()),
                        jsonPath("$.data.user.address").value(address),
                        jsonPath("$.data.user.date_of_birth").value(dateOfBirth.toString()),
                        jsonPath("$.data.user.document_id").value(documentId),
                        jsonPath("$.data.user.nationality").value(nationality.name()),
                        jsonPath("$.data.user.phone_number").value(phoneNumber)
                );
        verify(employeeService).getEmployee(1);
    }

    @Test
    @WithMockUser
    @DisplayName("Should create an employee")
    void createUserOk() throws Exception {
        LocalDate startDate = LocalDate.now();
        String firstname = "Damian";
        String lastname = "Mirek";
        Gender gender = Gender.FEMALE;
        String address = "ddd";
        LocalDate dateOfBirth = LocalDate.now();
        String documentId = "AA 001";
        Country nationality = Country.FRANCE;
        String phoneNumber = "123123123";
        EmployeeRole role = EmployeeRole.DOCTOR;
        String username = "ddaaqq";
        String password = "testtest";
        CreateEmployeeRequest createEmployeeRequest = new CreateEmployeeRequest(startDate, firstname, lastname, gender, dateOfBirth, nationality, address, phoneNumber, role, documentId, username, password);
        EmployeeResponse employee = new EmployeeResponse(1, startDate, role, new PersonalDetailsResponse(firstname, lastname, gender, address, dateOfBirth, documentId, nationality, phoneNumber));
        when(employeeService.createEmployee(createEmployeeRequest)).thenReturn(ApiResponse.toResponse(employee));

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(createEmployeeRequest)))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.data.id").value(1),
                        jsonPath("$.data.start_date").value(startDate.toString()),
                        jsonPath("$.data.role").value(role.toString()),
                        jsonPath("$.data.user.firstname").value(firstname),
                        jsonPath("$.data.user.lastname").value(lastname),
                        jsonPath("$.data.user.gender").value(gender.toString()),
                        jsonPath("$.data.user.address").value(address),
                        jsonPath("$.data.user.date_of_birth").value(dateOfBirth.toString()),
                        jsonPath("$.data.user.document_id").value(documentId),
                        jsonPath("$.data.user.nationality").value(nationality.toString()),
                        jsonPath("$.data.user.phone_number").value(phoneNumber)
                );
        verify(employeeService).createEmployee(createEmployeeRequest);
    }

    private static Stream<Arguments> invalidRequests(){
        return Stream.of(
                Arguments.of("firstname", "must not be blank", EmployeeRequestBuilder.anEmployeeRequest().withFirstname(null).build()),
                Arguments.of("firstname", "must not be blank", EmployeeRequestBuilder.anEmployeeRequest().withFirstname("  ").build()),
                Arguments.of("lastname", "must not be blank", EmployeeRequestBuilder.anEmployeeRequest().withLastname(null).build()),
                Arguments.of("lastname", "must not be blank", EmployeeRequestBuilder.anEmployeeRequest().withLastname("  ").build()),
                Arguments.of("startDate", "must not be null", EmployeeRequestBuilder.anEmployeeRequest().withStartDate(null).build()),
                Arguments.of("gender", "must not be null", EmployeeRequestBuilder.anEmployeeRequest().withGender(null).build()),
                Arguments.of("dateOfBirth", "must not be null", EmployeeRequestBuilder.anEmployeeRequest().withDateOfBirth(null).build()),
                Arguments.of("nationality", "must not be null", EmployeeRequestBuilder.anEmployeeRequest().withNationality(null).build()),
                Arguments.of("address", "must not be blank", EmployeeRequestBuilder.anEmployeeRequest().withAddress(null).build()),
                Arguments.of("address", "must not be blank", EmployeeRequestBuilder.anEmployeeRequest().withAddress("    ").build()),
                Arguments.of("phoneNumber", "must not be blank", EmployeeRequestBuilder.anEmployeeRequest().withPhoneNumber(null).build()),
                Arguments.of("phoneNumber", "must not be blank", EmployeeRequestBuilder.anEmployeeRequest().withPhoneNumber("    ").build()),
                Arguments.of("role", "must not be null", EmployeeRequestBuilder.anEmployeeRequest().withRole(null).build()),
                Arguments.of("documentId", "must not be blank", EmployeeRequestBuilder.anEmployeeRequest().withDocumentId(null).build()),
                Arguments.of("documentId", "must not be blank", EmployeeRequestBuilder.anEmployeeRequest().withDocumentId("   ").build()),
                Arguments.of("username", "must not be blank", EmployeeRequestBuilder.anEmployeeRequest().withUsername(null).build()),
                Arguments.of("username", "must not be blank", EmployeeRequestBuilder.anEmployeeRequest().withUsername("   ").build()),
                Arguments.of("password", "must not be blank", EmployeeRequestBuilder.anEmployeeRequest().withPassword(null).build()),
                Arguments.of("password", "must not be blank", EmployeeRequestBuilder.anEmployeeRequest().withPassword("   ").build())
        );
    }

    @ParameterizedTest
    @MethodSource("invalidRequests")
    @WithMockUser
    void shouldReturnBadRequestWhenRequestIsInvalid(String field, String error, CreateEmployeeRequest employeeRequest) throws Exception {
        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(employeeRequest))
                )
                .andExpect(status().isUnprocessableContent())
                .andExpectAll(
                        jsonPath("$.code").value("VALIDATION_ERROR"),
                        jsonPath("$.message").value("Incorrect input provided"),
                        jsonPath("$.validation_errors[0].field").value(field),
                        jsonPath("$.validation_errors[0].value").value(error)
                );
    }
}