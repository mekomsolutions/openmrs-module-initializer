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
public class DrugsCsvParser extends CsvParser<Drug, ConceptService, BaseLineProcessor<Drug, ConceptService>> {
	
	@Autowired
	public DrugsCsvParser(@Qualifier("conceptService") ConceptService cs) {
		this.service = cs;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.DRUGS;
	}
	
	@Override
	protected void setLineProcessors(String version, String[] headerLine) {
		lineProcessors.add(new DrugLineProcessor(headerLine, service));
	}
	
	@Override
	protected Drug save(Drug instance) {
		return service.saveDrug(instance);
	}
	
	@Override
	protected boolean isVoidedOrRetired(Drug instance) {
		return instance.isRetired();
	}
}
