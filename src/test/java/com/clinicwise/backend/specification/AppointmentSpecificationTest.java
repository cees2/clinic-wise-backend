package com.clinicwise.backend.specification;

import com.clinicwise.backend.common.list.filter.AppointmentsFilter;
import com.clinicwise.backend.common.list.filter.ParsedFilter;
import com.clinicwise.backend.config.MySqlTestContainersConfig;
import com.clinicwise.backend.entity.Appointment;
import com.clinicwise.backend.entity.Employee;
import com.clinicwise.backend.entity.Patient;
import com.clinicwise.backend.entity.User;
import com.clinicwise.backend.enums.AppointmentStatus;
import com.clinicwise.backend.repository.AppointmentRepository;
import com.clinicwise.backend.testdata.AppointmentBuilder;
import com.clinicwise.backend.testdata.EmployeeBuilder;
import com.clinicwise.backend.testdata.PatientBuilder;
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
class AppointmentSpecificationTest {
    @Autowired
    AppointmentRepository appointmentRepository;
    @Autowired
    TestEntityManager testEntityManager;

    @Test
    @DisplayName("Should return appointment with 'Damian' patient on search 'damian'")
    void searchByPatientFirstName() {
        User patientUserToBeFound = UserBuilder.aUser().withFirstname("Damian").withLastname("Mirek").build();
        User patientUserNotToBeFound = UserBuilder.aUser().withFirstname("Matt").withLastname("Johnson").build();
        User employeeUser = UserBuilder.aUser().withFirstname("Adam").withLastname("Smith").build();
        Employee employee = testEntityManager.persist(EmployeeBuilder.anEmployee().withUser(employeeUser).build());
        Patient patientToBeFound = testEntityManager.persist(PatientBuilder.aPatient().withUser(patientUserToBeFound).build());
        Patient patientNotToBeFound = testEntityManager.persist(PatientBuilder.aPatient().withUser(patientUserNotToBeFound).build());

        testEntityManager.persist(AppointmentBuilder.anAppointment().withEmployee(employee).withPatient(patientToBeFound).build());
        testEntityManager.persist(AppointmentBuilder.anAppointment().withEmployee(employee).withPatient(patientNotToBeFound).build());
        testEntityManager.flush();
        testEntityManager.clear();

        AppointmentsFilter appointmentsFilter = new AppointmentsFilter(20, 0, "", "damian", null, null, null, null);
        List<Appointment> appointments = appointmentRepository.findAll(AppointmentSpecification.whereFilter(appointmentsFilter));

        assertEquals(1, appointments.size());
        assertEquals(patientToBeFound.getId(), appointments.getFirst().getPatient().getId());
    }

    @Test
    @DisplayName("Should return appointment 'Damian' employee on search 'damian'")
    void returnAppointmentWithSearchEmployee(){
        User userToBeFound = UserBuilder.aUser().withFirstname("Damian").withLastname("Mirek").build();
        User userNotToBeFound = UserBuilder.aUser().withFirstname("James").withLastname("Bond").build();
        User patientUser = UserBuilder.aUser().withFirstname("Matt").withLastname("Brown").build();
        Employee employeeToBeFound = testEntityManager.persist(EmployeeBuilder.anEmployee().withUser(userToBeFound).build());
        Employee employeeNotToBeFound = testEntityManager.persist(EmployeeBuilder.anEmployee().withUser(userNotToBeFound).build());
        Patient patient = testEntityManager.persist(PatientBuilder.aPatient().withUser(patientUser).build());
        Appointment appointmentToBeReturned1 = testEntityManager.persist(AppointmentBuilder.anAppointment().withEmployee(employeeToBeFound).withPatient(patient).build());
        Appointment appointmentToBeReturned2 = testEntityManager.persist(AppointmentBuilder.anAppointment().withEmployee(employeeToBeFound).withPatient(patient).build());
        testEntityManager.persist(AppointmentBuilder.anAppointment().withEmployee(employeeNotToBeFound).withPatient(patient).build());
        testEntityManager.flush();
        testEntityManager.clear();

        AppointmentsFilter appointmentsFilter = new AppointmentsFilter(20, 0, null, "damian", null, null, null, null);
        List<Appointment> appointments = appointmentRepository.findAll(AppointmentSpecification.whereFilter(appointmentsFilter));
        List<Integer> appointmentIds = appointments.stream().map(Appointment::getId).toList();

        assertEquals(2, appointments.size());
        assertTrue(appointmentIds.containsAll(List.of(appointmentToBeReturned1.getId(), appointmentToBeReturned2.getId())));
    }

