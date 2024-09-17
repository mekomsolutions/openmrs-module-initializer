package org.openmrs.module.initializer.api.c;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Concept;
import org.openmrs.ConceptReferenceRange;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.CsvLine;
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
		return Domain.CONCEPT_REFERENCE_RANGE;
	}
	
	@Override
	public Concept bootstrap(CsvLine line) throws IllegalArgumentException {
		String uuid = line.getUuid();

		ConceptReferenceRange referenceRange = conceptService.getconceptReferenceRangeByUuid(uuid);

		if (referenceRange == null) {
			referenceRange = new ConceptReferenceRange();
            if (!StringUtils.isEmpty(uuid)) {
                referenceRange.setUuid(uuid);
            }
		}
		
		return concept;
	}
	
	@Override
	public Concept save(ConceptReferenceRange instance) {
		return conceptService.saveConcept(instance);
	}
}
