package org.openmrs.module.initializer.api.fhir.pis;

import org.openmrs.PatientIdentifierType;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.PatientService;
import org.openmrs.module.fhir2.model.FhirPatientIdentifierSystem;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@OpenmrsProfile(modules = { "fhir2:1.*" })
public class FhirPatientIdentifierSystemLineProcessor extends BaseLineProcessor<FhirPatientIdentifierSystem> {
	
	private static final String PATIENT_IDENTIFIER_TYPE_HEADER = "Patient identifier type name";
	
	private static final String URL_HEADER = "url";
	
	private final PatientService patientService;
	
	@Autowired
	public FhirPatientIdentifierSystemLineProcessor(PatientService patientService) {
		this.patientService = patientService;
	}
	
	@Override
	public FhirPatientIdentifierSystem fill(FhirPatientIdentifierSystem instance, CsvLine line)
	        throws IllegalArgumentException {
		String patientIdentifierTypeName = line.get(PATIENT_IDENTIFIER_TYPE_HEADER, true);
		
		instance.setName(patientIdentifierTypeName);
		
		PatientIdentifierType patientIdentifierType = patientService
		        .getPatientIdentifierTypeByName(patientIdentifierTypeName);
		
		if (patientIdentifierType == null) {
			throw new IllegalStateException("Could not find a patient identifier type named " + patientIdentifierTypeName);
		}
		
		instance.setPatientIdentifierType(patientIdentifierType);
		
		String url = line.get(URL_HEADER, true);
		
		if ((instance.getId() == null || !BaseLineProcessor.getVoidOrRetire(line))
		        && (instance.getUrl() == null || instance.getUrl().isEmpty()) && (url == null || url.isEmpty())) {
			throw new IllegalStateException("URL must be supplied");
		}
		
		if (url != null && !url.isEmpty()) {
			instance.setUrl(url);
		}
		
		return instance;
	}
}
