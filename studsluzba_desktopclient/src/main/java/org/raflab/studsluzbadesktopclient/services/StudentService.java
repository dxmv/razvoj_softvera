package org.raflab.studsluzbadesktopclient.services;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.dto.*;
import org.raflab.studsluzbadesktopclient.client.StudentApiClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service layer for student-related business logic.
 * Delegates HTTP operations to StudentApiClient.
 * Contains validation, business rules, and orchestration.
 */
@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentApiClient studentApiClient;

    // ==================== ASYNC METHODS (non-blocking for UI) ====================

    /**
     * Search students by name asynchronously.
     * Use for UI search operations that shouldn't block.
     */
    public Flux<StudentDto> searchStudentsAsync(String ime, String prezime) {
        return studentApiClient.searchStudentsAsync(ime, prezime);
    }

    /**
     * Fetch all students asynchronously.
     */
    public Flux<StudentDto> fetchAllStudentsAsync() {
        return studentApiClient.fetchAllStudentsAsync();
    }

    /**
     * Find student by index asynchronously.
     */
    public Mono<StudentDto> findStudentByIndexAsync(String indeks) {
        if (indeks == null || indeks.isBlank()) {
            return Mono.empty();
        }
        return studentApiClient.findStudentByIndexAsync(indeks);
    }

    /**
     * Find active index value for student asynchronously.
     */
    public Mono<String> findActiveIndexValue(Long studentId) {
        if (studentId == null) {
            return Mono.error(new IllegalArgumentException("studentId is required"));
        }
        return studentApiClient.findActiveIndexValueAsync(studentId);
    }

    /**
     * Find passed exams for student asynchronously.
     */
    public Flux<PolozenPredmetDto> findPassedExams(String indeks) {
        if (indeks == null || indeks.isBlank()) {
            return Flux.empty();
        }
        return studentApiClient.findPassedExamsAsync(indeks);
    }

    /**
     * Find failed exams for student asynchronously.
     */
    public Flux<PredmetDto> findFailedExams(String indeks) {
        if (indeks == null || indeks.isBlank()) {
            return Flux.empty();
        }
        return studentApiClient.findFailedExamsAsync(indeks);
    }

    /**
     * Find enrolled years for student asynchronously.
     */
    public Flux<UpisGodineDto> findEnrolledYears(String indeks) {
        if (indeks == null || indeks.isBlank()) {
            return Flux.empty();
        }
        return studentApiClient.findEnrolledYearsAsync(indeks);
    }

    /**
     * Find repeated years for student asynchronously.
     */
    public Flux<ObnovaGodineDto> findRepeatedYears(String indeks) {
        if (indeks == null || indeks.isBlank()) {
            return Flux.empty();
        }
        return studentApiClient.findRepeatedYearsAsync(indeks);
    }

    /**
     * Enroll student in year asynchronously.
     * Validates input before making API call.
     */
    public Mono<UpisGodineDto> enrollYear(String indeks, UpisGodineEnrollmentRequest request) {
        if (indeks == null || indeks.isBlank()) {
            return Mono.error(new IllegalArgumentException("Indeks je obavezan"));
        }
        if (request == null) {
            return Mono.error(new IllegalArgumentException("Request je obavezan"));
        }
        if (request.getGodinaStudija() == null) {
            return Mono.error(new IllegalArgumentException("Godina studija je obavezna"));
        }
        return studentApiClient.enrollYearAsync(indeks, request);
    }

    /**
     * Repeat year for student asynchronously.
     * Validates input before making API call.
     */
    public Mono<ObnovaGodineDto> repeatYear(String indeks, ObnovaGodineRequest request) {
        if (indeks == null || indeks.isBlank()) {
            return Mono.error(new IllegalArgumentException("Indeks je obavezan"));
        }
        if (request == null) {
            return Mono.error(new IllegalArgumentException("Request je obavezan"));
        }
        if (request.getGodinaStudija() == null) {
            return Mono.error(new IllegalArgumentException("Godina studija je obavezna"));
        }
        if (request.getPredmetIds() == null || request.getPredmetIds().isEmpty()) {
            return Mono.error(new IllegalArgumentException("Morate izabrati bar jedan predmet"));
        }
        return studentApiClient.repeatYearAsync(indeks, request);
    }

    /**
     * Find students by high school asynchronously.
     */
    public Flux<StudentDto> findStudentsByHighSchoolAsync(Long srednjaSkolaId) {
        if (srednjaSkolaId == null) {
            return Flux.empty();
        }
        return studentApiClient.findStudentsByHighSchoolAsync(srednjaSkolaId);
    }

    // ==================== SYNC METHODS (blocking - for reports) ====================

    /**
     * Search student synchronously.
     * Use for reports or when blocking is acceptable.
     */
    public List<StudentDto> searchStudent(String ime) {
        return studentApiClient.searchStudentSync(ime);
    }

    /**
     * Save student synchronously.
     */
    public Integer saveStudent(StudentDto student) {
        if (student == null) {
            return null;
        }
        return studentApiClient.saveStudentSync(student);
    }

    /**
     * Get all students synchronously.
     * Use for reports.
     */
    public List<StudentDto> sviStudenti() {
        return studentApiClient.getAllStudentsSync();
    }

    /**
     * Search students with pagination synchronously.
     * Use for reports.
     */
    public List<StudentDto> searchStudentsPaged(String ime, String prezime) {
        return studentApiClient.searchStudentsPagedSync(ime, prezime);
    }

    /**
     * Find student by index synchronously.
     */
    public StudentDto findStudentByIndex(String indeks) {
        if (indeks == null || indeks.isBlank()) {
            return null;
        }
        return studentApiClient.findStudentByIndexSync(indeks);
    }

    /**
     * Find students by high school synchronously.
     * Use for reports.
     */
    public List<StudentDto> findStudentsByHighSchool(Long srednjaSkolaId) {
        if (srednjaSkolaId == null) {
            return List.of();
        }
        return studentApiClient.findStudentsByHighSchoolSync(srednjaSkolaId);
    }

    /**
     * Search students by enrollment year synchronously.
     */
    public List<StudentDto> searchStudentsByGodinaUpisa(Integer godinaUpisa) {
        if (godinaUpisa == null) {
            return List.of();
        }
        return studentApiClient.searchStudentsByGodinaUpisaSync(godinaUpisa);
    }

    /**
     * Search students by study program synchronously.
     */
    public List<StudentDto> searchStudentsByStudProg(String studProg) {
        if (studProg == null || studProg.isBlank()) {
            return List.of();
        }
        return studentApiClient.searchStudentsByStudProgSync(studProg);
    }
}
