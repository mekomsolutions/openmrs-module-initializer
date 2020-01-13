package org.openmrs.module.initializer.api.drugs;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Drug;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
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
	public Drug bootstrap(CsvLine line) throws IllegalArgumentException {
		
		String uuid = line.getUuid();
		
		Drug drug = conceptService.getDrugByUuid(uuid);
		if (drug == null) {
			drug = conceptService.getDrug(line.getName(true));
		}
		if (drug == null) {
			drug = new Drug();
			if (!StringUtils.isEmpty(uuid)) {
				drug.setUuid(uuid);
			}
		}
		
		return drug;
	}
	
	@Override
	public Drug save(Drug instance) {
		return conceptService.saveDrug(instance);
	}
}
