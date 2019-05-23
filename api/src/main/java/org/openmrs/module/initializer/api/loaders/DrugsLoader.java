package org.openmrs.module.initializer.api.loaders;

import org.openmrs.module.initializer.api.drugs.DrugsCsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DrugsLoader extends BaseCsvLoader<DrugsCsvParser> {
	
	@Autowired
	public void setParser(DrugsCsvParser parser) {
		this.parser = parser;
	}
	
}
