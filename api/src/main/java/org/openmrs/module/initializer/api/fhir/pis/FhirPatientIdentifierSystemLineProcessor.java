package org.openmrs.module.initializer.api.fhir.pis;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.fhir2.model.FhirPatientIdentifierSystem;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;

@OpenmrsProfile(modules = { "fhir2:1.6.* - 9.*" })
public class FhirPatientIdentifierSystemLineProcessor extends BaseLineProcessor<FhirPatientIdentifierSystem> {
	
	private static final String URL_HEADER = "url";
	
	public FhirPatientIdentifierSystemLineProcessor() {
	}
	
	@Override
	public FhirPatientIdentifierSystem fill(FhirPatientIdentifierSystem instance, CsvLine line)
	        throws IllegalArgumentException {
		
		String uuid = line.getUuid();
		
		if (StringUtils.isNotBlank(uuid)) {
			instance.setUuid(line.getUuid());
		}
		
		// The Bootstrap method of the FhirPatientIdentifierSystemCsvParser should set the Identifier Type.
		// If this has not happened, throw an exception
		if (instance.getPatientIdentifierType() == null) {
			throw new IllegalArgumentException(
			        "patient identifier type is missing from FHIR concept source " + instance.getUuid());
		}
		instance.setName(instance.getPatientIdentifierType().getName());
		
		// Require a url unless it already exists and is being retired
		String url = line.get(URL_HEADER, true);
		boolean requiresUrl = (instance.getId() == null || !BaseLineProcessor.getVoidOrRetire(line));
		if (requiresUrl && StringUtils.isBlank(url)) {
			throw new IllegalStateException("URL must be supplied");
		}
		instance.setUrl(url);
		
		return instance;
	}
}
