package edu.gatech.ResultsManager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import edu.gatech.ResultsManager.FHIR2ECR.service.CQLFHIR2ECRService;
import edu.gatech.ResultsManager.cql.execution.service.CQLExecutionService;
import edu.gatech.ResultsManager.cql.storage.service.CQLStorageService;
import edu.gatech.ResultsManager.ecr.storage.service.ECRStorageService;
import gatech.edu.STIECR.JSON.ECR;

@RestController
public class ResultsManagerController {

	CQLStorageService cqlStorageService;
	CQLExecutionService cqlExecutionService;
	ECRStorageService ecrStorageService;
	CQLFHIR2ECRService cqlFhir2EcrService;
	
	@Autowired
	public ResultsManagerController(CQLStorageService cqlStorageService, CQLExecutionService cqlExecutionService,
			ECRStorageService ecrStorageService,CQLFHIR2ECRService cqlFhir2EcrService) {
		super();
		this.cqlStorageService = cqlStorageService;
		this.cqlExecutionService = cqlExecutionService;
		this.ecrStorageService = ecrStorageService;
		this.cqlFhir2EcrService = cqlFhir2EcrService;
	}
	
	@RequestMapping(value = "/PACER", method = RequestMethod.POST)
	public ResponseEntity<ECR> pacerFlow(@RequestParam(value = "firstName", required = true) String firstName,
			@RequestParam(value = "lastName", required = true) String lastName,
			@RequestParam(value = "patientId", required = true) String id,
			@RequestParam(value = "cqlType", required = true) String cqlName){
		String cqlBody = cqlStorageService.requestCQL(cqlName);
		ECR ecr = ecrStorageService.getECR(firstName, lastName);
		JsonNode cqlResults = cqlExecutionService.evaluateCQL(cqlBody,id);
		ECR ecrFromCQL = cqlFhir2EcrService.CQLFHIRResultsToECR((ArrayNode)cqlResults);
		ecr.update(ecrFromCQL);
		ecrStorageService.storeECR(ecr.toString());
		return new ResponseEntity<ECR>(ecr,HttpStatus.OK);
	}
}
