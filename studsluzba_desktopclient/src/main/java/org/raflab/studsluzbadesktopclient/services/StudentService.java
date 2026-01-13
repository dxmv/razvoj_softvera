package org.raflab.studsluzbadesktopclient.services;

import java.util.List;
import java.util.function.Consumer;

import lombok.AllArgsConstructor;
import org.raflab.studsluzba.model.dto.ObnovaGodineDto;
import org.raflab.studsluzba.model.dto.PolozenPredmetDto;
import org.raflab.studsluzba.model.dto.PredmetDto;
import org.raflab.studsluzba.model.dto.StudentDto;
import org.raflab.studsluzba.model.dto.UpisGodineDto;
import org.raflab.studsluzbadesktopclient.utils.PageResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
public class StudentService {
	
	private RestTemplate restTemplate;
	private WebClient webClient;
	private String baseUrl;
	
	private final String STUDENT_URL_PATH = "/student";
	private final String API_STUDENT_PATH = "/api/studenti";
    private static final int DEFAULT_PAGE_SIZE = 1000;
    private static final ParameterizedTypeReference<PageResponse<StudentDto>> STUDENT_PAGE_TYPE =
            new ParameterizedTypeReference<>() {};

    private String createURL(String pathEnd) {
		return baseUrl + STUDENT_URL_PATH + "/" + pathEnd;
	}

	public List<StudentDto> searchStudent(String ime) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(createURL("pronadji"));
		builder.queryParam("ime", ime);
		ResponseEntity<StudentDto[]> response = restTemplate.getForEntity(builder.toUriString(), StudentDto[].class, HttpMethod.GET);
		if(response.getStatusCode() == HttpStatus.OK && response.getBody() != null)
			return List.of(response.getBody());
		else return null;
	}

	public List<StudentDto> searchStudents(String ime) {
		return webClient
				.get()
				.uri(uriBuilder -> uriBuilder
						.path("student/pronadji")
						.queryParam("ime", ime)
						.build())
				.retrieve()
				.bodyToFlux(StudentDto.class)
				.collectList().block();
	}

	public Flux<StudentDto> searchStudentsAsync(String ime) {
		return fetchStudentPage(API_STUDENT_PATH + "/search", builder -> {
			if (ime != null && !ime.trim().isEmpty()) {
				builder.queryParam("ime", ime.trim());
			}
		});
	}


	public Integer saveStudent(StudentDto student) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(createURL("add"));
		ResponseEntity<Integer> response = restTemplate.postForEntity(builder.toUriString(), new HttpEntity<>(student), Integer.class);
		if(response.getStatusCode() == HttpStatus.OK && response.getBody() != null)
			return response.getBody();
		else return null;
	}

    public Flux<StudentDto> fetchAllStudentsAsync() {
		return fetchStudentPage(API_STUDENT_PATH, null);
    }

    public List<StudentDto> sviStudenti() {
		return fetchAllStudentsAsync().collectList().blockOptional().orElse(List.of());
    }

	public List<StudentDto> searchStudentsByGodinaUpisa(Integer godinaUpisa) {
		return webClient
				.get()
				.uri(uriBuilder -> uriBuilder
						.path("student/godina-upisa")
						.queryParam("godinaUpisa", godinaUpisa)
						.build())
				.retrieve()
				.bodyToFlux(StudentDto.class)
				.collectList().block();
	}

	public List<StudentDto> searchStudentsByStudProg(String studProg) {
		return webClient
				.get()
				.uri(uriBuilder -> uriBuilder
						.path("student/studprogram")
						.queryParam("studProg", studProg)
						.build())
				.retrieve()
				.bodyToFlux(StudentDto.class)
				.collectList().block();
	}

	public Mono<StudentDto> findStudentByIndexAsync(String indeks) {
		return webClient
				.get()
				.uri(uriBuilder -> uriBuilder
						.path("/api/studenti/by-index/{index}")
						.build(indeks))
				.retrieve()
				.bodyToMono(StudentDto.class);
	}

	public StudentDto findStudentByIndex(String indeks) {
		return findStudentByIndexAsync(indeks).block();
	}

	public Flux<PolozenPredmetDto> findPassedExams(String indeks) {
		return webClient
				.get()
				.uri(uriBuilder -> uriBuilder
						.path(API_STUDENT_PATH + "/by-index/{index}/passed")
						.queryParam("size", 1000)
						.build(indeks))
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<PageResponse<PolozenPredmetDto>>() {})
				.flatMapMany(page -> Flux.fromIterable(page.getContent()));
	}

	public Flux<PredmetDto> findFailedExams(String indeks) {
		return webClient
				.get()
				.uri(uriBuilder -> uriBuilder
						.path(API_STUDENT_PATH + "/by-index/{index}/failed")
						.queryParam("size", 1000)
						.build(indeks))
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<PageResponse<PredmetDto>>() {})
				.flatMapMany(page -> Flux.fromIterable(page.getContent()));
	}

	public Flux<UpisGodineDto> findEnrolledYears(String indeks) {
		return webClient
				.get()
				.uri(uriBuilder -> uriBuilder
						.path(API_STUDENT_PATH + "/by-index/{index}/enrolled")
						.build(indeks))
				.retrieve()
				.bodyToFlux(UpisGodineDto.class);
	}

	public Flux<ObnovaGodineDto> findRepeatedYears(String indeks) {
		return webClient
				.get()
				.uri(uriBuilder -> uriBuilder
						.path(API_STUDENT_PATH + "/by-index/{index}/repeated")
						.build(indeks))
				.retrieve()
				.bodyToFlux(ObnovaGodineDto.class);
	}

	private Flux<StudentDto> fetchStudentPage(String path, Consumer<UriBuilder> uriCustomizer) {
		return webClient
				.get()
				.uri(uriBuilder -> {
					UriBuilder builder = uriBuilder.path(path)
							.queryParam("page", 0)
							.queryParam("size", DEFAULT_PAGE_SIZE);
					if (uriCustomizer != null) {
						uriCustomizer.accept(builder);
					}
					return builder.build();
				})
				.retrieve()
				.bodyToMono(STUDENT_PAGE_TYPE)
				.flatMapMany(page -> Flux.fromIterable(page.getContent()));
	}
}
