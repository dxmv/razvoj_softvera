package org.raflab.studsluzbadesktopclient.services;

import java.util.Arrays;
import java.util.List;


import lombok.AllArgsConstructor;
import org.raflab.studsluzba.model.dto.StudProgramDto;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@AllArgsConstructor
public class StudProgramService {

 	private final RestTemplate restTemplate;
	private final String baseUrl;
	
	private final String STUDPROGRAMI_URL_PATH = "/studprogram";
	
	private String createURL(String type, String pathEnd) {
		return baseUrl + STUDPROGRAMI_URL_PATH + "/" + type + "/" +pathEnd;
	}
	
	public List<StudProgramDto> getSudijskiProgramiSorted(){
		StudProgramDto[] retVal =
				restTemplate.getForObject(createURL("all", "sorted"), StudProgramDto[].class);

		if (retVal == null) return null;
		return Arrays.asList(retVal);
	}
}
