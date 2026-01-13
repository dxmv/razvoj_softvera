package org.raflab.studsluzbadesktopclient.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzba.model.dto.SrednjaSkolaDto;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
public class SkoleService {

	private WebClient webClient;
	private String baseUrl;

	private static final String SKOLA_URL_PATH = "/api/srednje-skole";


	public SrednjaSkolaDto saveSrednjaSkola(SrednjaSkolaDto ss) {
		return webClient.post()
				.uri(baseUrl + SKOLA_URL_PATH)
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(ss)
				.retrieve()
				.bodyToMono(SrednjaSkolaDto.class)
				.block();
	}


	public List<SrednjaSkolaDto> getSrednjeSkole() {
		try {
			return webClient.get()
					.uri(baseUrl + SKOLA_URL_PATH)
					.accept(MediaType.APPLICATION_JSON)
					.retrieve()
					.bodyToFlux(SrednjaSkolaDto.class)
					.collectList()
					.blockOptional()
					.orElse(Collections.emptyList());
		} catch (RuntimeException ex) {
			System.out.println("Neuspešno učitavanje srednjih škola: " + ex.getMessage());
			return Collections.emptyList();
		}
	}
}
