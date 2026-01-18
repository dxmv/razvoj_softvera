package org.raflab.studsluzbadesktopclient.client;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.dto.*;
import org.raflab.studsluzbadesktopclient.utils.PageResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * API Client for student-related HTTP operations.
 * This class handles all HTTP communication with the server for student endpoints.
 * Use async methods (returning Mono/Flux) for non-blocking UI operations.
 * Use sync methods (returning direct values) when blocking is acceptable.
 */
@Component
@RequiredArgsConstructor
public class StudentApiClient {

    private final RestTemplate restTemplate;
    private final WebClient webClient;
    private final String baseUrl;

    private static final String STUDENT_URL_PATH = "/student";
    private static final String API_STUDENT_PATH = "/api/studenti";
    private static final int DEFAULT_PAGE_SIZE = 1000;

    // ==================== ASYNC METHODS (WebClient - non-blocking) ====================

    /**
     * Search students by name asynchronously (non-blocking).
     */
    public Flux<StudentDto> searchStudentsAsync(String ime, String prezime) {
        return webClient
                .get()
                .uri(uriBuilder -> {
                    uriBuilder.path(API_STUDENT_PATH + "/search")
                            .queryParam("page", 0)
                            .queryParam("size", DEFAULT_PAGE_SIZE);
                    if (ime != null && !ime.trim().isEmpty()) {
                        uriBuilder.queryParam("ime", ime.trim());
                    }
                    if (prezime != null && !prezime.trim().isEmpty()) {
                        uriBuilder.queryParam("prezime", prezime.trim());
                    }
                    return uriBuilder.build();
                })
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<PageResponse<StudentDto>>() {})
                .flatMapMany(page -> Flux.fromIterable(page.getContent()));
    }

    /**
     * Fetch all students asynchronously (non-blocking).
     */
    public Flux<StudentDto> fetchAllStudentsAsync() {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(API_STUDENT_PATH)
                        .queryParam("page", 0)
                        .queryParam("size", DEFAULT_PAGE_SIZE)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<PageResponse<StudentDto>>() {})
                .flatMapMany(page -> Flux.fromIterable(page.getContent()));
    }

    /**
     * Find student by index asynchronously (non-blocking).
     */
    public Mono<StudentDto> findStudentByIndexAsync(String indeks) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(API_STUDENT_PATH + "/by-index/{index}")
                        .build(indeks))
                .retrieve()
                .bodyToMono(StudentDto.class);
    }

    /**
     * Find active index value for student asynchronously (non-blocking).
     */
    public Mono<String> findActiveIndexValueAsync(Long studentId) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(API_STUDENT_PATH + "/{id}/active-index")
                        .build(studentId))
                .retrieve()
                .bodyToMono(String.class);
    }

    /**
     * Find passed exams for student asynchronously (non-blocking).
     */
    public Flux<PolozenPredmetDto> findPassedExamsAsync(String indeks) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(API_STUDENT_PATH + "/by-index/{index}/passed")
                        .queryParam("size", DEFAULT_PAGE_SIZE)
                        .build(indeks))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<PageResponse<PolozenPredmetDto>>() {})
                .flatMapMany(page -> Flux.fromIterable(page.getContent()));
    }

    /**
     * Find failed exams for student asynchronously (non-blocking).
     */
    public Flux<PredmetDto> findFailedExamsAsync(String indeks) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(API_STUDENT_PATH + "/by-index/{index}/failed")
                        .queryParam("size", DEFAULT_PAGE_SIZE)
                        .build(indeks))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<PageResponse<PredmetDto>>() {})
                .flatMapMany(page -> Flux.fromIterable(page.getContent()));
    }

    /**
     * Find enrolled years for student asynchronously (non-blocking).
     */
    public Flux<UpisGodineDto> findEnrolledYearsAsync(String indeks) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(API_STUDENT_PATH + "/by-index/{index}/enrolled")
                        .build(indeks))
                .retrieve()
                .bodyToFlux(UpisGodineDto.class);
    }

    /**
     * Find repeated years for student asynchronously (non-blocking).
     */
    public Flux<ObnovaGodineDto> findRepeatedYearsAsync(String indeks) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(API_STUDENT_PATH + "/by-index/{index}/repeated")
                        .build(indeks))
                .retrieve()
                .bodyToFlux(ObnovaGodineDto.class);
    }

    /**
     * Enroll student in year asynchronously (non-blocking).
     */
    public Mono<UpisGodineDto> enrollYearAsync(String indeks, UpisGodineEnrollmentRequest request) {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(API_STUDENT_PATH + "/by-index/{index}/enroll")
                        .build(indeks))
                .bodyValue(request)
                .retrieve()
                .bodyToMono(UpisGodineDto.class);
    }

    /**
     * Repeat year for student asynchronously (non-blocking).
     */
    public Mono<ObnovaGodineDto> repeatYearAsync(String indeks, ObnovaGodineRequest request) {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(API_STUDENT_PATH + "/by-index/{index}/repeat")
                        .build(indeks))
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ObnovaGodineDto.class);
    }

    /**
     * Find students by high school asynchronously (non-blocking).
     */
    public Flux<StudentDto> findStudentsByHighSchoolAsync(Long srednjaSkolaId) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(API_STUDENT_PATH + "/by-high-school/{id}")
                        .build(srednjaSkolaId))
                .retrieve()
                .bodyToFlux(StudentDto.class);
    }


    /**
     * Search student by name synchronously (blocking).
     * Use for reports or when immediate result is needed.
     */
    public List<StudentDto> searchStudentSync(String ime) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + STUDENT_URL_PATH + "/pronadji");
        builder.queryParam("ime", ime);
        ResponseEntity<StudentDto[]> response = restTemplate.getForEntity(
                builder.toUriString(), StudentDto[].class, HttpMethod.GET);
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return List.of(response.getBody());
        }
        return List.of();
    }

    /**
     * Save student synchronously (blocking).
     */
    public Integer saveStudentSync(StudentDto student) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + STUDENT_URL_PATH + "/add");
        ResponseEntity<Integer> response = restTemplate.postForEntity(
                builder.toUriString(), new HttpEntity<>(student), Integer.class);
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return response.getBody();
        }
        return null;
    }

    /**
     * Search students with pagination synchronously (blocking).
     * Use for reports generation.
     */
    public List<StudentDto> searchStudentsPagedSync(String ime, String prezime) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + API_STUDENT_PATH + "/search")
                .queryParam("page", 0)
                .queryParam("size", DEFAULT_PAGE_SIZE);
        if (ime != null && !ime.trim().isEmpty()) {
            builder.queryParam("ime", ime.trim());
        }
        if (prezime != null && !prezime.trim().isEmpty()) {
            builder.queryParam("prezime", prezime.trim());
        }
        ResponseEntity<PageResponse<StudentDto>> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageResponse<StudentDto>>() {}
        );
        PageResponse<StudentDto> body = response.getBody();
        return body == null ? List.of() : body.getContent();
    }

    /**
     * Get all students synchronously (blocking).
     * Use for reports generation.
     */
    public List<StudentDto> getAllStudentsSync() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + API_STUDENT_PATH)
                .queryParam("page", 0)
                .queryParam("size", DEFAULT_PAGE_SIZE);
        ResponseEntity<PageResponse<StudentDto>> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<PageResponse<StudentDto>>() {}
        );
        PageResponse<StudentDto> body = response.getBody();
        return body == null ? List.of() : body.getContent();
    }

    /**
     * Find student by index synchronously (blocking).
     */
    public StudentDto findStudentByIndexSync(String indeks) {
        return findStudentByIndexAsync(indeks).block();
    }

    /**
     * Find students by high school synchronously (blocking).
     * Use for reports.
     */
    public List<StudentDto> findStudentsByHighSchoolSync(Long srednjaSkolaId) {
        return findStudentsByHighSchoolAsync(srednjaSkolaId)
                .collectList()
                .blockOptional()
                .orElse(List.of());
    }

    /**
     * Search students by enrollment year synchronously (blocking).
     */
    public List<StudentDto> searchStudentsByGodinaUpisaSync(Integer godinaUpisa) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(STUDENT_URL_PATH + "/godina-upisa")
                        .queryParam("godinaUpisa", godinaUpisa)
                        .build())
                .retrieve()
                .bodyToFlux(StudentDto.class)
                .collectList()
                .block();
    }

    /**
     * Search students by study program synchronously (blocking).
     */
    public List<StudentDto> searchStudentsByStudProgSync(String studProg) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(STUDENT_URL_PATH + "/studprogram")
                        .queryParam("studProg", studProg)
                        .build())
                .retrieve()
                .bodyToFlux(StudentDto.class)
                .collectList()
                .block();
    }
}
