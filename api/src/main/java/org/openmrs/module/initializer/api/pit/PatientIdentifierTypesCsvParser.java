package org.openmrs.module.initializer.api.pit;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.PatientService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class PatientIdentifierTypesCsvParser extends CsvParser<PatientIdentifierType, BaseLineProcessor<PatientIdentifierType>> {
	
	private PatientService patientService;
	
	@Autowired
	public PatientIdentifierTypesCsvParser(@Qualifier("patientService") PatientService patientService,
	    PatientIdentifierTypeLineProcessor processor) {
		super(processor);
		this.patientService = patientService;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.PATIENT_IDENTIFIER_TYPES;
	}
	
	@Override
	public PatientIdentifierType bootstrap(CsvLine line) throws IllegalArgumentException {
		
		String uuid = line.getUuid();
		
		PatientIdentifierType type = patientService.getPatientIdentifierTypeByUuid(uuid);
		if (type == null) {
			type = patientService.getPatientIdentifierTypeByName(line.getName(true));
		}
		if (type == null) {
			type = new PatientIdentifierType();
			if (!StringUtils.isEmpty(uuid)) {
				type.setUuid(uuid);
			}
		}
		
		return type;
	}
	
	@Override
	public PatientIdentifierType save(PatientIdentifierType instance) {
		return patientService.savePatientIdentifierType(instance);
	}
}
