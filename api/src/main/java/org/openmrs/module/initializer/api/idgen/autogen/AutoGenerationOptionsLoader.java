package org.openmrs.module.initializer.api.idgen.autogen;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.springframework.beans.factory.annotation.Autowired;

@OpenmrsProfile(modules = { "idgen:4.6.0-SNAPSHOT" })
public class AutoGenerationOptionsLoader extends BaseCsvLoader<AutoGenerationOption, AutoGenerationOptionsCsvParser> {
	
	@Autowired
	public void setParser(AutoGenerationOptionsCsvParser parser) {
		this.parser = parser;
	}
}
