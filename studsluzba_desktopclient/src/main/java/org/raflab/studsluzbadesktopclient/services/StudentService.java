package org.raflab.studsluzbadesktopclient.services;

import java.util.List;

import lombok.AllArgsConstructor;
import org.raflab.studsluzba.model.dto.StudentDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
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
		return webClient
				.get()
				.uri(uriBuilder -> uriBuilder
						.path("/student/pronadji")
						.queryParam("ime", ime)
						.build())
				.retrieve()
				.bodyToFlux(StudentDto.class);
	}


	public Integer saveStudent(StudentDto student) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(createURL("add"));
		ResponseEntity<Integer> response = restTemplate.postForEntity(builder.toUriString(), new HttpEntity<>(student), Integer.class);
		if(response.getStatusCode() == HttpStatus.OK && response.getBody() != null)
			return response.getBody();
		else return null;
	}

    public List<StudentDto> sviStudenti() {
		return webClient
				.get()
				.uri(uriBuilder -> uriBuilder
						.path("/student/all")
						.build())
				.retrieve()
				.bodyToFlux(StudentDto.class)
				.collectList().block();
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
						.path("/student/by-index/{index}")
						.build(indeks))
				.retrieve()
				.bodyToMono(StudentDto.class);
	}

	public StudentDto findStudentByIndex(String indeks) {
		return findStudentByIndexAsync(indeks).block();
	}
}
