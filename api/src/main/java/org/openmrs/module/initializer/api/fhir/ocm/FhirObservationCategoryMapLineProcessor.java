package org.openmrs.module.initializer.api.fhir.ocm;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.ConceptClass;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.ConceptService;
import org.openmrs.module.fhir2.model.FhirObservationCategoryMap;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.springframework.beans.factory.annotation.Autowired;

import static org.openmrs.module.initializer.api.fhir.ocm.FhirObservationCategoryMapCsvParser.CONCEPT_CLASS_HEADER;
import static org.openmrs.module.initializer.api.fhir.ocm.FhirObservationCategoryMapCsvParser.FHIR_OBS_CATEGORY_HEADER;

@OpenmrsProfile(modules = { "fhir2:1.*" })
public class FhirObservationCategoryMapLineProcessor extends BaseLineProcessor<FhirObservationCategoryMap> {
	
	private final ConceptService conceptService;
	
	@Autowired
	public FhirObservationCategoryMapLineProcessor(ConceptService conceptService) {
		this.conceptService = conceptService;
	}
	
	@Override
	public FhirObservationCategoryMap fill(FhirObservationCategoryMap instance, CsvLine line)
	        throws IllegalArgumentException {
		if (StringUtils.isBlank(instance.getUuid()) && StringUtils.isBlank(line.getUuid())) {
			throw new IllegalArgumentException("No UUID was found for FHIR observation category map");
		}
		
		String fhirObsCategory = line.get(FHIR_OBS_CATEGORY_HEADER, true);
		if (StringUtils.isBlank(fhirObsCategory) && !instance.getRetired()) {
			throw new IllegalArgumentException("'" + FHIR_OBS_CATEGORY_HEADER
			        + "' was not found for FHIR observation category map " + instance.getUuid());
		}
		
		String conceptClass = line.get(CONCEPT_CLASS_HEADER, true);
		if (StringUtils.isBlank(conceptClass) && !instance.getRetired()) {
			throw new IllegalArgumentException(
			        "'" + CONCEPT_CLASS_HEADER + "' was not found for FHIR observation category map " + instance.getUuid());
		}
		
		ConceptClass cc = conceptService.getConceptClassByName(conceptClass);
		if (cc == null && !instance.getRetired()) {
			throw new IllegalArgumentException("Concept class " + conceptClass
			        + " was not found while creating FHIR observation category map " + instance.getUuid());
		}
		
		if (StringUtils.isNotBlank(line.getUuid())) {
			instance.setUuid(line.getUuid());
		}
		
		instance.setObservationCategory(fhirObsCategory);
		instance.setConceptClass(cc);
		
		return instance;
	}
}