    @Test
    @DisplayName("Should return 2 out of 3 appointments on additional note search presence")
    void additionalNoteSearch(){
        User patientUser = UserBuilder.aUser().build();
        User employeeUser = UserBuilder.aUser().build();
        Employee employee = testEntityManager.persist(EmployeeBuilder.anEmployee().withUser(employeeUser).build());
        Patient patient = testEntityManager.persist(PatientBuilder.aPatient().withUser(patientUser).build());
        Appointment appointmentWithMatchingSearchedAdditionalNote0 = testEntityManager.persist(AppointmentBuilder.anAppointment().withPatient(patient).withEmployee(employee).withAdditionalNote("Additional Note").build());
        Appointment appointmentWithMatchingSearchedAdditionalNote1 = testEntityManager.persist(AppointmentBuilder.anAppointment().withPatient(patient).withEmployee(employee).withAdditionalNote("noTE additional").build());
        testEntityManager.persist(AppointmentBuilder.anAppointment().withPatient(patient).withEmployee(employee).withAdditionalNote("No match").build());
        testEntityManager.flush();
        testEntityManager.clear();

        AppointmentsFilter appointmentsFilter = new AppointmentsFilter(20, 0, null, "note", null, null, null, null);
        List<Appointment> appointments = appointmentRepository.findAll(AppointmentSpecification.whereFilter(appointmentsFilter));
        List<Integer> appointmentIds = appointments.stream().map(Appointment::getId).toList();

        assertEquals(2, appointments.size());
        assertTrue(appointmentIds.containsAll(List.of(appointmentWithMatchingSearchedAdditionalNote0.getId(), appointmentWithMatchingSearchedAdditionalNote1.getId())));
    }

    @Test
    @DisplayName("Should return 1 out of 3 appointments with the exact 25 min duration")
    void returnAppointmentWith25MinDuration(){
        Patient patient = testEntityManager.persist(PatientBuilder.aPatient().build());
        Employee employee = testEntityManager.persist(EmployeeBuilder.anEmployee().build());
        Appointment appointment20MinDuration = testEntityManager.persist(AppointmentBuilder.anAppointment().withPatient(patient).withEmployee(employee).withDuration(20).build());
        Appointment appointment25MinDuration = testEntityManager.persist(AppointmentBuilder.anAppointment().withPatient(patient).withEmployee(employee).withDuration(25).build());
        Appointment appointment30MinDuration = testEntityManager.persist(AppointmentBuilder.anAppointment().withPatient(patient).withEmployee(employee).withDuration(30).build());
        testEntityManager.flush();
        testEntityManager.clear();

        AppointmentsFilter appointmentsFilter = new AppointmentsFilter(20, 0, null, null, "e" + ParsedFilter.filterConditionValueSeparator + "25", null, null, null);
        List<Appointment> appointments = appointmentRepository.findAll(AppointmentSpecification.whereFilter(appointmentsFilter));

        assertEquals(1, appointments.size());
        assertEquals(appointment25MinDuration.getId(), appointments.getFirst().getId());
    }

