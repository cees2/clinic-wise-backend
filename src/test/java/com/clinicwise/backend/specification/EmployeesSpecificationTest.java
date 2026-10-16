package com.clinicwise.backend.specification;

import com.clinicwise.backend.common.list.filter.EmployeesFilter;
import com.clinicwise.backend.common.list.filter.ParsedFilter;
import com.clinicwise.backend.config.MySqlTestContainersConfig;
import com.clinicwise.backend.entity.Employee;
import com.clinicwise.backend.enums.Country;
import com.clinicwise.backend.enums.Gender;
import com.clinicwise.backend.repository.EmployeeRepository;
import com.clinicwise.backend.testdata.EmployeeBuilder;
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

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(MySqlTestContainersConfig.class)
class EmployeesSpecificationTest {
    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    TestEntityManager testEntityManager;

    @Test
    @DisplayName("Should search for employees by their full name")
    void returnEmployeesByFullNameSearch() {
        Employee matchingEmployee0 = testEntityManager.persist(EmployeeBuilder.anEmployee().withUser(UserBuilder.aUser().withFirstname("Damian").withLastname("Mirek").build()).build());
        Employee matchingEmployee1 = testEntityManager.persist(EmployeeBuilder.anEmployee().withUser(UserBuilder.aUser().withFirstname("Damian").withLastname("Mtest").build()).build());
        Employee nonMatchingEmployee = testEntityManager.persist(EmployeeBuilder.anEmployee().withUser(UserBuilder.aUser().withFirstname("John").withLastname("Smith").build()).build());
        testEntityManager.flush();
        testEntityManager.clear();

        EmployeesFilter employeesFilter = new EmployeesFilter(20, 0, null, null, "damian m", null, null, null, null, null);
        List<Employee> employeeList = employeeRepository.findAll(EmployeesSpecification.whereFilter(employeesFilter));
        List<Integer> employeeIds = employeeList.stream().map(Employee::getId).toList();

        assertEquals(2, employeeList.size());
        assertTrue(employeeIds.containsAll(List.of(matchingEmployee0.getId(), matchingEmployee1.getId())));
    }

    @Test
    @DisplayName("Should search for employees by their username")
    void returnEmployeesByUsernameSearch() {
        Employee matchingEmployee0 = testEntityManager.persist(EmployeeBuilder.anEmployee().withUser(UserBuilder.aUser().withUsername("test#1").build()).build());
        Employee matchingEmployee1 = testEntityManager.persist(EmployeeBuilder.anEmployee().withUser(UserBuilder.aUser().withUsername("test#2").build()).build());
        Employee nonMatchingEmployee = testEntityManager.persist(EmployeeBuilder.anEmployee().withUser(UserBuilder.aUser().withUsername("no match").build()).build());
        testEntityManager.flush();
        testEntityManager.clear();

        EmployeesFilter employeesFilter = new EmployeesFilter(20, 0, null, null, "test", null, null, null, null, null);
        List<Employee> employeeList = employeeRepository.findAll(EmployeesSpecification.whereFilter(employeesFilter));
        List<Integer> employeeIds = employeeList.stream().map(Employee::getId).toList();

        assertEquals(2, employeeList.size());
        assertTrue(employeeIds.containsAll(List.of(matchingEmployee0.getId(), matchingEmployee1.getId())));
    }

    @Test
    @DisplayName("Should search for employees by their phone number")
    void returnEmployeesByPhoneNumberSearch() {
        Employee matchingEmployee0 = testEntityManager.persist(EmployeeBuilder.anEmployee().withUser(UserBuilder.aUser().withPhoneNumber("123123123").build()).build());
        Employee matchingEmployee1 = testEntityManager.persist(EmployeeBuilder.anEmployee().withUser(UserBuilder.aUser().withPhoneNumber("123321321").build()).build());
        Employee nonMatchingEmployee = testEntityManager.persist(EmployeeBuilder.anEmployee().withUser(UserBuilder.aUser().withPhoneNumber("999999999").build()).build());
        testEntityManager.flush();
        testEntityManager.clear();

        EmployeesFilter employeesFilter = new EmployeesFilter(20, 0, null, null, "123", null, null, null, null, null);
        List<Employee> employeeList = employeeRepository.findAll(EmployeesSpecification.whereFilter(employeesFilter));
        List<Integer> employeeIds = employeeList.stream().map(Employee::getId).toList();

        assertEquals(2, employeeList.size());
        assertTrue(employeeIds.containsAll(List.of(matchingEmployee0.getId(), matchingEmployee1.getId())));
    }

