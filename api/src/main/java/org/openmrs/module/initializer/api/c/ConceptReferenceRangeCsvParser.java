package org.openmrs.module.initializer.api.c;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.ConceptReferenceRange;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ConceptReferenceRangeCsvParser extends CsvParser<ConceptReferenceRange, BaseLineProcessor<ConceptReferenceRange>> {
	
	private final ConceptService conceptService;
	
	@Autowired
	public ConceptReferenceRangeCsvParser(@Qualifier("conceptService") ConceptService conceptService,
	    ConceptReferenceRangeLineProcessor processor) {
		super(processor);
		this.conceptService = conceptService;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.CONCEPT_REFERENCE_RANGES;
	}
	
	@Override
	public ConceptReferenceRange bootstrap(CsvLine line) throws IllegalArgumentException {
		String uuid = line.getUuid();
		
		ConceptReferenceRange referenceRange = conceptService.getConceptReferenceRangeByUuid(uuid);
		
		if (referenceRange == null) {
			referenceRange = new ConceptReferenceRange();
			if (!StringUtils.isEmpty(uuid)) {
				referenceRange.setUuid(uuid);
			}
		}
		
		return referenceRange;
	}
	
	@Override
	public ConceptReferenceRange save(ConceptReferenceRange instance) {
		return conceptService.saveConceptReferenceRange(instance);
	}
}
