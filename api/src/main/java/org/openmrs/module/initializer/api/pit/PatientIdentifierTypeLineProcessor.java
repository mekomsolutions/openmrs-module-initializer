package org.openmrs.module.initializer.api.pit;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.PatientService;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class PatientIdentifierTypeLineProcessor extends BaseLineProcessor<PatientIdentifierType> {
	
	public static final String HEADER_REQUIRED = "required";
	
	public static final String HEADER_FORMAT = "format";
	
	public static final String HEADER_FORMAT_DESCRIPTION = "format description";
	
	public static final String HEADER_VALIDATOR = "validator";
	
	public static final String HEADER_LOCATION_BEHAVIOR = "location behavior";
	
	public static final String HEADER_UNIQUENESS_BEHAVIOR = "uniqueness behavior";
	
	private PatientService service;
	
	@Autowired
	public PatientIdentifierTypeLineProcessor(@Qualifier("patientService") PatientService patientService) {
		super();
		this.service = patientService;
	}
	
	@Override
	public PatientIdentifierType fill(PatientIdentifierType type, CsvLine line) throws IllegalArgumentException {
		
		type.setName(line.getName(true));
		type.setDescription(line.get(HEADER_DESC));
		type.setRequired(line.getBool(HEADER_REQUIRED));
		type.setFormat(line.get(HEADER_FORMAT));
		type.setFormatDescription(line.get(HEADER_FORMAT_DESCRIPTION));
		type.setValidator(line.get(HEADER_VALIDATOR));
		{
			String locationBehaviorName = line.get(HEADER_LOCATION_BEHAVIOR);
			if (!StringUtils.isEmpty(locationBehaviorName)) {
				PatientIdentifierType.LocationBehavior locationBehavior = PatientIdentifierType.LocationBehavior
				        .valueOf(locationBehaviorName);
				type.setLocationBehavior(locationBehavior);
			}
		}
		{
			String uniquenessBehaviorName = line.get(HEADER_UNIQUENESS_BEHAVIOR);
			if (!StringUtils.isEmpty(uniquenessBehaviorName)) {
				PatientIdentifierType.UniquenessBehavior uniquenessBehavior = PatientIdentifierType.UniquenessBehavior
				        .valueOf(uniquenessBehaviorName);
				type.setUniquenessBehavior(uniquenessBehavior);
			}
		}
		
		return type;
	}
}
