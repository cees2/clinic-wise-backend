package com.clinicwise.backend.specification;

import com.clinicwise.backend.common.list.filter.ParsedFilter;
import com.clinicwise.backend.common.list.filter.PatientsFilter;
import com.clinicwise.backend.config.MySqlTestContainersConfig;
import com.clinicwise.backend.entity.Patient;
import com.clinicwise.backend.enums.Country;
import com.clinicwise.backend.enums.Gender;
import com.clinicwise.backend.repository.PatientRepository;
import com.clinicwise.backend.testdata.PatientBuilder;
import com.clinicwise.backend.testdata.UserBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(MySqlTestContainersConfig.class)
public class PatientSpecificationTest {
    @Autowired
    PatientRepository patientRepository;
    @Autowired
    TestEntityManager testEntityManager;

    @Test
    @DisplayName("Should search for patients by its document ID")
    void returnPatientByDocumentIdSearch(){
        Patient matchingPatient1 = testEntityManager.persist(PatientBuilder.aPatient().withUser(UserBuilder.aUser().withDocumentID("AA 001").build()).build());
        Patient matchingPatient2 = testEntityManager.persist(PatientBuilder.aPatient().withUser(UserBuilder.aUser().withDocumentID("AA 002").build()).build());
        Patient nonMatchingPatient = testEntityManager.persist(PatientBuilder.aPatient().withUser(UserBuilder.aUser().withDocumentID("BB 111").build()).build());
        testEntityManager.flush();
        testEntityManager.clear();

        PatientsFilter patientsFilter = new PatientsFilter(20, 0, null, "aa", null, null, null, null, null);

        List<Patient> patients = patientRepository.findAll(PatientSpecification.whereFilter(patientsFilter));
        List<Integer> patientIds = patients.stream().map(Patient::getId).toList();

        assertEquals(2, patientIds.size());
        assertTrue(patientIds.containsAll(List.of(matchingPatient1.getId(), matchingPatient2.getId())));
    }

    @Test
    @DisplayName("Should search for patients by its full name")
    void returnPatientByFullNameSearch(){
        Patient matchingPatient1 = testEntityManager.persist(PatientBuilder.aPatient().withUser(UserBuilder.aUser().withFirstname("Damian").withLastname("Mirek").build()).build());
        Patient matchingPatient2 = testEntityManager.persist(PatientBuilder.aPatient().withUser(UserBuilder.aUser().withFirstname("damian").withLastname("mirek").build()).build());
        Patient nonMatchingPatient = testEntityManager.persist(PatientBuilder.aPatient().withUser(UserBuilder.aUser().withFirstname("John").withLastname("Smith").build()).build());
        testEntityManager.flush();
        testEntityManager.clear();

        PatientsFilter patientsFilter = new PatientsFilter(20, 0, null, "damian m", null, null, null, null, null);

        List<Patient> patients = patientRepository.findAll(PatientSpecification.whereFilter(patientsFilter));
        List<Integer> patientIds = patients.stream().map(Patient::getId).toList();

        assertEquals(2, patientIds.size());
        assertTrue(patientIds.containsAll(List.of(matchingPatient1.getId(), matchingPatient2.getId())));
    }

    @Test
    @DisplayName("Should return patients with matching name(contains)")
    void returnPatientByMatchingName(){
        Patient matchingPatient1 = testEntityManager.persist(PatientBuilder.aPatient().withUser(UserBuilder.aUser().withFirstname("Damian").withLastname("Mirek").build()).build());
        Patient matchingPatient2 = testEntityManager.persist(PatientBuilder.aPatient().withUser(UserBuilder.aUser().withFirstname("damian").withLastname("mirek").build()).build());
        Patient nonMatchingPatient = testEntityManager.persist(PatientBuilder.aPatient().withUser(UserBuilder.aUser().withFirstname("John").withLastname("Smith").build()).build());
        testEntityManager.flush();
        testEntityManager.clear();

        PatientsFilter patientsFilter = new PatientsFilter(20, 0, null, null, "c" + ParsedFilter.filterConditionValueSeparator + "dami", null, null, null, null);

        List<Patient> patients = patientRepository.findAll(PatientSpecification.whereFilter(patientsFilter));
        List<Integer> patientIds = patients.stream().map(Patient::getId).toList();

        assertEquals(2, patientIds.size());
        assertTrue(patientIds.containsAll(List.of(matchingPatient1.getId(), matchingPatient2.getId())));
    }