    @Test
    @DisplayName("Should return appointments with durations: 20, 30, and not return with duration of 25")
    void returnAppointmentNEDuration25(){
        Patient patient = testEntityManager.persist(PatientBuilder.aPatient().build());
        Employee employee = testEntityManager.persist(EmployeeBuilder.anEmployee().build());
        Appointment appointment20MinDuration = testEntityManager.persist(AppointmentBuilder.anAppointment().withPatient(patient).withEmployee(employee).withDuration(20).build());
        Appointment appointment25MinDuration = testEntityManager.persist(AppointmentBuilder.anAppointment().withPatient(patient).withEmployee(employee).withDuration(25).build());
        Appointment appointment30MinDuration = testEntityManager.persist(AppointmentBuilder.anAppointment().withPatient(patient).withEmployee(employee).withDuration(30).build());
        testEntityManager.flush();
        testEntityManager.clear();

        AppointmentsFilter appointmentsFilter = new AppointmentsFilter(20, 0, null, null, "ne" + ParsedFilter.filterConditionValueSeparator + "25", null, null, null);
        List<Appointment> appointments = appointmentRepository.findAll(AppointmentSpecification.whereFilter(appointmentsFilter));
        List<Integer> appointmentIds = appointments.stream().map(Appointment::getId).toList();

        assertEquals(2, appointments.size());
        assertTrue(appointmentIds.containsAll(List.of(appointment20MinDuration.getId(), appointment30MinDuration.getId())));
    }

    @Test
    @DisplayName("Should return appointment with duration 20 for filter with value less than 25")
    void returnAppointmentLTDuration25(){
        Patient patient = testEntityManager.persist(PatientBuilder.aPatient().build());
        Employee employee = testEntityManager.persist(EmployeeBuilder.anEmployee().build());
        Appointment appointment20MinDuration = testEntityManager.persist(AppointmentBuilder.anAppointment().withPatient(patient).withEmployee(employee).withDuration(20).build());
        Appointment appointment25MinDuration = testEntityManager.persist(AppointmentBuilder.anAppointment().withPatient(patient).withEmployee(employee).withDuration(25).build());
        Appointment appointment30MinDuration = testEntityManager.persist(AppointmentBuilder.anAppointment().withPatient(patient).withEmployee(employee).withDuration(30).build());
        testEntityManager.flush();
        testEntityManager.clear();

        AppointmentsFilter appointmentsFilter = new AppointmentsFilter(20, 0, null, null, "lt" + ParsedFilter.filterConditionValueSeparator + "25", null, null, null);
        List<Appointment> appointments = appointmentRepository.findAll(AppointmentSpecification.whereFilter(appointmentsFilter));

        assertEquals(1, appointments.size());
        assertEquals(appointment20MinDuration.getId(), appointments.getFirst().getId());
    }

    @Test
    @DisplayName("Should return appointments with duration: 20, 25 for filter with value less than or equal to 25")
    void returnAppointmentLTEDuration25(){
        Patient patient = testEntityManager.persist(PatientBuilder.aPatient().build());
        Employee employee = testEntityManager.persist(EmployeeBuilder.anEmployee().build());
        Appointment appointment20MinDuration = testEntityManager.persist(AppointmentBuilder.anAppointment().withPatient(patient).withEmployee(employee).withDuration(20).build());
        Appointment appointment25MinDuration = testEntityManager.persist(AppointmentBuilder.anAppointment().withPatient(patient).withEmployee(employee).withDuration(25).build());
        Appointment appointment30MinDuration = testEntityManager.persist(AppointmentBuilder.anAppointment().withPatient(patient).withEmployee(employee).withDuration(30).build());
        testEntityManager.flush();
        testEntityManager.clear();

        AppointmentsFilter appointmentsFilter = new AppointmentsFilter(20, 0, null, null, "lte" + ParsedFilter.filterConditionValueSeparator + "25", null, null, null);
        List<Appointment> appointments = appointmentRepository.findAll(AppointmentSpecification.whereFilter(appointmentsFilter));
        List<Integer> appointmentIds = appointments.stream().map(Appointment::getId).toList();

        assertEquals(2, appointments.size());
        assertTrue(appointmentIds.containsAll(List.of(appointment20MinDuration.getId(), appointment25MinDuration.getId())));
    }

