package edu.gatech.ResultsManager.fhir.identifier.service;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Patient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.TokenClientParam;

@Service
@Configuration
@ConfigurationProperties(prefix="cql.execution")
@Primary
public class PatientIdentifierService {
	private String dataServiceUri;
	private IGenericClient client;
	private FhirContext ctx;
	public PatientIdentifierService() {
		ctx = FhirContext.forDstu3();
	}
	
	public String getFhirIdByIdentifier(String identifier) throws Exception {
		client = ctx.newRestfulGenericClient(dataServiceUri);
		Bundle results = client
				.search()
				.forResource(Patient.class)
				.where(new TokenClientParam("identifier").exactly().code(identifier))
				.returnBundle(Bundle.class)
				.execute();
		if(!results.hasEntry())
			throw new Exception("Patient identifier " + identifier + "not found.");
		Patient patient = (Patient) results.getEntryFirstRep().getResource();
		return patient.getIdElement().getIdPart();
	}
	
	public String getDataServiceUri() {
		return dataServiceUri;
	}

	public void setDataServiceUri(String dataServiceUri) {
		this.dataServiceUri = dataServiceUri;
	}
	
}