    @Test
    @DisplayName("Should search for employees by their document ID")
    void returnEmployeesByDocumentIdSearch() {
        Employee matchingEmployee0 = testEntityManager.persist(EmployeeBuilder.anEmployee().withUser(UserBuilder.aUser().withDocumentID("AA 001").build()).build());
        Employee matchingEmployee1 = testEntityManager.persist(EmployeeBuilder.anEmployee().withUser(UserBuilder.aUser().withDocumentID("AA 333").build()).build());
        Employee nonMatchingEmployee = testEntityManager.persist(EmployeeBuilder.anEmployee().withUser(UserBuilder.aUser().withDocumentID("BB 444").build()).build());
        testEntityManager.flush();
        testEntityManager.clear();

        EmployeesFilter employeesFilter = new EmployeesFilter(20, 0, null, null, "AA", null, null, null, null, null);
        List<Employee> employeeList = employeeRepository.findAll(EmployeesSpecification.whereFilter(employeesFilter));
        List<Integer> employeeIds = employeeList.stream().map(Employee::getId).toList();

        assertEquals(2, employeeList.size());
        assertTrue(employeeIds.containsAll(List.of(matchingEmployee0.getId(), matchingEmployee1.getId())));
    }

    @Test
    @DisplayName("Should return one employee of two based on the name filter(contains)")
    void returnEmployeesByContainsNameFilter() {
        Employee matchingEmployee = testEntityManager.persist(EmployeeBuilder.anEmployee().withUser(UserBuilder.aUser().withFirstname("Damian").build()).build());
        Employee nonMatchingEmployee = testEntityManager.persist(EmployeeBuilder.anEmployee().withUser(UserBuilder.aUser().withFirstname("Matt").build()).build());
        testEntityManager.flush();
        testEntityManager.clear();

        EmployeesFilter employeesFilter = new EmployeesFilter(20, 0, null, "c" + ParsedFilter.filterConditionValueSeparator + "dam", null, null, null, null, null, null);
        List<Employee> employeeList = employeeRepository.findAll(EmployeesSpecification.whereFilter(employeesFilter));

        assertEquals(1, employeeList.size());
        assertEquals(matchingEmployee.getId(), employeeList.getFirst().getId());
    }

    @Test
    @DisplayName("Should return one employee of two based on the name filter(equals)")
    void returnEmployeesByEqualsNameFilter() {
        Employee matchingEmployee = testEntityManager.persist(EmployeeBuilder.anEmployee().withUser(UserBuilder.aUser().withFirstname("Damian").build()).build());
        Employee nonMatchingEmployee = testEntityManager.persist(EmployeeBuilder.anEmployee().withUser(UserBuilder.aUser().withFirstname("Matt").build()).build());
        testEntityManager.flush();
        testEntityManager.clear();

        EmployeesFilter employeesFilter = new EmployeesFilter(20, 0, null, "e" + ParsedFilter.filterConditionValueSeparator + "damian", null, null, null, null, null, null);
        List<Employee> employeeList = employeeRepository.findAll(EmployeesSpecification.whereFilter(employeesFilter));

        assertEquals(1, employeeList.size());
        assertEquals(matchingEmployee.getId(), employeeList.getFirst().getId());
    }

    @Test
    @DisplayName("Should return one employee of two based on the surname filter(contains)")
    void returnEmployeesByContainsSurnameFilter() {
        Employee matchingEmployee = testEntityManager.persist(EmployeeBuilder.anEmployee().withUser(UserBuilder.aUser().withLastname("Brown").build()).build());
        Employee nonMatchingEmployee = testEntityManager.persist(EmployeeBuilder.anEmployee().withUser(UserBuilder.aUser().withLastname("Smith").build()).build());
        testEntityManager.flush();
        testEntityManager.clear();

        EmployeesFilter employeesFilter = new EmployeesFilter(20, 0, null, null, null, "c" + ParsedFilter.filterConditionValueSeparator + "bro", null, null, null, null);
        List<Employee> employeeList = employeeRepository.findAll(EmployeesSpecification.whereFilter(employeesFilter));

        assertEquals(1, employeeList.size());
        assertEquals(matchingEmployee.getId(), employeeList.getFirst().getId());
    }