    @Test
    @DisplayName("Should return appointment with duration 30 for filter with value greater than 25")
    void returnAppointmentGTDuration25(){
        Patient patient = testEntityManager.persist(PatientBuilder.aPatient().build());
        Employee employee = testEntityManager.persist(EmployeeBuilder.anEmployee().build());
        Appointment appointment20MinDuration = testEntityManager.persist(AppointmentBuilder.anAppointment().withPatient(patient).withEmployee(employee).withDuration(20).build());
        Appointment appointment25MinDuration = testEntityManager.persist(AppointmentBuilder.anAppointment().withPatient(patient).withEmployee(employee).withDuration(25).build());
        Appointment appointment30MinDuration = testEntityManager.persist(AppointmentBuilder.anAppointment().withPatient(patient).withEmployee(employee).withDuration(30).build());
        testEntityManager.flush();
        testEntityManager.clear();

        AppointmentsFilter appointmentsFilter = new AppointmentsFilter(20, 0, null, null, "gt" + ParsedFilter.filterConditionValueSeparator + "25", null, null, null);
        List<Appointment> appointments = appointmentRepository.findAll(AppointmentSpecification.whereFilter(appointmentsFilter));

        assertEquals(1, appointments.size());
        assertEquals(appointment30MinDuration.getId(), appointments.getFirst().getId());
    }

    @Test
    @DisplayName("Should return appointment with duration 30 for filter with value greater than or equal to 25")
    void returnAppointmentGTEDuration30(){
        Patient patient = testEntityManager.persist(PatientBuilder.aPatient().build());
        Employee employee = testEntityManager.persist(EmployeeBuilder.anEmployee().build());
        Appointment appointment20MinDuration = testEntityManager.persist(AppointmentBuilder.anAppointment().withPatient(patient).withEmployee(employee).withDuration(20).build());
        Appointment appointment25MinDuration = testEntityManager.persist(AppointmentBuilder.anAppointment().withPatient(patient).withEmployee(employee).withDuration(25).build());
        Appointment appointment30MinDuration = testEntityManager.persist(AppointmentBuilder.anAppointment().withPatient(patient).withEmployee(employee).withDuration(30).build());
        testEntityManager.flush();
        testEntityManager.clear();

        AppointmentsFilter appointmentsFilter = new AppointmentsFilter(20, 0, null, null, "gte" + ParsedFilter.filterConditionValueSeparator + "25", null, null, null);
        List<Appointment> appointments = appointmentRepository.findAll(AppointmentSpecification.whereFilter(appointmentsFilter));
        List<Integer> appointmentIds = appointments.stream().map(Appointment::getId).toList();

        assertEquals(2, appointments.size());
        assertTrue(appointmentIds.containsAll(List.of(appointment25MinDuration.getId(), appointment30MinDuration.getId())));
    }

    @Test
    @DisplayName("Should return one appointment with the SCHEDULED status out of 3 appointments")
    void returnSCHEDULEDAppointment(){
        Patient patient = testEntityManager.persist(PatientBuilder.aPatient().build());
        Employee employee = testEntityManager.persist(EmployeeBuilder.anEmployee().build());
        Appointment appointmentSCHEDULEDStatus = testEntityManager.persist(AppointmentBuilder.anAppointment().withPatient(patient).withEmployee(employee).withAppointmentStatus(AppointmentStatus.SCHEDULED).build());
        Appointment appointmentCANCELLEDStatus = testEntityManager.persist(AppointmentBuilder.anAppointment().withPatient(patient).withEmployee(employee).withAppointmentStatus(AppointmentStatus.CANCELLED).build());
        Appointment appointmentCOMPLETEDStatus = testEntityManager.persist(AppointmentBuilder.anAppointment().withPatient(patient).withEmployee(employee).withAppointmentStatus(AppointmentStatus.COMPLETED).build());
        testEntityManager.flush();
        testEntityManager.clear();

        AppointmentsFilter appointmentsFilter = new AppointmentsFilter(20, 0, null, null, null, "e" + ParsedFilter.filterConditionValueSeparator + "SCHEDULED", null, null);
        List<Appointment> appointments = appointmentRepository.findAll(AppointmentSpecification.whereFilter(appointmentsFilter));

        assertEquals(1, appointments.size());
        assertEquals(appointmentSCHEDULEDStatus.getId(), appointments.getFirst().getId());
    }

