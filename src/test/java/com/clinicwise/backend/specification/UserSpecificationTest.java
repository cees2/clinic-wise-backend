package com.clinicwise.backend.specification;

import com.clinicwise.backend.config.MySqlTestContainersConfig;
import com.clinicwise.backend.entity.User;
import com.clinicwise.backend.repository.UserRepository;
import com.clinicwise.backend.testdata.UserBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(MySqlTestContainersConfig.class)
class UserSpecificationTest {
    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should return a user with the matching phone number")
    void findConflictingMatchingPhoneNumber() {
        String matchingPhoneNumber = "111222333";
        User user = testEntityManager.persist(UserBuilder.aUser().withPhoneNumber(matchingPhoneNumber).build());
        User newUser = UserBuilder.aUser().withPhoneNumber(matchingPhoneNumber).build();
        testEntityManager.flush();
        testEntityManager.clear();

        List<User> foundUsers = userRepository.findAll(UserSpecification.findConflicting(newUser));

        assertEquals(1, foundUsers.size());
        assertEquals(user.getId(), foundUsers.getFirst().getId());
    }

    @Test
    @DisplayName("Should return a user with the matching document ID")
    void findConflictingMatchingDocumentId() {
        String matchingDocumentId = "AAA 01";
        User user = testEntityManager.persist(UserBuilder.aUser().withDocumentID(matchingDocumentId).build());
        User newUser = UserBuilder.aUser().withDocumentID(matchingDocumentId).build();
        testEntityManager.flush();
        testEntityManager.clear();

        List<User> foundUsers = userRepository.findAll(UserSpecification.findConflicting(newUser));

        assertEquals(1, foundUsers.size());
        assertEquals(user.getId(), foundUsers.getFirst().getId());
    }

    @Test
    @DisplayName("Should return a user with the matching username")
    void findConflictingMatchingUsername() {
        String matchingUsername = "test@test.com";
        User user = testEntityManager.persist(UserBuilder.aUser().withUsername(matchingUsername).build());
        User newUser = UserBuilder.aUser().withUsername(matchingUsername).build();
        testEntityManager.flush();
        testEntityManager.clear();

        List<User> foundUsers = userRepository.findAll(UserSpecification.findConflicting(newUser));

        assertEquals(1, foundUsers.size());
        assertEquals(user.getId(), foundUsers.getFirst().getId());
    }

    @Test
    @DisplayName("Should not return itself")
    void findConflictingShouldNotReturnItself() {
        String matchingPhoneNumber = "111222333";
        User user = testEntityManager.persist(UserBuilder.aUser().withPhoneNumber(matchingPhoneNumber).build());
        User newUser = UserBuilder.aUser().withId(user.getId()).withPhoneNumber(matchingPhoneNumber).build();
        testEntityManager.flush();
        testEntityManager.clear();

        List<User> foundUsers = userRepository.findAll(UserSpecification.findConflicting(newUser));

        assertEquals(0, foundUsers.size());
    }
}