    @Test
    @DisplayName("Should return one employee of two based on the surname filter(equals)")
    void returnEmployeesByEqualsSurnameFilter() {
        Employee matchingEmployee = testEntityManager.persist(EmployeeBuilder.anEmployee().withUser(UserBuilder.aUser().withLastname("Brown").build()).build());
        Employee nonMatchingEmployee = testEntityManager.persist(EmployeeBuilder.anEmployee().withUser(UserBuilder.aUser().withLastname("Smith").build()).build());
        testEntityManager.flush();
        testEntityManager.clear();

        EmployeesFilter employeesFilter = new EmployeesFilter(20, 0, null, null, null, "e" + ParsedFilter.filterConditionValueSeparator + "brown", null, null, null, null);
        List<Employee> employeeList = employeeRepository.findAll(EmployeesSpecification.whereFilter(employeesFilter));

        assertEquals(1, employeeList.size());
        assertEquals(matchingEmployee.getId(), employeeList.getFirst().getId());
    }

    @Test
    @DisplayName("Should return one employee of two based on the gender filter(male)")
    void returnEmployeesByGenderMaleFilter() {
        Employee matchingEmployee = testEntityManager.persist(EmployeeBuilder.anEmployee().withUser(UserBuilder.aUser().withGender(Gender.MALE).build()).build());
        Employee nonMatchingEmployee = testEntityManager.persist(EmployeeBuilder.anEmployee().withUser(UserBuilder.aUser().withGender(Gender.FEMALE).build()).build());
        testEntityManager.flush();
        testEntityManager.clear();

        EmployeesFilter employeesFilter = new EmployeesFilter(20, 0, null, null, null, null, "e" + ParsedFilter.filterConditionValueSeparator + "male", null, null, null);
        List<Employee> employeeList = employeeRepository.findAll(EmployeesSpecification.whereFilter(employeesFilter));

        assertEquals(1, employeeList.size());
        assertEquals(matchingEmployee.getId(), employeeList.getFirst().getId());
    }

    @Test
    @DisplayName("Should return one employee of two based on the start date filter(less than or equal to)")
    void returnEmployeesByStartDateLTEFilter() {
        Employee matchingEmployee = testEntityManager.persist(EmployeeBuilder.anEmployee().withUser(UserBuilder.aUser().build()).withStartDate(LocalDate.of(2026,10,1)).build());
        Employee nonMatchingEmployee = testEntityManager.persist(EmployeeBuilder.anEmployee().withUser(UserBuilder.aUser().build()).withStartDate(LocalDate.of(2026,10,4)).build());
        testEntityManager.flush();
        testEntityManager.clear();

        EmployeesFilter employeesFilter = new EmployeesFilter(20, 0, null, null, null, null, null, "lte" + ParsedFilter.filterConditionValueSeparator + "2026-10-02", null, null);
        List<Employee> employeeList = employeeRepository.findAll(EmployeesSpecification.whereFilter(employeesFilter));

        assertEquals(1, employeeList.size());
        assertEquals(matchingEmployee.getId(), employeeList.getFirst().getId());
    }

    @Test
    @DisplayName("Should return one employee of two based on the start date filter(greater than or equal to)")
    void returnEmployeesByStartDateGTEFilter() {
        Employee matchingEmployee = testEntityManager.persist(EmployeeBuilder.anEmployee().withUser(UserBuilder.aUser().build()).withStartDate(LocalDate.of(2026,10,4)).build());
        Employee nonMatchingEmployee = testEntityManager.persist(EmployeeBuilder.anEmployee().withUser(UserBuilder.aUser().build()).withStartDate(LocalDate.of(2026,10,1)).build());
        testEntityManager.flush();
        testEntityManager.clear();

        EmployeesFilter employeesFilter = new EmployeesFilter(20, 0, null, null, null, null, null, "gte" + ParsedFilter.filterConditionValueSeparator + "2026-10-02", null, null);
        List<Employee> employeeList = employeeRepository.findAll(EmployeesSpecification.whereFilter(employeesFilter));

        assertEquals(1, employeeList.size());
        assertEquals(matchingEmployee.getId(), employeeList.getFirst().getId());
    }