    @Test
    @DisplayName("Should return one appointment with the CANCELLED status out of 3 appointments")
    void returnCANCELLEDAppointment(){
        Patient patient = testEntityManager.persist(PatientBuilder.aPatient().build());
        Employee employee = testEntityManager.persist(EmployeeBuilder.anEmployee().build());
        Appointment appointmentSCHEDULEDStatus = testEntityManager.persist(AppointmentBuilder.anAppointment().withPatient(patient).withEmployee(employee).withAppointmentStatus(AppointmentStatus.SCHEDULED).build());
        Appointment appointmentCANCELLEDStatus = testEntityManager.persist(AppointmentBuilder.anAppointment().withPatient(patient).withEmployee(employee).withAppointmentStatus(AppointmentStatus.CANCELLED).build());
        Appointment appointmentCOMPLETEDStatus = testEntityManager.persist(AppointmentBuilder.anAppointment().withPatient(patient).withEmployee(employee).withAppointmentStatus(AppointmentStatus.COMPLETED).build());
        testEntityManager.flush();
        testEntityManager.clear();

        AppointmentsFilter appointmentsFilter = new AppointmentsFilter(20, 0, null, null, null, "e" + ParsedFilter.filterConditionValueSeparator + "CANCELLED", null, null);
        List<Appointment> appointments = appointmentRepository.findAll(AppointmentSpecification.whereFilter(appointmentsFilter));

        assertEquals(1, appointments.size());
        assertEquals(appointmentCANCELLEDStatus.getId(), appointments.getFirst().getId());
    }

    @Test
    @DisplayName("Should return one appointment with the COMPLETED status out of 3 appointments")
    void returnCOMPLETEDAppointment(){
        Patient patient = testEntityManager.persist(PatientBuilder.aPatient().build());
        Employee employee = testEntityManager.persist(EmployeeBuilder.anEmployee().build());
        Appointment appointmentSCHEDULEDStatus = testEntityManager.persist(AppointmentBuilder.anAppointment().withPatient(patient).withEmployee(employee).withAppointmentStatus(AppointmentStatus.SCHEDULED).build());
        Appointment appointmentCANCELLEDStatus = testEntityManager.persist(AppointmentBuilder.anAppointment().withPatient(patient).withEmployee(employee).withAppointmentStatus(AppointmentStatus.CANCELLED).build());
        Appointment appointmentCOMPLETEDStatus = testEntityManager.persist(AppointmentBuilder.anAppointment().withPatient(patient).withEmployee(employee).withAppointmentStatus(AppointmentStatus.COMPLETED).build());
        testEntityManager.flush();
        testEntityManager.clear();

        AppointmentsFilter appointmentsFilter = new AppointmentsFilter(20, 0, null, null, null, "e" + ParsedFilter.filterConditionValueSeparator + "COMPLETED", null, null);
        List<Appointment> appointments = appointmentRepository.findAll(AppointmentSpecification.whereFilter(appointmentsFilter));

        assertEquals(1, appointments.size());
        assertEquals(appointmentCOMPLETEDStatus.getId(), appointments.getFirst().getId());
    }

    @Test
    @DisplayName("Should return two appointments with statuses: SCHEDULED, CANCELLED out of 3 appointments")
    void returnSCHEDULEDAndCANCELLEDAppointment(){
        Patient patient = testEntityManager.persist(PatientBuilder.aPatient().build());
        Employee employee = testEntityManager.persist(EmployeeBuilder.anEmployee().build());
        Appointment appointmentSCHEDULEDStatus = testEntityManager.persist(AppointmentBuilder.anAppointment().withPatient(patient).withEmployee(employee).withAppointmentStatus(AppointmentStatus.SCHEDULED).build());
        Appointment appointmentCANCELLEDStatus = testEntityManager.persist(AppointmentBuilder.anAppointment().withPatient(patient).withEmployee(employee).withAppointmentStatus(AppointmentStatus.CANCELLED).build());
        Appointment appointmentCOMPLETEDStatus = testEntityManager.persist(AppointmentBuilder.anAppointment().withPatient(patient).withEmployee(employee).withAppointmentStatus(AppointmentStatus.COMPLETED).build());
        testEntityManager.flush();
        testEntityManager.clear();

        AppointmentsFilter appointmentsFilter = new AppointmentsFilter(20, 0, null, null, null, "e" + ParsedFilter.filterConditionValueSeparator + "SCHEDULED" + ParsedFilter.filterValueSeparator + "CANCELLED", null, null);
        List<Appointment> appointments = appointmentRepository.findAll(AppointmentSpecification.whereFilter(appointmentsFilter));
        List<Integer> appointmentIds = appointments.stream().map(Appointment::getId).toList();

        assertEquals(2, appointments.size());
        assertTrue(appointmentIds.containsAll(List.of(appointmentSCHEDULEDStatus.getId(), appointmentCANCELLEDStatus.getId())));
    }