    @Test
    @DisplayName("Should return patients with matching name(equals)")
    void returnPatientByMatchingNameEquals(){
        Patient matchingPatient1 = testEntityManager.persist(PatientBuilder.aPatient().withUser(UserBuilder.aUser().withFirstname("Damian").withLastname("Mirek").build()).build());
        Patient matchingPatient2 = testEntityManager.persist(PatientBuilder.aPatient().withUser(UserBuilder.aUser().withFirstname("Damian").withLastname("mirek").build()).build());
        Patient nonMatchingPatient1 = testEntityManager.persist(PatientBuilder.aPatient().withUser(UserBuilder.aUser().withFirstname("Damian12").withLastname("mirek21").build()).build());
        Patient nonMatchingPatient2 = testEntityManager.persist(PatientBuilder.aPatient().withUser(UserBuilder.aUser().withFirstname("John").withLastname("Smith").build()).build());
        testEntityManager.flush();
        testEntityManager.clear();

        PatientsFilter patientsFilter = new PatientsFilter(20, 0, null, null, "e" + ParsedFilter.filterConditionValueSeparator + "damian", null, null, null, null);

        List<Patient> patients = patientRepository.findAll(PatientSpecification.whereFilter(patientsFilter));
        List<Integer> patientIds = patients.stream().map(Patient::getId).toList();

        assertEquals(2, patientIds.size());
        assertTrue(patientIds.containsAll(List.of(matchingPatient1.getId(), matchingPatient2.getId())));
    }

    @Test
    @DisplayName("Should return patients with matching surname(equals)")
    void returnPatientByMatchingSurnameEquals(){
        Patient matchingPatient1 = testEntityManager.persist(PatientBuilder.aPatient().withUser(UserBuilder.aUser().withFirstname("Damian").withLastname("Mirek").build()).build());
        Patient matchingPatient2 = testEntityManager.persist(PatientBuilder.aPatient().withUser(UserBuilder.aUser().withFirstname("damian").withLastname("Mirek").build()).build());
        Patient nonMatchingPatient1 = testEntityManager.persist(PatientBuilder.aPatient().withUser(UserBuilder.aUser().withFirstname("damian").withLastname("Mirek23").build()).build());
        Patient nonMatchingPatient2 = testEntityManager.persist(PatientBuilder.aPatient().withUser(UserBuilder.aUser().withFirstname("John").withLastname("Smith").build()).build());
        testEntityManager.flush();
        testEntityManager.clear();

        PatientsFilter patientsFilter = new PatientsFilter(20, 0, null, null, null, "e" + ParsedFilter.filterConditionValueSeparator + "Mirek", null, null, null);

        List<Patient> patients = patientRepository.findAll(PatientSpecification.whereFilter(patientsFilter));
        List<Integer> patientIds = patients.stream().map(Patient::getId).toList();

        assertEquals(2, patientIds.size());
        assertTrue(patientIds.containsAll(List.of(matchingPatient1.getId(), matchingPatient2.getId())));
    }

    @Test
    @DisplayName("Should return patients with matching surname(contains)")
    void returnPatientByMatchingSurnameContains(){
        Patient matchingPatient1 = testEntityManager.persist(PatientBuilder.aPatient().withUser(UserBuilder.aUser().withFirstname("Damian").withLastname("Mirek").build()).build());
        Patient matchingPatient2 = testEntityManager.persist(PatientBuilder.aPatient().withUser(UserBuilder.aUser().withFirstname("Damian").withLastname("mirek").build()).build());
        Patient nonMatchingPatient = testEntityManager.persist(PatientBuilder.aPatient().withUser(UserBuilder.aUser().withFirstname("John").withLastname("Smith").build()).build());
        testEntityManager.flush();
        testEntityManager.clear();

        PatientsFilter patientsFilter = new PatientsFilter(20, 0, null, null, null, "c" + ParsedFilter.filterConditionValueSeparator + "mire", null, null, null);

        List<Patient> patients = patientRepository.findAll(PatientSpecification.whereFilter(patientsFilter));
        List<Integer> patientIds = patients.stream().map(Patient::getId).toList();

        assertEquals(2, patientIds.size());
        assertTrue(patientIds.containsAll(List.of(matchingPatient1.getId(), matchingPatient2.getId())));
    }

