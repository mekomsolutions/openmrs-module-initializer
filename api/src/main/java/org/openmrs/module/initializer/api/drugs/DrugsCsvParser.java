package org.openmrs.module.initializer.api.drugs;

import java.io.IOException;
import java.io.InputStream;

import org.openmrs.Drug;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvParser;

public class DrugsCsvParser extends CsvParser<Drug, ConceptService, BaseLineProcessor<Drug, ConceptService>> {
	
	public DrugsCsvParser(InputStream is, ConceptService cs) throws IOException {
		super(is, cs);
	}
	
	@Override
	protected void setLineProcessors(String version, String[] headerLine) {
		addLineProcessor(new BaseDrugLineProcessor(headerLine, service));
	}
	
	@Override
	protected Drug save(Drug instance) {
		return service.saveDrug(instance);
	}
	
	@Override
	protected boolean voidOrRetire(Drug instance) {
		return instance.isRetired();
	}
}