    @Test
    @DisplayName("Should return two appointments with statuses: SCHEDULED, COMPLETED out of 3 appointments")
    void returnSCHEDULEDAndCOMPLETEDAppointment(){
        Patient patient = testEntityManager.persist(PatientBuilder.aPatient().build());
        Employee employee = testEntityManager.persist(EmployeeBuilder.anEmployee().build());
        Appointment appointmentSCHEDULEDStatus = testEntityManager.persist(AppointmentBuilder.anAppointment().withPatient(patient).withEmployee(employee).withAppointmentStatus(AppointmentStatus.SCHEDULED).build());
        Appointment appointmentCANCELLEDStatus = testEntityManager.persist(AppointmentBuilder.anAppointment().withPatient(patient).withEmployee(employee).withAppointmentStatus(AppointmentStatus.CANCELLED).build());
        Appointment appointmentCOMPLETEDStatus = testEntityManager.persist(AppointmentBuilder.anAppointment().withPatient(patient).withEmployee(employee).withAppointmentStatus(AppointmentStatus.COMPLETED).build());
        testEntityManager.flush();
        testEntityManager.clear();

        AppointmentsFilter appointmentsFilter = new AppointmentsFilter(20, 0, null, null, null, "e" + ParsedFilter.filterConditionValueSeparator + "SCHEDULED" + ParsedFilter.filterValueSeparator + "COMPLETED", null, null);
        List<Appointment> appointments = appointmentRepository.findAll(AppointmentSpecification.whereFilter(appointmentsFilter));
        List<Integer> appointmentIds = appointments.stream().map(Appointment::getId).toList();

        assertEquals(2, appointments.size());
        assertTrue(appointmentIds.containsAll(List.of(appointmentSCHEDULEDStatus.getId(), appointmentCOMPLETEDStatus.getId())));
    }

    @Test
    @DisplayName("Should return two appointments with statuses: CANCELLED, COMPLETED out of 3 appointments")
    void returnCANCELLEDAndCOMPLETEDAppointment(){
        Patient patient = testEntityManager.persist(PatientBuilder.aPatient().build());
        Employee employee = testEntityManager.persist(EmployeeBuilder.anEmployee().build());
        Appointment appointmentSCHEDULEDStatus = testEntityManager.persist(AppointmentBuilder.anAppointment().withPatient(patient).withEmployee(employee).withAppointmentStatus(AppointmentStatus.SCHEDULED).build());
        Appointment appointmentCANCELLEDStatus = testEntityManager.persist(AppointmentBuilder.anAppointment().withPatient(patient).withEmployee(employee).withAppointmentStatus(AppointmentStatus.CANCELLED).build());
        Appointment appointmentCOMPLETEDStatus = testEntityManager.persist(AppointmentBuilder.anAppointment().withPatient(patient).withEmployee(employee).withAppointmentStatus(AppointmentStatus.COMPLETED).build());
        testEntityManager.flush();
        testEntityManager.clear();

        AppointmentsFilter appointmentsFilter = new AppointmentsFilter(20, 0, null, null, null, "e" + ParsedFilter.filterConditionValueSeparator + "CANCELLED" + ParsedFilter.filterValueSeparator + "COMPLETED", null, null);
        List<Appointment> appointments = appointmentRepository.findAll(AppointmentSpecification.whereFilter(appointmentsFilter));
        List<Integer> appointmentIds = appointments.stream().map(Appointment::getId).toList();

        assertEquals(2, appointments.size());
        assertTrue(appointmentIds.containsAll(List.of(appointmentCANCELLEDStatus.getId(), appointmentCOMPLETEDStatus.getId())));
    }

