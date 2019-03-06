package edu.gatech.ResultsManager.cql.execution.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.gatech.ResultsManager.FHIR2ECR.service.CQLFHIR2ECRService;

@Service
@Configuration
@ConfigurationProperties(prefix="cql.execution")
@Primary
public class CQLExecutionService {
	Logger log = LoggerFactory.getLogger(CQLExecutionService.class);
	
	private String endpoint;
	private String terminologyServiceUri;
	private String dataServiceUri;
	private String terminologyUser;
	private String terminologyPass;
	private RestTemplate restTemplate;
	private ObjectMapper objectMapper;
	private ObjectNode requestJson;
	
	public CQLExecutionService() {
		restTemplate = new RestTemplate();
		objectMapper = new ObjectMapper();
		requestJson = JsonNodeFactory.instance.objectNode();
	}

	public JsonNode evaluateCQL(String cqlBody, String patientId) {
		log.debug("cql body:"+cqlBody);
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme("https").host(endpoint).port("443").path("/cql/evaluate").build();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		requestJson.put("terminologyServiceUri", terminologyServiceUri);
		requestJson.put("dataServiceUri", dataServiceUri);
		requestJson.put("terminologyUser", terminologyUser);
		requestJson.put("terminologyPass", terminologyPass);
		requestJson.put("code", cqlBody);
		requestJson.put("patientId", patientId);
		HttpEntity<String> entity = new HttpEntity<String>(requestJson.toString(), headers);
		String cQLResultString = restTemplate.postForEntity(uriComponents.toUriString(), entity, String.class).getBody();
		JsonNode resultsJson = null;
		try {
			resultsJson = objectMapper.readTree(cQLResultString);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultsJson;
	}
	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getTerminologyServiceUri() {
		return terminologyServiceUri;
	}

	public void setTerminologyServiceUri(String fhirServiceUri) {
		this.terminologyServiceUri = fhirServiceUri;
	}

	public String getDataServiceUri() {
		return dataServiceUri;
	}

	public void setDataServiceUri(String dataServiceUri) {
		this.dataServiceUri = dataServiceUri;
	}

	public String getTerminologyUser() {
		return terminologyUser;
	}

	public void setTerminologyUser(String terminologyUser) {
		this.terminologyUser = terminologyUser;
	}

	public String getTerminologyPass() {
		return terminologyPass;
	}

	public void setTerminologyPass(String terminologyPass) {
		this.terminologyPass = terminologyPass;
	}
	
	
}
