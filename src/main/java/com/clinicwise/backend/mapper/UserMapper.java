package com.clinicwise.backend.mapper;

import com.clinicwise.backend.dto.response.UserResponse;
import com.clinicwise.backend.entity.Authority;
import com.clinicwise.backend.entity.User;
import com.clinicwise.backend.enums.AuthorityType;
import com.clinicwise.backend.enums.Gender;
import com.clinicwise.backend.faker.CustomFaker;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Component
public class UserMapper {
    private static final Map<String, Gender> genderMap = Map.of(
            "Male", Gender.MALE,
            "Female", Gender.FEMALE
    );
    private static final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);

    public static UserResponse toResponse(User user) {
        Set<String> authorities = user.getAuthorities()
                .stream()
                .map(authority -> authority.getAuthority().name())
                .collect(Collectors.toSet());

        return new UserResponse(
                user.getUsername(),
                user.getFirstname(),
                user.getLastname(),
                user.getGender(),
                user.getAddress(),
                user.getDateOfBirth(),
                user.getDocumentId(),
                user.getEnabled(),
                user.getNationality(),
                user.getPhoneNumber(),
                authorities
        );
    }

    public static User generateFakeUser(){
        CustomFaker faker = new CustomFaker();
        String username = faker.internet().emailAddress();
        Gender gender = genderMap.get(faker.gender().binaryTypes());
        User user = new User();
        Authority authority = new Authority();

        authority.setUsername(username);
        authority.setUser(user);
        authority.setAuthority(AuthorityType.ROLE_PATIENT);

        user.setUsername(username);
        user.setPassword(bCryptPasswordEncoder.encode("pass1234"));
        user.setFirstname(faker.name().firstName());
        user.setLastname(faker.name().lastName());
        user.setEnabled(true);
        user.setAddress(faker.address().fullAddress());
        user.setDateOfBirth(MapperUtils.randomDate60YearsTo20YearsAgo());
        user.setDocumentId(faker.letterify("???", true) + " " + faker.numerify("##"));
        user.setNationality(faker.country().name());
        user.setPhoneNumber(faker.phoneNumber().phoneNumber());
        user.setGender(gender);
        user.setAuthorities(Set.of(authority));

        return user;
    }

}