    @Test
    @DisplayName("Should return all of the three appointments when all of the three statuses are present in the filter")
    void returnAllStatusesAppointments(){
        Patient patient = testEntityManager.persist(PatientBuilder.aPatient().build());
        Employee employee = testEntityManager.persist(EmployeeBuilder.anEmployee().build());
        Appointment appointmentSCHEDULEDStatus = testEntityManager.persist(AppointmentBuilder.anAppointment().withPatient(patient).withEmployee(employee).withAppointmentStatus(AppointmentStatus.SCHEDULED).build());
        Appointment appointmentCANCELLEDStatus = testEntityManager.persist(AppointmentBuilder.anAppointment().withPatient(patient).withEmployee(employee).withAppointmentStatus(AppointmentStatus.CANCELLED).build());
        Appointment appointmentCOMPLETEDStatus = testEntityManager.persist(AppointmentBuilder.anAppointment().withPatient(patient).withEmployee(employee).withAppointmentStatus(AppointmentStatus.COMPLETED).build());
        testEntityManager.flush();
        testEntityManager.clear();

        AppointmentsFilter appointmentsFilter = new AppointmentsFilter(20, 0, null, null, null, "e" + ParsedFilter.filterConditionValueSeparator + "CANCELLED" + ParsedFilter.filterValueSeparator + "COMPLETED" + ParsedFilter.filterValueSeparator + "SCHEDULED", null, null);
        List<Appointment> appointments = appointmentRepository.findAll(AppointmentSpecification.whereFilter(appointmentsFilter));
        List<Integer> appointmentIds = appointments.stream().map(Appointment::getId).toList();

        assertEquals(3, appointments.size());
        assertTrue(appointmentIds.containsAll(List.of(appointmentCANCELLEDStatus.getId(), appointmentCOMPLETEDStatus.getId(), appointmentSCHEDULEDStatus.getId())));
    }

    @Test
    @DisplayName("Should return one of three appointments with exact additional note match")
    void returnAdditionalNoteMatch(){
        Patient patient = testEntityManager.persist(PatientBuilder.aPatient().build());
        Employee employee = testEntityManager.persist(EmployeeBuilder.anEmployee().build());
        Appointment matchingNoteAppointment = testEntityManager.persist(AppointmentBuilder.anAppointment().withPatient(patient).withEmployee(employee).withAdditionalNote("Exact note").build());
        testEntityManager.persist(AppointmentBuilder.anAppointment().withPatient(patient).withEmployee(employee).withAdditionalNote("Other note").build());
        testEntityManager.persist(AppointmentBuilder.anAppointment().withPatient(patient).withEmployee(employee).withAdditionalNote("").build());
        testEntityManager.flush();
        testEntityManager.clear();

        AppointmentsFilter appointmentsFilter = new AppointmentsFilter(20, 0, null, null, null, null, "e" + ParsedFilter.filterConditionValueSeparator + "Exact note", null);
        List<Appointment> appointments = appointmentRepository.findAll(AppointmentSpecification.whereFilter(appointmentsFilter));

        assertEquals(1, appointments.size());
        assertEquals(matchingNoteAppointment.getId(), appointments.getFirst().getId());
    }

