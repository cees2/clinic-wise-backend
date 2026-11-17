package com.clinicwise.backend.controller;

import com.clinicwise.backend.api.response.ApiResponse;
import com.clinicwise.backend.api.response.PageResponse;
import com.clinicwise.backend.config.MyUserDetailsService;
import com.clinicwise.backend.config.SecurityConfig;
import com.clinicwise.backend.dto.request.CreatePatientRequest;
import com.clinicwise.backend.dto.response.PatientResponse;
import com.clinicwise.backend.dto.response.PersonalDetailsResponse;
import com.clinicwise.backend.enums.Country;
import com.clinicwise.backend.enums.Gender;
import com.clinicwise.backend.enums.PatientSubscriptionPlan;
import com.clinicwise.backend.service.JWTService;
import com.clinicwise.backend.service.PatientService;
import com.clinicwise.backend.testdata.PatientRequestBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PatientController.class)
@Import(SecurityConfig.class)
class PatientControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private JWTService jwtService;
    @MockitoBean
    private PatientService patientService;
    @Autowired
    private JsonMapper jsonMapper;
    @MockitoBean
    private MyUserDetailsService myUserDetailsService;

    @Test
    @WithMockUser
    @DisplayName("Should return patients list")
    void getAllPatients() throws Exception{
        List<PatientResponse> patientResponseList = List.of(
                new PatientResponse(1, LocalDate.now(), PatientSubscriptionPlan.BASIC, null),
                new PatientResponse(2, LocalDate.now(), PatientSubscriptionPlan.PLATINUM, null),
                new PatientResponse(3, LocalDate.now(), PatientSubscriptionPlan.PREMIUM, null)
        );
        when(patientService.getAllPatients(any())).thenReturn(PageResponse.toResponse(patientResponseList, true));

        mockMvc.perform(
                get("/patients")
                    .param("size", "10")
                    .param("page", "0")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(3));
    }

    @Test
    @WithMockUser
    @DisplayName("Should return a patient")
    void getPatient() throws Exception{
        int patientId = 1;
        String username = "username";
        String firstname = "John";
        String lastname = "Smith";
        Gender gender = Gender.MALE;
        String address = "Krakow, ul. powstancow 32";
        LocalDate dateOfBirth = LocalDate.now();
        String documentId = "AA 444";
        Country nationality = Country.FRANCE;
        String phoneNumber = "111222333";
        LocalDate startDate = LocalDate.now();
        PersonalDetailsResponse personalDetailsResponse = new PersonalDetailsResponse(username, firstname, lastname, gender, address, dateOfBirth, documentId, nationality, phoneNumber);
        PatientResponse patientResponse = new PatientResponse(1, startDate, PatientSubscriptionPlan.BASIC, personalDetailsResponse);

        when(patientService.getPatient(patientId)).thenReturn(ApiResponse.toResponse(patientResponse));

        mockMvc.perform(get(String.format("/patients/%d", patientId)))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.data.id").value(1),
                        jsonPath("$.data.patient_subscription_plan").value(PatientSubscriptionPlan.BASIC.name()),
                        jsonPath("$.data.start_date").value(startDate.toString()),
                        jsonPath("$.data.user.address").value(address),
                        jsonPath("$.data.user.date_of_birth").value(dateOfBirth.toString()),
                        jsonPath("$.data.user.document_id").value(documentId),
                        jsonPath("$.data.user.firstname").value(firstname),
                        jsonPath("$.data.user.gender").value(gender.name()),
                        jsonPath("$.data.user.lastname").value(lastname),
                        jsonPath("$.data.user.nationality").value(nationality.name()),
                        jsonPath("$.data.user.phone_number").value(phoneNumber),
                        jsonPath("$.data.user.username").value(username)
                );
    }

    private static Stream<Arguments> invalidCreationData(){
        return Stream.of(
                Arguments.of("firstname", "must not be blank", PatientRequestBuilder.aPatientRequestBuilder().withFirstName(null).build()),
                Arguments.of("firstname", "must not be blank", PatientRequestBuilder.aPatientRequestBuilder().withFirstName("  ").build()),
                Arguments.of("lastname", "must not be blank", PatientRequestBuilder.aPatientRequestBuilder().withLastName(null).build()),
                Arguments.of("lastname", "must not be blank", PatientRequestBuilder.aPatientRequestBuilder().withLastName("  ").build()),
                Arguments.of("gender", "must not be null", PatientRequestBuilder.aPatientRequestBuilder().withGender(null).build()),
                Arguments.of("dateOfBirth", "must not be null", PatientRequestBuilder.aPatientRequestBuilder().withDateOfBirth(null).build()),
                Arguments.of("documentId", "must not be blank", PatientRequestBuilder.aPatientRequestBuilder().withDocumentId(null).build()),
                Arguments.of("documentId", "must not be blank", PatientRequestBuilder.aPatientRequestBuilder().withDocumentId("   ").build()),
                Arguments.of("address", "must not be blank", PatientRequestBuilder.aPatientRequestBuilder().withAddress(null).build()),
                Arguments.of("address", "must not be blank", PatientRequestBuilder.aPatientRequestBuilder().withAddress("  ").build()),
                Arguments.of("nationality", "must not be null", PatientRequestBuilder.aPatientRequestBuilder().withNationality(null).build()),
                Arguments.of("phoneNumber", "must not be blank", PatientRequestBuilder.aPatientRequestBuilder().withPhoneNumber(null).build()),
                Arguments.of("phoneNumber", "must not be blank", PatientRequestBuilder.aPatientRequestBuilder().withPhoneNumber("  ").build()),
                Arguments.of("startDate", "must not be null", PatientRequestBuilder.aPatientRequestBuilder().withStartDate(null).build()),
                Arguments.of("patientSubscriptionPlan", "must not be null", PatientRequestBuilder.aPatientRequestBuilder().withPatientSubscriptionPlan(null).build()),
                Arguments.of("username", "must not be blank", PatientRequestBuilder.aPatientRequestBuilder().withUsername(null).build()),
                Arguments.of("username", "must not be blank", PatientRequestBuilder.aPatientRequestBuilder().withUsername("  ").build()),
                Arguments.of("password", "must not be blank", PatientRequestBuilder.aPatientRequestBuilder().withPassword(null).build()),
                Arguments.of("password", "must not be blank", PatientRequestBuilder.aPatientRequestBuilder().withPassword("   ").build())
        );
    }

    @WithMockUser
    @ParameterizedTest(name = "should return the message: ''{1}'' on invalid creation data property {0}")
    @MethodSource("invalidCreationData")
    void shouldReturnBadRequestWhenCreateUserBodyIsInvalid(String fieldName, String errorMessage, CreatePatientRequest createPatientRequest) throws Exception {
        mockMvc.perform(
                post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(createPatientRequest))

                )
                .andExpect(status().isUnprocessableContent())
                .andExpectAll(
                        jsonPath("$.code").value("VALIDATION_ERROR"),
                        jsonPath("$.message").value("Incorrect input provided"),
                        jsonPath("$.validation_errors[0].field").value(fieldName),
                        jsonPath("$.validation_errors[0].value").value(errorMessage)
                );
    }
}