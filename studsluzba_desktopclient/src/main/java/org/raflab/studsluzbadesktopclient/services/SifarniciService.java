package org.raflab.studsluzbadesktopclient.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzbadesktopclient.dtos.SrednjaSkolaDTO;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;

@RestController
@AllArgsConstructor
public class SifarniciService {

	private WebClient webClient;
	private String baseUrl;

	private final String SKOLA_URL_PATH = "/srednja-skola";

	private String createURL(String pathEnd) {
		return baseUrl + SKOLA_URL_PATH + "/" + pathEnd;
	}


	public Integer saveSrednjaSkola(SrednjaSkolaDTO ss) {
		return webClient.post()
				.uri(createURL("add"))
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(ss)
				.retrieve()
				.bodyToMono(Integer.class)
				.block();
	}


	public List<SrednjaSkolaDTO> getSrednjeSkole() throws Exception{

		return webClient.get()
				.uri(createURL("all"))
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToFlux(SrednjaSkolaDTO.class)
				.collectList()
				.block();
	}
}