    @Test
    @DisplayName("Should return patients with matching date of birth(less than)")
    void returnPatientByMatchingDateOfBirthLessThan(){
        Patient matchingPatient1 = testEntityManager.persist(PatientBuilder.aPatient().withUser(UserBuilder.aUser().withDateOfBirth(LocalDate.of(1999, 1, 1)).build()).build());
        Patient matchingPatient2 = testEntityManager.persist(PatientBuilder.aPatient().withUser(UserBuilder.aUser().withDateOfBirth(LocalDate.of(1999, 1, 1)).build()).build());
        Patient nonMatchingPatient = testEntityManager.persist(PatientBuilder.aPatient().withUser(UserBuilder.aUser().withDateOfBirth(LocalDate.of(2000, 1, 1)).build()).build());
        testEntityManager.flush();
        testEntityManager.clear();

        PatientsFilter patientsFilter = new PatientsFilter(20, 0, null, null, null, null, "lte" + ParsedFilter.filterConditionValueSeparator + "1999-12-01", null, null);

        List<Patient> patients = patientRepository.findAll(PatientSpecification.whereFilter(patientsFilter));
        List<Integer> patientIds = patients.stream().map(Patient::getId).toList();

        assertEquals(2, patientIds.size());
        assertTrue(patientIds.containsAll(List.of(matchingPatient1.getId(), matchingPatient2.getId())));
    }

    @Test
    @DisplayName("Should return patients with matching date of birth(greater than)")
    void returnPatientByMatchingDateOfBirthGreaterThan(){
        Patient matchingPatient1 = testEntityManager.persist(PatientBuilder.aPatient().withUser(UserBuilder.aUser().withDateOfBirth(LocalDate.of(2000, 1, 1)).build()).build());
        Patient matchingPatient2 = testEntityManager.persist(PatientBuilder.aPatient().withUser(UserBuilder.aUser().withDateOfBirth(LocalDate.of(2000, 1, 1)).build()).build());
        Patient nonMatchingPatient = testEntityManager.persist(PatientBuilder.aPatient().withUser(UserBuilder.aUser().withDateOfBirth(LocalDate.of(1999, 1, 1)).build()).build());
        testEntityManager.flush();
        testEntityManager.clear();

        PatientsFilter patientsFilter = new PatientsFilter(20, 0, null, null, null, null, "gte" + ParsedFilter.filterConditionValueSeparator + "1999-10-12", null, null);

        List<Patient> patients = patientRepository.findAll(PatientSpecification.whereFilter(patientsFilter));
        List<Integer> patientIds = patients.stream().map(Patient::getId).toList();

        assertEquals(2, patientIds.size());
        assertTrue(patientIds.containsAll(List.of(matchingPatient1.getId(), matchingPatient2.getId())));
    }

    @Test
    @DisplayName("Should return patients with matching gender(male)")
    void returnMalePatients(){
        Patient matchingPatient1 = testEntityManager.persist(PatientBuilder.aPatient().withUser(UserBuilder.aUser().withGender(Gender.MALE).build()).build());
        Patient matchingPatient2 = testEntityManager.persist(PatientBuilder.aPatient().withUser(UserBuilder.aUser().withGender(Gender.MALE).build()).build());
        Patient nonMatchingPatient = testEntityManager.persist(PatientBuilder.aPatient().withUser(UserBuilder.aUser().withGender(Gender.FEMALE).build()).build());
        testEntityManager.flush();
        testEntityManager.clear();

        PatientsFilter patientsFilter = new PatientsFilter(20, 0, null, null, null, null, null, "e" + ParsedFilter.filterConditionValueSeparator + "male", null);
        List<Patient> patients = patientRepository.findAll(PatientSpecification.whereFilter(patientsFilter));
        List<Integer> patientIds = patients.stream().map(Patient::getId).toList();

        assertEquals(2, patientIds.size());
        assertTrue(patientIds.containsAll(List.of(matchingPatient1.getId(), matchingPatient2.getId())));
    }

