package org.openmrs.module.initializer.api.fhir.cs;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.ConceptSource;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.ConceptService;
import org.openmrs.module.fhir2.model.FhirConceptSource;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openmrs.module.initializer.api.fhir.cs.FhirConceptSourceCsvParser.CONCEPT_SOURCE_NAME_HEADER;

@Component
@OpenmrsProfile(modules = { "fhir2:1.*" })
public class FhirConceptSourceLineProcessor extends BaseLineProcessor<FhirConceptSource> {
	
	private static final String URL_HEADER = "url";
	
	private final ConceptService conceptService;
	
	@Autowired
	public FhirConceptSourceLineProcessor(ConceptService conceptService) {
		this.conceptService = conceptService;
	}
	
	@Override
	public FhirConceptSource fill(FhirConceptSource instance, CsvLine line) throws IllegalArgumentException {
		String uuid = line.getUuid();
		
		if (StringUtils.isNotBlank(uuid)) {
			instance.setUuid(line.getUuid());
		}
		
		String conceptSourceName = line.get(CONCEPT_SOURCE_NAME_HEADER, true);
		if (StringUtils.isBlank(conceptSourceName) && !instance.getRetired()) {
			throw new IllegalArgumentException(
			        "'concept source name' was not found for FHIR concept source " + instance.getUuid());
		}
		
		ConceptSource cs = conceptService.getConceptSourceByName(conceptSourceName);
		if (cs == null && !instance.getRetired()) {
			throw new IllegalArgumentException("Concept source " + conceptSourceName
			        + " was not found while creating FHIR concept source " + instance.getName());
		}
		
		if (cs != null) {
			instance.setConceptSource(cs);
		}
		
		String conceptSourceUrl = line.get(URL_HEADER, true);
		if (StringUtils.isBlank(conceptSourceUrl)
		        && (instance.getId() == null || !BaseLineProcessor.getVoidOrRetire(line))) {
			throw new IllegalArgumentException(
			        "FHIR concept source " + instance.getUuid() + " does not define a URL for " + conceptSourceName);
		}
		
		if (conceptSourceUrl != null) {
			instance.setUrl(conceptSourceUrl);
		}
		
		return instance;
	}
}
