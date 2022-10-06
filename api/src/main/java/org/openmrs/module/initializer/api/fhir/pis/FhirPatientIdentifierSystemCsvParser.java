package org.openmrs.module.initializer.api.fhir.pis;

import org.apache.commons.lang.StringUtils;
import org.openmrs.PatientIdentifierType;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.PatientService;
import org.openmrs.module.fhir2.api.FhirPatientIdentifierSystemService;
import org.openmrs.module.fhir2.model.FhirPatientIdentifierSystem;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.openmrs.module.initializer.Domain.FHIR_PATIENT_IDENTIFIER_SYSTEMS;

@Component
@OpenmrsProfile(modules = { "fhir2:1.6.*" })
public class FhirPatientIdentifierSystemCsvParser extends CsvParser<FhirPatientIdentifierSystem, BaseLineProcessor<FhirPatientIdentifierSystem>> {
	
	private static final String PATIENT_IDENTIFIER_TYPE_HEADER = "Patient identifier type";
	
	private final PatientService patientService;
	
	private final FhirPatientIdentifierSystemService fhirPatientIdentifierSystemService;
	
	@Autowired
	protected FhirPatientIdentifierSystemCsvParser(@Qualifier("patientService") PatientService patientService,
	    FhirPatientIdentifierSystemService fhirPatientIdentifierSystemService,
	    BaseLineProcessor<FhirPatientIdentifierSystem> lineProcessor) {
		super(lineProcessor);
		this.patientService = patientService;
		this.fhirPatientIdentifierSystemService = fhirPatientIdentifierSystemService;
	}
	
	@Override
	public Domain getDomain() {
		return FHIR_PATIENT_IDENTIFIER_SYSTEMS;
	}
	
	@Override
	public FhirPatientIdentifierSystem bootstrap(CsvLine line) throws IllegalArgumentException {
		PatientIdentifierType identifierType;
		String ref = line.getString(PATIENT_IDENTIFIER_TYPE_HEADER);
		
		if (StringUtils.isNotBlank(ref)) {
			identifierType = patientService.getPatientIdentifierTypeByUuid(ref);
			if (identifierType == null) {
				identifierType = patientService.getPatientIdentifierTypeByName(ref);
			}
			if (identifierType == null) {
				throw new IllegalArgumentException("'Patient Identifier Type '" + ref
				        + "' not found for FHIR patient identifier system " + line.getUuid());
			}
		} else {
			throw new IllegalArgumentException("'" + PATIENT_IDENTIFIER_TYPE_HEADER
			        + "' is missing from FHIR patient identifier system CSV: " + line.getUuid());
		}
		
		Optional<FhirPatientIdentifierSystem> system = fhirPatientIdentifierSystemService
		        .getFhirPatientIdentifierSystem(identifierType);
		
		if (system.isPresent()) {
			return system.get();
		}
		
		FhirPatientIdentifierSystem newSystem = new FhirPatientIdentifierSystem();
		newSystem.setName(identifierType.getName());
		newSystem.setPatientIdentifierType(identifierType);
		return newSystem;
	}
	
	@Override
	public FhirPatientIdentifierSystem save(FhirPatientIdentifierSystem instance) {
		return fhirPatientIdentifierSystemService.saveFhirPatientIdentifierSystem(instance);
	}
}
