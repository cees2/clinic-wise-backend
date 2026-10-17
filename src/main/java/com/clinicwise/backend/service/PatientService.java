package com.clinicwise.backend.service;

import com.clinicwise.backend.api.response.ApiResponse;
import com.clinicwise.backend.api.response.PageResponse;
import com.clinicwise.backend.common.list.filter.PatientsFilter;
import com.clinicwise.backend.common.list.sort.SearchSelectBaseFilter;
import com.clinicwise.backend.common.list.sort.SortUtil;
import com.clinicwise.backend.dto.request.CreatePatientRequest;
import com.clinicwise.backend.dto.request.UpdatePatientRequest;
import com.clinicwise.backend.dto.response.PatientResponse;
import com.clinicwise.backend.dto.response.SearchSelect;
import com.clinicwise.backend.entity.Patient;
import com.clinicwise.backend.entity.User;
import com.clinicwise.backend.exceptions.UserWithProvidedDataExists;
import com.clinicwise.backend.mapper.PatientMapper;
import com.clinicwise.backend.repository.PatientRepository;
import com.clinicwise.backend.repository.UserRepository;
import com.clinicwise.backend.specification.PatientSpecification;
import com.clinicwise.backend.specification.UserSpecifications;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class PatientService {
    private static final Map<String, String> sortMap = Map.of(
            "name", "user.firstname",
            "lastname", "user.lastname",
            "date_of_birth", "user.dateOfBirth",
            "gender", "user.gender",
            "nationality", "user.nationality"
    );
    private PatientRepository patientRepository;
    private UserRepository userRepository;
    private PatientMapper patientMapper;

    public PatientService(PatientRepository patientRepository, UserRepository userRepository, PatientMapper patientMapper) {
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
        this.patientMapper = patientMapper;
    }

    public PageResponse<PatientResponse> getAllPatients(PatientsFilter patientsFilter) {
        Sort sort = SortUtil.parseFromString(patientsFilter.getSort(), sortMap);
        Pageable pageable = PageRequest.of(patientsFilter.getPage(), patientsFilter.getSize() + 1, sort);
        Page<Patient> patients = patientRepository.findAll(PatientSpecification.whereFilter(patientsFilter), pageable);
        List<PatientResponse> patientsList = patients.stream()
                .map(patientMapper::toResponse)
                .limit(patientsFilter.getSize())
                .toList();
        boolean hasNext = patients.getNumberOfElements() == patientsFilter.getSize() + 1;

        return PageResponse.toResponse(patientsList, hasNext);
    }

    public ApiResponse<PatientResponse> getPatient(int patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find a patient with ID: " + patientId));

        return ApiResponse.toResponse(patientMapper.toResponse(patient));
    }

    @Transactional
    public ApiResponse<PatientResponse> updatePatient(int patientId, UpdatePatientRequest updatePatientRequest) {
        Patient patientToBeUpdated = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find a patient with ID: " + patientId));

        assertNoDuplicatePatient(
                patientToBeUpdated.getUser().getId(),
                updatePatientRequest.documentId(),
                updatePatientRequest.phoneNumber(),
                updatePatientRequest.username()
        );

        patientMapper.updatePatientFromRequest(updatePatientRequest, patientToBeUpdated);

        return ApiResponse.toResponse(patientMapper.toResponse(patientToBeUpdated));
    }

    @Transactional
    public ApiResponse<PatientResponse> createPatient(CreatePatientRequest createPatientRequest) {
        assertNoDuplicatePatient(
                null,
                createPatientRequest.documentId(),
                createPatientRequest.phoneNumber(),
                createPatientRequest.username()
        );

        Patient patient = patientMapper.createPatientFromRequest(createPatientRequest);

        userRepository.save(patient.getUser());
        patientRepository.save(patient);

        return ApiResponse.toResponse(patientMapper.toResponse(patient));
    }

    @Transactional
    public void deletePatient(int patientId) {
        Patient patientToBeDeleted = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find a patient with ID: " + patientId));

        patientRepository.delete(patientToBeDeleted);
    }

    @Transactional
    public ApiResponse<List<PatientResponse>> generatePatients() {
        List<Patient> patients = patientMapper.generateFakePatients();

        patientRepository.saveAll(patients);

        return ApiResponse.toResponse(patients
                .stream()
                .map(patientMapper::toResponse)
                .toList()
        );
    }

    public ApiResponse<PageResponse<SearchSelect>> getSearchSelect(SearchSelectBaseFilter searchSelectBaseFilter) {
        Pageable pageable = PageRequest.of(searchSelectBaseFilter.getPage(), searchSelectBaseFilter.getSize(), Sort.by("user.firstname", "user.lastname"));
        Page<Patient> employees = patientRepository.findAll(PatientSpecification.searchSelect(searchSelectBaseFilter.getSearch()), pageable);
        List<SearchSelect> employeesSearchSelect = employees.stream().map(patientMapper::toSearchSelect).toList();
        boolean hasMore = employees.getTotalPages() != searchSelectBaseFilter.getPage();
        PageResponse<SearchSelect> pageResponse = new PageResponse<>(employeesSearchSelect, hasMore);

        return ApiResponse.toResponse(pageResponse);
    }

    private void assertNoDuplicatePatient(Integer patientUserId, String documentId, String phoneNumber, String username) {
        List<User> usersWithSimilarData = userRepository
                .findAll(
                        UserSpecifications
                                .hasDocumentIDOrPhoneNumberOrUserName(documentId, phoneNumber, username)
                );

        if (usersWithSimilarData.stream().anyMatch(user -> !user.getId().equals(patientUserId))) {
            throw new UserWithProvidedDataExists();
        }
    }
}
