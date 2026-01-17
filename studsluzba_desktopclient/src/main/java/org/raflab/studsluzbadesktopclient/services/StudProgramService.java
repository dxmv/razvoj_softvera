package org.raflab.studsluzbadesktopclient.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import org.raflab.studsluzba.model.dto.PredmetDto;
import org.raflab.studsluzba.model.dto.StudProgramDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class StudProgramService {

	private final RestTemplate restTemplate;
	@Autowired
	private final WebClient webClient;
	private final String baseUrl;

	private final String STUDPROGRAMI_URL_PATH = "/studprogram";
	private final String PREDMETI_URL_PATH = "/api/predmeti";

	private String createURL(String type, String pathEnd) {
		return baseUrl + STUDPROGRAMI_URL_PATH + "/" + type + "/" + pathEnd;
	}

	public List<StudProgramDto> getSudijskiProgramiSorted(){
		StudProgramDto[] retVal =
				restTemplate.getForObject(createURL("all", "sorted"), StudProgramDto[].class);
		return retVal == null ? null : Arrays.asList(retVal);
	}


	public Mono<List<PredmetDto>> getPredmetiByProgram(Long programId) {
		return webClient.get()
				.uri(uriBuilder -> uriBuilder.path(PREDMETI_URL_PATH + "/by-stud-program")
						.queryParam("studProgramId", programId).build())
				.retrieve()
				.bodyToMono(JsonNode.class)
				.map(node -> {
					List<PredmetDto> list = new ArrayList<>();
					JsonNode content = node.get("content");
					if (content != null && content.isArray()) {
						content.forEach(p -> list.add(PredmetDto.builder()
								.id(p.get("id").asLong())
								.sifra(p.get("sifra").asText())
								.naziv(p.get("naziv").asText())
								.espbBodovi(p.get("espbBodovi").asInt())
								.semestar(p.get("semestar").asInt()).build()));
					}
					return list;
				});
	}

	public Mono<Double> getAverageOcena(Long predmetId, String gOd, String gDo) {
		return webClient.get()
				.uri(uriBuilder -> uriBuilder
						.path("/api/polozeni-predmeti/averageOcena")
						.queryParam("predmetId", predmetId)
						.queryParam("godinaOd", gOd)
						.queryParam("godinaDo", gDo).build())
				.retrieve()
				.bodyToMono(Double.class);
	}

	public Mono<Void> savePredmet(PredmetDto dto) {
		return webClient.post()
				.uri(PREDMETI_URL_PATH + "/create")
				.bodyValue(dto)
				.retrieve()
				.toBodilessEntity()
				.then();
	}
}