    @Test
    @DisplayName("Should return one employee of two based on the date of birth filter(less than or equal to)")
    void returnEmployeesByDateOfBirthLTEFilter() {
        Employee matchingEmployee = testEntityManager.persist(EmployeeBuilder.anEmployee().withUser(UserBuilder.aUser().build()).withBirthDate(LocalDate.of(2026,10,1)).build());
        Employee nonMatchingEmployee = testEntityManager.persist(EmployeeBuilder.anEmployee().withUser(UserBuilder.aUser().build()).withBirthDate(LocalDate.of(2026,10,4)).build());
        testEntityManager.flush();
        testEntityManager.clear();

        EmployeesFilter employeesFilter = new EmployeesFilter(20, 0, null, null, null, null, null, null, "lte" + ParsedFilter.filterConditionValueSeparator + "2026-10-02", null);
        List<Employee> employeeList = employeeRepository.findAll(EmployeesSpecification.whereFilter(employeesFilter));

        assertEquals(1, employeeList.size());
        assertEquals(matchingEmployee.getId(), employeeList.getFirst().getId());
    }

    @Test
    @DisplayName("Should return one employee of two based on the date of birth filter(greater than or equal to)")
    void returnEmployeesByDateOfBirthGTEFilter() {
        Employee matchingEmployee = testEntityManager.persist(EmployeeBuilder.anEmployee().withUser(UserBuilder.aUser().build()).withBirthDate(LocalDate.of(2026,10,4)).build());
        Employee nonMatchingEmployee = testEntityManager.persist(EmployeeBuilder.anEmployee().withUser(UserBuilder.aUser().build()).withBirthDate(LocalDate.of(2026,10,1)).build());
        testEntityManager.flush();
        testEntityManager.clear();

        EmployeesFilter employeesFilter = new EmployeesFilter(20, 0, null, null, null, null, null, null, "gte" + ParsedFilter.filterConditionValueSeparator + "2026-10-02", null);
        List<Employee> employeeList = employeeRepository.findAll(EmployeesSpecification.whereFilter(employeesFilter));

        assertEquals(1, employeeList.size());
        assertEquals(matchingEmployee.getId(), employeeList.getFirst().getId());
    }

    @Test
    @DisplayName("Should return one employee of three based on the nationality filter(Poland)")
    void returnEmployeesByNationalityPolandFilter() {
        Employee matchingEmployee = testEntityManager.persist(EmployeeBuilder.anEmployee().withUser(UserBuilder.aUser().build()).withNationality(Country.POLAND).build());
        Employee nonMatchingEmployee0 = testEntityManager.persist(EmployeeBuilder.anEmployee().withUser(UserBuilder.aUser().build()).withNationality(Country.GERMANY).build());
        Employee nonMatchingEmployee1 = testEntityManager.persist(EmployeeBuilder.anEmployee().withUser(UserBuilder.aUser().build()).withNationality(Country.UNITED_STATES).build());
        testEntityManager.flush();
        testEntityManager.clear();

        EmployeesFilter employeesFilter = new EmployeesFilter(20, 0, null, null, null, null, null, null, null, "e" + ParsedFilter.filterConditionValueSeparator + "POLAND");
        List<Employee> employeeList = employeeRepository.findAll(EmployeesSpecification.whereFilter(employeesFilter));

        assertEquals(1, employeeList.size());
        assertEquals(matchingEmployee.getId(), employeeList.getFirst().getId());
    }

    @Test
    @DisplayName("Should return one employee of three based on the nationality filter(Poland, Germany)")
    void returnEmployeesByNationalityPolandAndGermanyFilter() {
        Employee matchingEmployee0 = testEntityManager.persist(EmployeeBuilder.anEmployee().withUser(UserBuilder.aUser().build()).withNationality(Country.POLAND).build());
        Employee matchingEmployee1 = testEntityManager.persist(EmployeeBuilder.anEmployee().withUser(UserBuilder.aUser().build()).withNationality(Country.GERMANY).build());
        Employee nonMatchingEmployee1 = testEntityManager.persist(EmployeeBuilder.anEmployee().withUser(UserBuilder.aUser().build()).withNationality(Country.UNITED_STATES).build());
        testEntityManager.flush();
        testEntityManager.clear();

        EmployeesFilter employeesFilter = new EmployeesFilter(20, 0, null, null, null, null, null, null, null, "e" + ParsedFilter.filterConditionValueSeparator + "POLAND" + ParsedFilter.filterValueSeparator + "GERMANY");
        List<Employee> employeeList = employeeRepository.findAll(EmployeesSpecification.whereFilter(employeesFilter));
        List<Integer> employeeIds = employeeList.stream().map(Employee::getId).toList();

        assertEquals(2, employeeList.size());
        assertTrue(employeeIds.containsAll(List.of(matchingEmployee0.getId(), matchingEmployee1.getId())));
    }
}