    @Test
    @DisplayName("Should return patients with matching gender(female)")
    void returnFemalePatients(){
        Patient nonMatching1 = testEntityManager.persist(PatientBuilder.aPatient().withUser(UserBuilder.aUser().withGender(Gender.MALE).build()).build());
        Patient nonMatching2 = testEntityManager.persist(PatientBuilder.aPatient().withUser(UserBuilder.aUser().withGender(Gender.MALE).build()).build());
        Patient matchingPatient = testEntityManager.persist(PatientBuilder.aPatient().withUser(UserBuilder.aUser().withGender(Gender.FEMALE).build()).build());
        testEntityManager.flush();
        testEntityManager.clear();

        PatientsFilter patientsFilter = new PatientsFilter(20, 0, null, null, null, null, null, "e" + ParsedFilter.filterConditionValueSeparator + "female", null);
        List<Patient> patients = patientRepository.findAll(PatientSpecification.whereFilter(patientsFilter));

        assertEquals(1, patients.size());
        assertEquals(matchingPatient.getId(), patients.getFirst().getId());
    }

    @Test
    @DisplayName("Should return patients with matching nationality(Poland)")
    void returnPolishPatients(){
        Patient polishPatient1 = testEntityManager.persist(PatientBuilder.aPatient().withUser(UserBuilder.aUser().withNationality(Country.POLAND).build()).build());
        Patient polishPatient2 = testEntityManager.persist(PatientBuilder.aPatient().withUser(UserBuilder.aUser().withNationality(Country.POLAND).build()).build());
        Patient frenchPatient = testEntityManager.persist(PatientBuilder.aPatient().withUser(UserBuilder.aUser().withNationality(Country.FRANCE).build()).build());
        testEntityManager.flush();
        testEntityManager.clear();

        PatientsFilter patientsFilter = new PatientsFilter(20, 0, null, null, null, null, null, null, "e" + ParsedFilter.filterConditionValueSeparator + "Poland");
        List<Patient> patients = patientRepository.findAll(PatientSpecification.whereFilter(patientsFilter));
        List<Integer> patientIds = patients.stream().map(Patient::getId).toList();

        assertEquals(2, patientIds.size());
        assertTrue(patientIds.containsAll(List.of(polishPatient1.getId(), polishPatient2.getId())));
    }

    @Test
    @DisplayName("Should return patients with matching nationalities(Poland, Germany)")
    void returnPolishAndGermanPatients(){
        Patient polishPatient = testEntityManager.persist(PatientBuilder.aPatient().withUser(UserBuilder.aUser().withNationality(Country.POLAND).build()).build());
        Patient germanPatient = testEntityManager.persist(PatientBuilder.aPatient().withUser(UserBuilder.aUser().withNationality(Country.GERMANY).build()).build());
        Patient frenchPatient = testEntityManager.persist(PatientBuilder.aPatient().withUser(UserBuilder.aUser().withNationality(Country.FRANCE).build()).build());
        testEntityManager.flush();
        testEntityManager.clear();

        PatientsFilter patientsFilter = new PatientsFilter(20, 0, null, null, null, null, null, null, "e" + ParsedFilter.filterConditionValueSeparator + "Poland" + ParsedFilter.filterValueSeparator + "Germany");
        List<Patient> patients = patientRepository.findAll(PatientSpecification.whereFilter(patientsFilter));
        List<Integer> patientIds = patients.stream().map(Patient::getId).toList();

        assertEquals(2, patientIds.size());
        assertTrue(patientIds.containsAll(List.of(polishPatient.getId(), germanPatient.getId())));
    }
}
