package org.openmrs.module.initializer.api.fhir.cs;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.fhir2.model.FhirConceptSource;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;

@OpenmrsProfile(modules = { "fhir2:1.6.* - 9.*" })
public class FhirConceptSourceLineProcessor extends BaseLineProcessor<FhirConceptSource> {
	
	private static final String URL_HEADER = "url";
	
	public FhirConceptSourceLineProcessor() {
	}
	
	@Override
	public FhirConceptSource fill(FhirConceptSource instance, CsvLine line) throws IllegalArgumentException {
		String uuid = line.getUuid();
		
		if (StringUtils.isNotBlank(uuid)) {
			instance.setUuid(line.getUuid());
		}
		
		// The Bootstrap method of the FhirConceptSourceCsvParser should set the Concept Source.
		// If this has not happened, throw an exception
		if (instance.getConceptSource() == null) {
			throw new IllegalArgumentException("concept source is missing from FHIR concept source " + instance.getUuid());
		}
		
		String conceptSourceUrl = line.get(URL_HEADER, true);
		if (StringUtils.isBlank(conceptSourceUrl)
		        && (instance.getId() == null || !BaseLineProcessor.getVoidOrRetire(line))) {
			throw new IllegalArgumentException("FHIR concept source " + instance.getUuid() + " does not define a URL");
		}
		
		if (conceptSourceUrl != null) {
			instance.setUrl(conceptSourceUrl);
		}
		
		return instance;
	}
}
