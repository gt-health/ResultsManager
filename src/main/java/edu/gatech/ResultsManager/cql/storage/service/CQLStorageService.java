package edu.gatech.ResultsManager.cql.storage.service;

import java.io.IOException;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
@Configuration
@ConfigurationProperties(prefix="cql.storage")
@Primary
public class CQLStorageService {
	private String endpoint;
	private RestTemplate restTemplate;
	private ObjectMapper objectMapper;
	
	public CQLStorageService() {
		restTemplate = new RestTemplate();
		objectMapper = new ObjectMapper();
	}

	public String requestCQL(String cqlName) {
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme("http").host(endpoint).port("80").path("/CQL").queryParam("name", cqlName).build();
		String responseString = restTemplate.getForEntity(uriComponents.toUriString(), String.class).getBody();
		ObjectNode responseObject = null;
		try {
			responseObject = (ObjectNode) objectMapper.readTree(responseString);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		String cqlString = responseObject.get("_embedded").get("CQLs").get(0).get("body").asText();
		return cqlString;
	}
	
	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	
	
}
