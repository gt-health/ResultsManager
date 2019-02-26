package edu.gatech.ResultsManager.fhirfilter.service;

import java.io.IOException;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import gatech.edu.STIECR.JSON.ECR;

@Service
@Configuration
@ConfigurationProperties(prefix="fhir.filter")
@Primary
public class FHIRFilterService {
	private String endpoint;
	private RestTemplate restTemplate;
	private ObjectMapper objectMapper;
	
	public FHIRFilterService() {
		restTemplate = new RestTemplate();
		objectMapper = new ObjectMapper();
	}

	/**
	 * 
	 * @param rawFhir A FHIR Bundle converted into string
	 * @return A filtered version of the FHIR bundle. NOTE: must convert back to structured data if necessary
	 */
	public String applyFilter(String rawFhir) {
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme("https").host(endpoint).port("443").path("/apply").build();
		String filteredResult = restTemplate.postForEntity(uriComponents.toUriString(), rawFhir, String.class).getBody();
		return filteredResult;
	}
	
	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	
	
}
