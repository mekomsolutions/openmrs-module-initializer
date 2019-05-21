package org.openmrs.module.initializer.api.drugs;

import org.openmrs.Drug;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class DrugsCsvParser extends CsvParser<Drug, BaseLineProcessor<Drug>> {
	
	private ConceptService conceptService;
	
	@Autowired
	public DrugsCsvParser(@Qualifier("conceptService") ConceptService conceptService, DrugLineProcessor processor) {
		super(processor);
		this.conceptService = conceptService;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.DRUGS;
	}
	
	@Override
	protected Drug save(Drug instance) {
		return conceptService.saveDrug(instance);
	}
}
