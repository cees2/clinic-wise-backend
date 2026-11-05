package com.clinicwise.backend.controller;

import com.clinicwise.backend.api.response.ApiResponse;
import com.clinicwise.backend.api.response.PageResponse;
import com.clinicwise.backend.common.list.filter.EmployeesFilter;
import com.clinicwise.backend.common.list.filter.ParsedFilter;
import com.clinicwise.backend.common.list.sort.SearchSelectBaseFilter;
import com.clinicwise.backend.config.MyUserDetailsService;
import com.clinicwise.backend.config.SecurityConfig;
import com.clinicwise.backend.dto.request.CreateEmployeeRequest;
import com.clinicwise.backend.dto.request.UpdateEmployeeRequest;
import com.clinicwise.backend.dto.response.EmployeeResponse;
import com.clinicwise.backend.dto.response.PersonalDetailsResponse;
import com.clinicwise.backend.entity.Employee;
import com.clinicwise.backend.enums.Country;
import com.clinicwise.backend.enums.EmployeeRole;
import com.clinicwise.backend.enums.Gender;
import com.clinicwise.backend.service.EmployeeService;
import com.clinicwise.backend.service.JWTService;
import com.clinicwise.backend.testdata.EmployeeBuilder;
import com.clinicwise.backend.testdata.EmployeeRequestBuilder;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.testcontainers.shaded.org.awaitility.core.ThrowingRunnable;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

    private LocalDate startDate = LocalDate.now();
    private String firstname = "Damian";
    private String lastname = "Mirek";
    private Gender gender = Gender.FEMALE;
    private String address = "ddd";
    private LocalDate dateOfBirth = LocalDate.now();
    private String documentId = "AA 001";
    private Country nationality = Country.FRANCE;
    private String phoneNumber = "123123123";
    private String username = "test@test.test";
    private EmployeeRole role = EmployeeRole.DOCTOR;
    private String password = "testtest";
    private static final String NOT_FOUND_MESSAGE = "Could not find an user with id: 1";

    @Test
    @WithMockUser
    @DisplayName("Should return employees list")
    void getAllEmployeesOk() throws Exception {
        List<EmployeeResponse> employees = List.of(
                new EmployeeResponse(1, LocalDate.now(), EmployeeRole.DOCTOR, null),
                new EmployeeResponse(2, LocalDate.now(), EmployeeRole.DOCTOR, null),
                new EmployeeResponse(3, LocalDate.now(), EmployeeRole.DOCTOR, null)
        );
        when(employeeService.getAllEmployees(any())).thenReturn(PageResponse.toResponse(employees, true));

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
        EmployeeResponse employee = new EmployeeResponse(1, startDate, EmployeeRole.DOCTOR, new PersonalDetailsResponse(username, firstname, lastname, gender, address, dateOfBirth, documentId, nationality, phoneNumber));
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
        CreateEmployeeRequest createEmployeeRequest = new CreateEmployeeRequest(startDate, firstname, lastname, gender, dateOfBirth, nationality, address, phoneNumber, role, documentId, username, password);
        EmployeeResponse employee = new EmployeeResponse(1, startDate, role, new PersonalDetailsResponse(username, firstname, lastname, gender, address, dateOfBirth, documentId, nationality, phoneNumber));
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
    void shouldReturnBadRequestWhenCreateUserBodyIsInvalid(String field, String error, CreateEmployeeRequest employeeRequest) throws Exception {
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

    @Test
    @WithMockUser
    @DisplayName("Should update an employee")
    void updateEmployeeOk() throws Exception{
        LocalDate newStartDate = LocalDate.now().plusDays(1);
        EmployeeRole newRole = EmployeeRole.REGISTRATION;
        String newName = "newName";
        String newLastName = "newLastName";
        Gender newGender = Gender.MALE;
        String newAddress = "newAddress";
        LocalDate newDateOfBirth = LocalDate.now().plusDays(1);
        String newDocumentId = "newDocumentId";
        Country newNationality = Country.ITALY;
        String newPhoneNumber = "newPhoneNumber";
        EmployeeResponse employee = new EmployeeResponse(1, newStartDate, newRole, new PersonalDetailsResponse(username, newName, newLastName, newGender, newAddress, newDateOfBirth, newDocumentId, newNationality, newPhoneNumber));
        UpdateEmployeeRequest updateEmployeeRequest = new UpdateEmployeeRequest(newStartDate, newName, newLastName, newGender, newDateOfBirth, newNationality, newAddress, newPhoneNumber, newRole, newDocumentId, username, password, true);
        when(employeeService.updateEmployee(anyInt(), any(UpdateEmployeeRequest.class))).thenReturn(new ApiResponse<>(employee));

        mockMvc.perform(patch(String.format("/employees/%d", employee.id()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(updateEmployeeRequest)))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.data.start_date").value(newStartDate.toString()),
                        jsonPath("$.data.role").value(newRole.name()),
                        jsonPath("$.data.user.address").value(newAddress),
                        jsonPath("$.data.user.date_of_birth").value(newDateOfBirth.toString()),
                        jsonPath("$.data.user.document_id").value(newDocumentId),
                        jsonPath("$.data.user.firstname").value(newName),
                        jsonPath("$.data.user.gender").value(newGender.name()),
                        jsonPath("$.data.user.lastname").value(newLastName),
                        jsonPath("$.data.user.nationality").value(newNationality.name()),
                        jsonPath("$.data.user.phone_number").value(newPhoneNumber)
                );
    }

    @Test
    @WithMockUser
    @DisplayName("Should delete an employee")
    void deleteEmployeeOk() throws Exception{
        Employee employee = EmployeeBuilder.anEmployee().build();

        mockMvc.perform(delete("/employees/1"))
                .andExpect(status().isNoContent());

        verify(employeeService).deleteEmployee(anyInt());
    }


    @Test
    @WithMockUser
    void employeeNotFoundGet() throws Exception{
        when(employeeService.getEmployee(anyInt())).thenThrow(new EntityNotFoundException(NOT_FOUND_MESSAGE));

        mockMvc.perform(get("/employees/1"))
                .andExpect(status().isNotFound())
                .andExpectAll(
                        jsonPath("$.code").value("ENTITY_NOT_FOUND"),
                        jsonPath("$.message").value("Could not find an user with id: 1")
                );
    }

    @Test
    @WithMockUser
    void employeeNotFoundPatch() throws Exception{
        UpdateEmployeeRequest body = new UpdateEmployeeRequest(startDate, firstname, lastname, gender,
                dateOfBirth, nationality, address, phoneNumber, role, documentId, username, password, true);
        when(employeeService.updateEmployee(anyInt(), any())).thenThrow(new EntityNotFoundException(NOT_FOUND_MESSAGE));

        mockMvc.perform(patch("/employees/1").contentType(MediaType.APPLICATION_JSON).content(jsonMapper.writeValueAsString(body)))
                .andExpect(status().isNotFound())
                .andExpectAll(
                        jsonPath("$.code").value("ENTITY_NOT_FOUND"),
                        jsonPath("$.message").value("Could not find an user with id: 1")
                );
    }

    @Test
    @WithMockUser
    void employeeNotFoundDelete() throws Exception{
        doThrow(new EntityNotFoundException(NOT_FOUND_MESSAGE)).when(employeeService).deleteEmployee(anyInt());

        mockMvc.perform(delete("/employees/1"))
                .andExpect(status().isNotFound())
                .andExpectAll(
                        jsonPath("$.code").value("ENTITY_NOT_FOUND"),
                        jsonPath("$.message").value("Could not find an user with id: 1")
                );
    }

    @Test
    void unauthorizedOnNoUserProvided() throws Exception{
        mockMvc.perform(get("/employees"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(employeeService);
    }

    @Test
    @WithMockUser
    void passesFilterWhenGettingAllTheEmployees() throws Exception {
        ArgumentCaptor<EmployeesFilter> argumentCaptor = ArgumentCaptor.forClass(EmployeesFilter.class);
        String GENDER_FILTER = "e" + ParsedFilter.filterConditionValueSeparator + "male";
        String START_DATE_FILTER = "gte" + ParsedFilter.filterConditionValueSeparator + "2026-11-01";

        mockMvc.perform(get("/employees")
                        .param("gender", GENDER_FILTER)
                        .param("start_date", START_DATE_FILTER)
                        .param("page", "0")
                        .param("size", "0"))
                .andExpect(status().isOk());

        verify(employeeService).getAllEmployees(argumentCaptor.capture());

        assertEquals(START_DATE_FILTER, argumentCaptor.getValue().getStartDate());
        assertEquals(GENDER_FILTER, argumentCaptor.getValue().getGender());
    }

    @Test
    @WithMockUser
    void search_select_ok() throws Exception{
        ArgumentCaptor<SearchSelectBaseFilter> captor = ArgumentCaptor.forClass(SearchSelectBaseFilter.class);

        mockMvc.perform(get("/employees/search_select")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk());

        verify(employeeService).getSearchSelect(captor.capture());

        assertEquals(20, captor.getValue().getSize());
        assertEquals(0, captor.getValue().getPage());
    }
}