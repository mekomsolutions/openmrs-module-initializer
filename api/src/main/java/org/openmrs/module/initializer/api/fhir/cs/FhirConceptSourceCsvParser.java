package org.openmrs.module.initializer.api.fhir.cs;

import org.apache.commons.lang.StringUtils;
import org.openmrs.ConceptSource;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.ConceptService;
import org.openmrs.module.fhir2.api.FhirConceptSourceService;
import org.openmrs.module.fhir2.model.FhirConceptSource;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openmrs.module.initializer.Domain.FHIR_CONCEPT_SOURCES;

@Component
@OpenmrsProfile(modules = { "fhir2:1.6.*" })
public class FhirConceptSourceCsvParser extends CsvParser<FhirConceptSource, BaseLineProcessor<FhirConceptSource>> {
	
	public static final String CONCEPT_SOURCE_HEADER = "Concept source";
	
	private final ConceptService conceptService;
	
	private final FhirConceptSourceService fhirConceptSourceService;
	
	@Autowired
	protected FhirConceptSourceCsvParser(ConceptService conceptService, FhirConceptSourceService fhirConceptSourceService,
	    BaseLineProcessor<FhirConceptSource> lineProcessor) {
		super(lineProcessor);
		this.conceptService = conceptService;
		this.fhirConceptSourceService = fhirConceptSourceService;
	}
	
	@Override
	public Domain getDomain() {
		return FHIR_CONCEPT_SOURCES;
	}
	
	@Override
	public FhirConceptSource bootstrap(CsvLine line) throws IllegalArgumentException {
		ConceptSource conceptSource;
		String ref = line.getString(CONCEPT_SOURCE_HEADER);
		
		if (StringUtils.isNotBlank(ref)) {
			conceptSource = conceptService.getConceptSourceByUuid(ref);
			if (conceptSource == null) {
				conceptSource = conceptService.getConceptSourceByName(ref);
			}
			if (conceptSource == null) {
				conceptSource = conceptService.getConceptSourceByHL7Code(ref);
			}
			if (conceptSource == null) {
				conceptSource = conceptService.getConceptSourceByUniqueId(ref);
			}
			if (conceptSource == null) {
				throw new IllegalArgumentException(
				        "'concept source '" + ref + "' not found for FHIR concept source " + line.getUuid());
			}
		} else {
			throw new IllegalArgumentException(
			        "'" + CONCEPT_SOURCE_HEADER + "' is missing from FHIR concept source CSV: " + line.getUuid());
		}
		
		for (FhirConceptSource fhirConceptSource : fhirConceptSourceService.getFhirConceptSources()) {
			if (fhirConceptSource.getConceptSource().equals(conceptSource)) {
				return fhirConceptSource;
			}
		}
		
		FhirConceptSource newConceptSource = new FhirConceptSource();
		newConceptSource.setName(conceptSource.getName());
		newConceptSource.setConceptSource(conceptSource);
		return newConceptSource;
	}
	
	@Override
	public FhirConceptSource save(FhirConceptSource instance) {
		return fhirConceptSourceService.saveFhirConceptSource(instance);
	}
}