    @Test
    @DisplayName("Should return two of three appointments with like-searched additional note matches")
    void returnAdditionalNoteLikeMatch(){
        Patient patient = testEntityManager.persist(PatientBuilder.aPatient().build());
        Employee employee = testEntityManager.persist(EmployeeBuilder.anEmployee().build());
        Appointment matchingNoteAppointment0 = testEntityManager.persist(AppointmentBuilder.anAppointment().withPatient(patient).withEmployee(employee).withAdditionalNote("a Note#1").build());
        Appointment matchingNoteAppointment1 = testEntityManager.persist(AppointmentBuilder.anAppointment().withPatient(patient).withEmployee(employee).withAdditionalNote("a Note#2").build());
        testEntityManager.persist(AppointmentBuilder.anAppointment().withPatient(patient).withEmployee(employee).withAdditionalNote("").build());
        testEntityManager.flush();
        testEntityManager.clear();

        AppointmentsFilter appointmentsFilter = new AppointmentsFilter(20, 0, null, null, null, null, "c" + ParsedFilter.filterConditionValueSeparator + "note#", null);
        List<Appointment> appointments = appointmentRepository.findAll(AppointmentSpecification.whereFilter(appointmentsFilter));
        List<Integer> appointmentIds = appointments.stream().map(Appointment::getId).toList();

        assertEquals(2, appointments.size());
        assertTrue(appointmentIds.containsAll(List.of(matchingNoteAppointment0.getId(), matchingNoteAppointment1.getId())));
    }

    @Test
    @DisplayName("Should return one of three appointments with start date greater than '2026-10-05'")
    void returnStartDateGT(){
        Patient patient = testEntityManager.persist(PatientBuilder.aPatient().build());
        Employee employee = testEntityManager.persist(EmployeeBuilder.anEmployee().build());
        Appointment matchingStartDateAppointment = testEntityManager.persist(AppointmentBuilder.anAppointment().withPatient(patient).withEmployee(employee).withStartDate(LocalDateTime.of(2026, 10, 6, 0,0,0,0)).build());
        testEntityManager.persist(AppointmentBuilder.anAppointment().withPatient(patient).withEmployee(employee).withStartDate(LocalDateTime.of(2026, 10, 2, 0,0,0,0)).build());
        testEntityManager.persist(AppointmentBuilder.anAppointment().withPatient(patient).withEmployee(employee).withStartDate(LocalDateTime.of(2026, 10, 1, 0,0,0,0)).build());
        testEntityManager.flush();
        testEntityManager.clear();

        AppointmentsFilter appointmentsFilter = new AppointmentsFilter(20, 0, null, null, null, null, null, "gte" + ParsedFilter.filterConditionValueSeparator + "2026-10-05");
        List<Appointment> appointments = appointmentRepository.findAll(AppointmentSpecification.whereFilter(appointmentsFilter));

        assertEquals(1, appointments.size());
        assertEquals(matchingStartDateAppointment.getId(), appointments.getFirst().getId());
    }

    @Test
    @DisplayName("Should return two of three appointments with start date less than '2026-10-05'")
    void returnStartDateLT(){
        Patient patient = testEntityManager.persist(PatientBuilder.aPatient().build());
        Employee employee = testEntityManager.persist(EmployeeBuilder.anEmployee().build());
        testEntityManager.persist(AppointmentBuilder.anAppointment().withPatient(patient).withEmployee(employee).withStartDate(LocalDateTime.of(2026, 10, 6, 0,0,0,0)).build());
        Appointment matchingStartDateAppointment0 = testEntityManager.persist(AppointmentBuilder.anAppointment().withPatient(patient).withEmployee(employee).withStartDate(LocalDateTime.of(2026, 10, 2, 0,0,0,0)).build());
        Appointment matchingStartDateAppointment1 =testEntityManager.persist(AppointmentBuilder.anAppointment().withPatient(patient).withEmployee(employee).withStartDate(LocalDateTime.of(2026, 10, 1, 0,0,0,0)).build());
        testEntityManager.flush();
        testEntityManager.clear();

        AppointmentsFilter appointmentsFilter = new AppointmentsFilter(20, 0, null, null, null, null, null, "lte" + ParsedFilter.filterConditionValueSeparator + "2026-10-05");
        List<Appointment> appointments = appointmentRepository.findAll(AppointmentSpecification.whereFilter(appointmentsFilter));
        List<Integer> appointmentIds = appointments.stream().map(Appointment::getId).toList();

        assertEquals(2, appointments.size());
        assertTrue(appointmentIds.containsAll(List.of(matchingStartDateAppointment0.getId(), matchingStartDateAppointment1.getId())));
    }
}