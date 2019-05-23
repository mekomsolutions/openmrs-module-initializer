package org.openmrs.module.initializer.api.loaders;

import org.openmrs.module.initializer.api.programs.ProgramsCsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProgramsLoader extends BaseCsvLoader<ProgramsCsvParser> {
	
	@Autowired
	public void setParser(ProgramsCsvParser parser) {
		this.parser = parser;
	}
	
}
